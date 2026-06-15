package com.prorf.app.platform

/**
 * L0 · Platform Core — capability gating (§10).
 *
 * All product gating must flow through [CapabilityService.has]; the spec forbids
 * ad-hoc `if (isProUser)` checks scattered through the code.
 */
object Capabilities {
    const val EXPORT_REPORT = "report.export"
    const val MONTE_CARLO = "analysis.monteCarlo"
    const val UNLIMITED_NODES = "workflow.unlimitedNodes"
}

interface CapabilityService {
    fun has(capabilityId: String): Boolean
}

/** Simple in-memory capability set; an entitlement backend can replace this later. */
class StaticCapabilityService(private val granted: Set<String>) : CapabilityService {
    override fun has(capabilityId: String): Boolean = capabilityId in granted
}
