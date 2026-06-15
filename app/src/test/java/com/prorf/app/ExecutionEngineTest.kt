package com.prorf.app

import com.prorf.app.platform.Connection
import com.prorf.app.platform.ExecutionEngine
import com.prorf.app.platform.GraphCycleException
import com.prorf.app.platform.NodeDefinition
import com.prorf.app.platform.NodeInstance
import com.prorf.app.platform.NodeRegistry
import com.prorf.app.platform.Port
import com.prorf.app.platform.PortDirection.IN
import com.prorf.app.platform.PortDirection.OUT
import com.prorf.app.platform.WorkflowGraph
import org.junit.Assert.assertEquals
import org.junit.Test

/** L0 platform engine tests: topological execution + cycle detection (§6). */
class ExecutionEngineTest {

    private object AddOne : NodeDefinition {
        override val typeId = "test.addOne"
        override val inputs = listOf(Port("in", IN))
        override val outputs = listOf(Port("out", OUT))
        override val parameterIds = emptyList<String>()
        override fun execute(inputs: Map<String, Any?>, params: Map<String, Any?>): Map<String, Any?> {
            val v = (inputs["in"] as? Int) ?: 0
            return mapOf("out" to v + 1)
        }
    }

    @Test
    fun executesInTopologicalOrder() {
        val registry = NodeRegistry().register(AddOne)
        val graph = WorkflowGraph(
            nodes = listOf(NodeInstance("a", "test.addOne"), NodeInstance("b", "test.addOne")),
            connections = listOf(Connection("a", "out", "b", "in")),
        )
        val out = ExecutionEngine(registry).execute(graph)
        assertEquals(1, out.getValue("a").outputs["out"])
        assertEquals(2, out.getValue("b").outputs["out"])
    }

    @Test(expected = GraphCycleException::class)
    fun detectsCycles() {
        val registry = NodeRegistry().register(AddOne)
        val graph = WorkflowGraph(
            nodes = listOf(NodeInstance("a", "test.addOne"), NodeInstance("b", "test.addOne")),
            connections = listOf(Connection("a", "out", "b", "in"), Connection("b", "out", "a", "in")),
        )
        ExecutionEngine(registry).execute(graph)
    }
}
