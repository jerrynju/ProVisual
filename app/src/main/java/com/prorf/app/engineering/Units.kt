package com.prorf.app.engineering

import kotlin.math.log10
import kotlin.math.roundToInt

/**
 * L1 · Engineering Foundation — quantity / unit / dimension system.
 *
 * Per ProRF Build Spec §7: every parameter is value + unit + dimension. No naked
 * doubles and no string-based unit handling are allowed to leak into the UI.
 * This layer is domain-agnostic (knows nothing about RF).
 */
enum class Dimension {
    POWER,        // dBm
    GAIN,         // dB
    ANTENNA_GAIN, // dBi
    FREQUENCY,    // Hz family
    DISTANCE,     // m family
    TEMPERATURE,  // °C / K
    DIMENSIONLESS,
}

/** A measurement unit. [isLog] marks logarithmic scales, [signed] forces a +/- prefix. */
data class Unit(
    val symbol: String,
    val dimension: Dimension,
    val isLog: Boolean = false,
    val signed: Boolean = false,
) {
    companion object {
        val dBm = Unit("dBm", Dimension.POWER, isLog = true, signed = true)
        val dB = Unit("dB", Dimension.GAIN, isLog = true, signed = true)
        val dBi = Unit("dBi", Dimension.ANTENNA_GAIN, isLog = true, signed = true)
        val GHz = Unit("GHz", Dimension.FREQUENCY)
        val MHz = Unit("MHz", Dimension.FREQUENCY)
        val km = Unit("km", Dimension.DISTANCE)
        val celsius = Unit("°C", Dimension.TEMPERATURE)
    }
}

/** An immutable value carrying its unit (and therefore its dimension). */
data class Quantity(val value: Double, val unit: Unit) {

    val dimension: Dimension get() = unit.dimension

    /** "43.0" / "+43.0" depending on the unit's [Unit.signed] flag. */
    fun formattedValue(decimals: Int = 1): String {
        val rounded = if (decimals == 0) value.roundToInt().toString()
        else String.format("%.${decimals}f", value)
        val sign = if (unit.signed && value > 0) "+" else ""
        return "$sign$rounded"
    }

    /** "+43.0 dB" */
    fun display(decimals: Int = 1): String = "${formattedValue(decimals)} ${unit.symbol}"

    /** Adds a gain/loss expressed in dB to a power/gain quantity (log-domain add). */
    fun plus(deltaDb: Quantity): Quantity {
        require(deltaDb.unit.isLog) { "delta must be a logarithmic quantity" }
        return copy(value = value + deltaDb.value)
    }

    companion object {
        fun dBm(v: Double) = Quantity(v, Unit.dBm)
        fun dB(v: Double) = Quantity(v, Unit.dB)
        fun dBi(v: Double) = Quantity(v, Unit.dBi)
        fun gHz(v: Double) = Quantity(v, Unit.GHz)
        fun km(v: Double) = Quantity(v, Unit.km)
        fun celsius(v: Double) = Quantity(v, Unit.celsius)
    }
}

/** Free-space path loss in dB. d in km, f in GHz: FSPL = 20log10(d) + 20log10(f) + 92.45. */
fun freeSpacePathLoss(distanceKm: Double, freqGHz: Double): Quantity =
    Quantity.dB(-(20 * log10(distanceKm) + 20 * log10(freqGHz) + 92.45))
