package com.prorf.app.domain.rf

import com.prorf.app.engineering.Quantity
import com.prorf.app.engineering.Unit
import com.prorf.app.engineering.freeSpacePathLoss
import com.prorf.app.platform.NodeDefinition
import com.prorf.app.platform.Port
import com.prorf.app.platform.PortDirection.IN
import com.prorf.app.platform.PortDirection.OUT

/**
 * L3 · Domain Pack (ProRF) — RF node definitions.
 *
 * Executors are pure computation over [Quantity] values (§4 / §6: no logic in the
 * UI, no formulas in the platform). Definitions only depend on platform + engineering.
 */
private fun q(v: Map<String, Any?>, id: String): Quantity = v[id] as Quantity

object SignalSourceDef : NodeDefinition {
    override val typeId = "rf.source"
    override val inputs = emptyList<Port>()
    override val outputs = listOf(Port("out", OUT))
    override val parameterIds = listOf("pout", "freq")
    override fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>) =
        mapOf("out" to q(params, "pout"))
}

object AmplifierDef : NodeDefinition {
    override val typeId = "rf.amplifier"
    override val inputs = listOf(Port("in", IN))
    override val outputs = listOf(Port("out", OUT))
    override val parameterIds = listOf("gain", "nf", "p1db")
    override fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>) =
        mapOf("out" to q(inputs, "in").plus(q(params, "gain")))
}

object CouplerDef : NodeDefinition {
    override val typeId = "rf.coupler"
    override val inputs = listOf(Port("in", IN))
    override val outputs = listOf(Port("through", OUT), Port("coupled", OUT))
    override val parameterIds = listOf("insertionLoss", "coupling")
    override fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>): Map<String, Any?> {
        val input = q(inputs, "in")
        return mapOf(
            "through" to input.plus(q(params, "insertionLoss")),
            "coupled" to input.plus(q(params, "coupling")),
        )
    }
}

object AntennaDef : NodeDefinition {
    override val typeId = "rf.antenna"
    override val inputs = listOf(Port("in", IN))
    override val outputs = listOf(Port("out", OUT))
    override val parameterIds = listOf("gain", "directivity")
    override fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>) =
        mapOf("out" to q(inputs, "in").plus(q(params, "gain")))
}

object FreeSpaceDef : NodeDefinition {
    override val typeId = "rf.fspl"
    override val inputs = listOf(Port("in", IN))
    override val outputs = listOf(Port("out", OUT))
    override val parameterIds = listOf("freq", "distance")
    override fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>): Map<String, Any?> {
        val loss = freeSpacePathLoss(q(params, "distance").value, q(params, "freq").value)
        return mapOf("out" to q(inputs, "in").plus(loss))
    }
}

object ReceiverDef : NodeDefinition {
    override val typeId = "rf.receiver"
    override val inputs = listOf(Port("in", IN))
    override val outputs = listOf(Port("prx", OUT))
    override val parameterIds = listOf("nf", "sensitivity")
    override fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>) =
        mapOf("prx" to q(inputs, "in"))
}

object RfDomain {
    val definitions = listOf(
        SignalSourceDef, AmplifierDef, CouplerDef, AntennaDef, FreeSpaceDef, ReceiverDef,
    )

    /** Human-readable label + unit for a parameter id (UI declarative mapping, §4). */
    fun paramSpec(id: String): Pair<String, Unit> = when (id) {
        "pout" -> "输出功率 (Pout)" to Unit.dBm
        "freq" -> "工作频率" to Unit.GHz
        "gain" -> "增益 (Gain)" to Unit.dB
        "nf" -> "噪声系数 (NF)" to Unit.dB
        "p1db" -> "P1dB" to Unit.dBm
        "insertionLoss" -> "插入损耗" to Unit.dB
        "coupling" -> "耦合度" to Unit.dB
        "directivity" -> "方向性" to Unit.dB
        "distance" -> "距离" to Unit.km
        "sensitivity" -> "灵敏度" to Unit.dBm
        else -> id to Unit.dB
    }
}
