package com.reshmenamma.pride.util

// ─── API Configuration ────────────────────────────────────────────────────────
// Replace with your actual Gemini API key from https://makersuite.google.com/
object Constants {

    const val GEMINI_API_KEY = "AIzaSyBwNvJmc8NHddFHvv3me-VcLkLCfSyNdHk"
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val GEMINI_MODEL = "gemini-1.5-flash"

    // ─── Instar Stage Definitions ────────────────────────────────────────────
    // Each instar stage has: ideal temp range, ideal humidity range, duration (days)
    val INSTAR_STAGES = mapOf(
        1 to InstarConfig(
            stage = 1,
            name = "Instar I (Chawki)",
            durationDays = 3,
            idealTempMin = 26f,
            idealTempMax = 28f,
            cautionTempMin = 24f,
            cautionTempMax = 30f,
            idealHumidityMin = 85,
            idealHumidityMax = 90,
            cautionHumidityMin = 80,
            cautionHumidityMax = 95,
            description = "Newly hatched larvae — most delicate stage"
        ),
        2 to InstarConfig(
            stage = 2,
            name = "Instar II",
            durationDays = 3,
            idealTempMin = 26f,
            idealTempMax = 28f,
            cautionTempMin = 24f,
            cautionTempMax = 30f,
            idealHumidityMin = 85,
            idealHumidityMax = 90,
            cautionHumidityMin = 80,
            cautionHumidityMax = 95,
            description = "Second instar — still sensitive to cold"
        ),
        3 to InstarConfig(
            stage = 3,
            name = "Instar III",
            durationDays = 4,
            idealTempMin = 25f,
            idealTempMax = 27f,
            cautionTempMin = 23f,
            cautionTempMax = 29f,
            idealHumidityMin = 80,
            idealHumidityMax = 85,
            cautionHumidityMin = 75,
            cautionHumidityMax = 90,
            description = "Rapid growth phase — monitor closely"
        ),
        4 to InstarConfig(
            stage = 4,
            name = "Instar IV",
            durationDays = 5,
            idealTempMin = 24f,
            idealTempMax = 26f,
            cautionTempMin = 22f,
            cautionTempMax = 28f,
            idealHumidityMin = 70,
            idealHumidityMax = 80,
            cautionHumidityMin = 65,
            cautionHumidityMax = 85,
            description = "High feeding — keep humidity moderate"
        ),
        5 to InstarConfig(
            stage = 5,
            name = "Instar V (Pre-spinning)",
            durationDays = 7,
            idealTempMin = 24f,
            idealTempMax = 26f,
            cautionTempMin = 22f,
            cautionTempMax = 28f,
            idealHumidityMin = 65,
            idealHumidityMax = 75,
            cautionHumidityMin = 60,
            cautionHumidityMax = 80,
            description = "Pre-cocoon stage — reduce humidity for quality silk"
        )
    )

    // Total rearing duration before harvest alert
    const val TOTAL_REARING_DAYS = 22 // Sum of all instar durations

    // Input validation ranges
    const val TEMP_MIN = 10f
    const val TEMP_MAX = 45f
    const val HUMIDITY_MIN = 10
    const val HUMIDITY_MAX = 100

    // Climate dial status
    const val STATUS_GREEN = "GREEN"
    const val STATUS_ORANGE = "ORANGE"
    const val STATUS_RED = "RED"
}

data class InstarConfig(
    val stage: Int,
    val name: String,
    val durationDays: Int,
    val idealTempMin: Float,
    val idealTempMax: Float,
    val cautionTempMin: Float,
    val cautionTempMax: Float,
    val idealHumidityMin: Int,
    val idealHumidityMax: Int,
    val cautionHumidityMin: Int,
    val cautionHumidityMax: Int,
    val description: String
)
