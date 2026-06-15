package com.prorf.app

import com.prorf.app.platform.Capabilities
import com.prorf.app.platform.CapabilityService
import com.prorf.app.platform.StaticCapabilityService

/**
 * L4 · App Shell — the single place that wires entitlements into the
 * [CapabilityService] (§10). Feature code must call `service.has(id)` rather than
 * checking `isProUser` directly.
 */
object AppCapabilities {
    val service: CapabilityService = StaticCapabilityService(
        granted = setOf(
            Capabilities.EXPORT_REPORT,
            Capabilities.UNLIMITED_NODES,
            // Capabilities.MONTE_CARLO intentionally NOT granted (Pro upsell).
        ),
    )
}
