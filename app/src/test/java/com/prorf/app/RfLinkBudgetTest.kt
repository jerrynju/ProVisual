package com.prorf.app

import com.prorf.app.domain.rf.RfLinkBudget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Self-verification (§4 / §11): the workflow graph executes and produces a
 * non-empty, numerically valid link budget.
 */
class RfLinkBudgetTest {

    @Test
    fun graphExecutesToNonEmptyResult() {
        val result = RfLinkBudget.compute()
        assertEquals("expected 8 stages in the MVP chain", 8, result.stages.size)
        assertTrue("every stage must produce at least one output",
            result.stages.all { it.outputs.isNotEmpty() })
    }

    @Test
    fun eirpAndReceivedPowerMatchCascade() {
        val result = RfLinkBudget.compute()
        // 20 (Pout) + 23 (PA) - 1.5 (coupler) + 15 (antenna) = 56.5 dBm
        assertEquals(56.5, result.eirp.value, 0.01)
        // 56.5 - FSPL(10km, 2.4GHz ≈ 120.05) + 15 + 18 ≈ -30.55 dBm
        assertEquals(-30.55, result.receivedPower.value, 0.2)
        // Link margin must be positive for the chain to close.
        assertTrue("link margin should be positive", result.margin.value > 0)
    }
}
