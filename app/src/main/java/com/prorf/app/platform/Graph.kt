package com.prorf.app.platform

/**
 * L0 · Platform Core — generic workflow graph + execution engine.
 *
 * Per ProRF Build Spec §1: this layer must NOT contain any RF concept, engineering
 * formula or UI logic. It only knows nodes, ports, edges and how to execute a DAG.
 * Values flowing through ports are opaque ([Any]); the platform never inspects them.
 */
enum class PortDirection { IN, OUT }

data class Port(val id: String, val direction: PortDirection)

/** Static schema of a node type: its ports and the ids of its parameters. */
interface NodeDefinition {
    val typeId: String
    val inputs: List<Port>
    val outputs: List<Port>
    val parameterIds: List<String>

    /** Pure computation: given input-port values and parameters, produce output-port values. */
    fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>): Map<String, Any?>
}

/** A placed instance of a node type: parameters + canvas position only (§4). */
data class NodeInstance(
    val id: String,
    val typeId: String,
    val params: Map<String, Any?> = emptyMap(),
    val x: Int = 0,
    val y: Int = 0,
)

data class Connection(
    val fromNode: String,
    val fromPort: String,
    val toNode: String,
    val toPort: String,
)

/** Serializable workflow document (§8). */
data class WorkflowGraph(
    val nodes: List<NodeInstance>,
    val connections: List<Connection>,
    val schemaVersion: Int = 1,
)

/** Per-instance result produced by the engine. */
data class NodeOutput(val nodeId: String, val outputs: Map<String, Any?>)

class GraphCycleException(message: String) : IllegalStateException(message)

/** Registry of node definitions, keyed by typeId (§1 plugin registry). */
class NodeRegistry {
    private val defs = mutableMapOf<String, NodeDefinition>()
    fun register(def: NodeDefinition): NodeRegistry = apply { defs[def.typeId] = def }
    fun definition(typeId: String): NodeDefinition =
        defs[typeId] ?: error("No node definition registered for type '$typeId'")
}

/**
 * Executes a workflow graph: topological sort -> per-node pure execution, with a
 * simple output cache keyed by node id (§6: DAG execution, cached node output).
 */
class ExecutionEngine(private val registry: NodeRegistry) {

    fun execute(graph: WorkflowGraph): Map<String, NodeOutput> {
        val order = topologicalOrder(graph)
        val cache = LinkedHashMap<String, NodeOutput>()
        val nodeById = graph.nodes.associateBy { it.id }

        for (nodeId in order) {
            val instance = nodeById.getValue(nodeId)
            val def = registry.definition(instance.typeId)
            // Gather inputs from upstream outputs via connections.
            val inputs = mutableMapOf<String, Any?>()
            graph.connections.filter { it.toNode == nodeId }.forEach { c ->
                inputs[c.toPort] = cache[c.fromNode]?.outputs?.get(c.fromPort)
            }
            val outputs = def.execute(inputs, instance.params)
            cache[nodeId] = NodeOutput(nodeId, outputs)
        }
        return cache
    }

    private fun topologicalOrder(graph: WorkflowGraph): List<String> {
        val ids = graph.nodes.map { it.id }
        val indegree = ids.associateWith { 0 }.toMutableMap()
        val adj = ids.associateWith { mutableListOf<String>() }
        graph.connections.forEach { c ->
            adj.getValue(c.fromNode).add(c.toNode)
            indegree[c.toNode] = indegree.getValue(c.toNode) + 1
        }
        val queue = ArrayDeque(ids.filter { indegree.getValue(it) == 0 })
        val order = mutableListOf<String>()
        while (queue.isNotEmpty()) {
            val n = queue.removeFirst()
            order.add(n)
            adj.getValue(n).forEach { m ->
                indegree[m] = indegree.getValue(m) - 1
                if (indegree.getValue(m) == 0) queue.add(m)
            }
        }
        if (order.size != ids.size) throw GraphCycleException("Workflow graph contains a cycle")
        return order
    }
}
