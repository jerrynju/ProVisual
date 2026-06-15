package com.prorf.app.domain.rf

import com.prorf.app.engineering.Quantity
import com.prorf.app.engineering.Unit
import com.prorf.app.platform.Connection
import com.prorf.app.platform.ExecutionEngine
import com.prorf.app.platform.NodeInstance
import com.prorf.app.platform.NodeRegistry
import com.prorf.app.platform.WorkflowGraph
import kotlin.math.log10
import kotlin.math.pow

/** A value with a display label (used for inputs / parameters / outputs). */
data class LabeledQuantity(val label: String, val quantity: Quantity)

data class Diagnostic(val message: String, val ok: Boolean)

/** Fully-computed view of one node in the executed chain. */
data class RfStage(
    val id: String,
    val typeId: String,
    val name: String,
    val code: String,
    val x: Int,
    val y: Int,
    val params: List<LabeledQuantity>,
    val inputs: List<LabeledQuantity>,
    val outputs: List<LabeledQuantity>,
    val headline: Quantity,
    val branch: List<LabeledQuantity> = emptyList(),
    val diagnostics: List<Diagnostic> = emptyList(),
)

/** Result of executing the RF link-budget workflow. */
data class RfResult(
    val stages: List<RfStage>,
    val eirp: Quantity,
    val receivedPower: Quantity,
    val systemGain: Quantity,
    val noiseFigure: Quantity,
    val sensitivity: Quantity,
    val margin: Quantity,
    /** Per-stage gain contribution for the link-gain waterfall chart. */
    val gainBars: List<Pair<String, Double>>,
)

/**
 * Builds the MVP RF link-budget workflow (§5) and executes it through the platform
 * engine (§6). The UI consumes [RfResult]; it never performs this computation itself.
 */
object RfLinkBudget {

    private data class Descriptor(
        val id: String, val typeId: String, val name: String, val code: String,
        val x: Int, val y: Int, val params: List<Pair<String, Quantity>>,
        val headlineParam: String?, // param id used as the card headline, null = use output
        val nfDb: Double? = null, val gainDb: Double,
    )

    private val descriptors = listOf(
        Descriptor("tx", "rf.source", "发射机", "TX", 24, 24,
            listOf("pout" to Quantity.dBm(20.0), "freq" to Quantity.gHz(2.4)),
            headlineParam = "pout", gainDb = 20.0),
        Descriptor("pa", "rf.amplifier", "功率放大器", "AMP", 24, 180,
            listOf("gain" to Quantity.dB(23.0), "nf" to Quantity.dB(2.0), "p1db" to Quantity.dBm(33.0)),
            headlineParam = "gain", nfDb = 2.0, gainDb = 23.0),
        Descriptor("coup", "rf.coupler", "定向耦合器", "COUP", 24, 336,
            listOf("insertionLoss" to Quantity.dB(-1.5), "coupling" to Quantity.dB(-20.0)),
            headlineParam = "insertionLoss", nfDb = 1.5, gainDb = -1.5),
        Descriptor("ant1", "rf.antenna", "发射天线", "ANT", 230, 336,
            listOf("gain" to Quantity.dBi(15.0), "directivity" to Quantity.dB(12.0)),
            headlineParam = "gain", gainDb = 15.0),
        Descriptor("fspl", "rf.fspl", "自由空间传播", "PATH", 24, 492,
            listOf("freq" to Quantity.gHz(2.4), "distance" to Quantity.km(10.0)),
            headlineParam = null, gainDb = 0.0),
        Descriptor("ant2", "rf.antenna", "接收天线", "ANT", 230, 492,
            listOf("gain" to Quantity.dBi(15.0), "directivity" to Quantity.dB(12.0)),
            headlineParam = "gain", gainDb = 15.0),
        Descriptor("lna", "rf.amplifier", "低噪声放大器", "LNA", 24, 648,
            listOf("gain" to Quantity.dB(18.0), "nf" to Quantity.dB(1.2), "p1db" to Quantity.dBm(10.0)),
            headlineParam = "gain", nfDb = 1.2, gainDb = 18.0),
        Descriptor("rx", "rf.receiver", "接收机", "RX", 130, 804,
            listOf("nf" to Quantity.dB(3.0), "sensitivity" to Quantity.dBm(-146.0)),
            headlineParam = null, nfDb = 3.0, gainDb = 0.0),
    )

    private val connections = listOf(
        Connection("tx", "out", "pa", "in"),
        Connection("pa", "out", "coup", "in"),
        Connection("coup", "through", "ant1", "in"),
        Connection("ant1", "out", "fspl", "in"),
        Connection("fspl", "out", "ant2", "in"),
        Connection("ant2", "out", "lna", "in"),
        Connection("lna", "out", "rx", "in"),
    )

    /** The serializable workflow document (§8). */
    fun graph(): WorkflowGraph = WorkflowGraph(
        nodes = descriptors.map { NodeInstance(it.id, it.typeId, it.params.toMap(), it.x, it.y) },
        connections = connections,
    )

    private fun registry(): NodeRegistry =
        NodeRegistry().apply { RfDomain.definitions.forEach { register(it) } }

    fun compute(): RfResult {
        val graph = graph()
        val outputs = ExecutionEngine(registry()).execute(graph)
        val byId = descriptors.associateBy { it.id }
        val def = RfDomain.definitions.associateBy { it.typeId }

        val stages = descriptors.map { d ->
            val nodeOut = outputs.getValue(d.id).outputs
            val incoming = connections.firstOrNull { it.toNode == d.id }
            val inputQ = incoming?.let { outputs[it.fromNode]?.outputs?.get(it.fromPort) as? Quantity }

            val paramsLabeled = d.params.map { (id, q) ->
                LabeledQuantity(RfDomain.paramSpec(id).first, q)
            }
            val inputsLabeled = inputQ?.let { listOf(LabeledQuantity("输入功率", it)) } ?: emptyList()
            val outputsLabeled = def.getValue(d.typeId).outputs.map { port ->
                LabeledQuantity(port.id, nodeOut[port.id] as Quantity)
            }
            val branch = if (d.typeId == "rf.coupler")
                listOf(LabeledQuantity("耦合端", nodeOut["coupled"] as Quantity)) else emptyList()

            // Headline: a chosen parameter, or a computed contribution.
            val headline: Quantity = when (d.typeId) {
                "rf.fspl" -> Quantity.dB((nodeOut["out"] as Quantity).value - (inputQ?.value ?: 0.0))
                "rf.receiver" -> nodeOut["prx"] as Quantity
                else -> d.headlineParam?.let { pid -> d.params.first { it.first == pid }.second }
                    ?: (nodeOut.values.first() as Quantity)
            }

            RfStage(
                id = d.id, typeId = d.typeId, name = d.name, code = d.code, x = d.x, y = d.y,
                params = paramsLabeled, inputs = inputsLabeled, outputs = outputsLabeled,
                headline = headline, branch = branch,
                diagnostics = diagnose(d, headline),
            )
        }

        val eirp = outputs.getValue("ant1").outputs["out"] as Quantity
        val received = outputs.getValue("rx").outputs["prx"] as Quantity
        val pout = byId.getValue("tx").params.first { it.first == "pout" }.second
        val systemGain = Quantity.dB(received.value - pout.value)
        val sensitivity = byId.getValue("rx").params.first { it.first == "sensitivity" }.second
        val nf = cascadeNoiseFigure(listOf(byId.getValue("lna"), byId.getValue("rx")))
        val margin = Quantity.dB(received.value - sensitivity.value)

        // Waterfall: per-stage contribution; final stage shows cumulative received power.
        val bars = stages.map { s ->
            val d = byId.getValue(s.id)
            val v = if (d.typeId == "rf.fspl")
                (outputs.getValue("fspl").outputs["out"] as Quantity).value - eirp.value
            else if (d.typeId == "rf.receiver") received.value
            else d.gainDb
            s.code to v
        }

        return RfResult(stages, eirp, received, systemGain, nf, sensitivity, margin, bars)
    }

    private fun diagnose(d: Descriptor, headline: Quantity): List<Diagnostic> {
        val list = mutableListOf(Diagnostic("计算有效", true))
        d.params.firstOrNull { it.first == "freq" }?.let {
            list.add(Diagnostic("工作频率在器件支持范围内", true))
        }
        d.nfDb?.let { list.add(Diagnostic("噪声系数 ${it} dB 已纳入级联计算", true)) }
        return list
    }

    /** Friis cascade noise figure for a receive chain of (gain dB, NF dB) stages. */
    private fun cascadeNoiseFigure(stages: List<Descriptor>): Quantity {
        var fTotal = 0.0
        var gainProduct = 1.0
        stages.forEachIndexed { i, s ->
            val f = 10.0.pow((s.nfDb ?: 0.0) / 10.0)
            fTotal += if (i == 0) f else (f - 1) / gainProduct
            gainProduct *= 10.0.pow(s.gainDb / 10.0)
        }
        return Quantity(10 * log10(fTotal), Unit.dB)
    }
}
