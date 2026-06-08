package com.vaani.core.app

/**
 * A known, launchable app: its Android package plus the Telugu label spoken back to the user.
 */
data class KnownApp(
    val packageName: String,
    val teluguLabel: String,
    /** Lower-cased spoken aliases (Telugu script + transliteration) that map to this app. */
    val aliases: Set<String>,
)

/**
 * Resolves a spoken app name (as the user said it, Telugu or transliterated)
 * to a [KnownApp]. Returns null when no supported app matches.
 *
 * This is the "official integration / known package" tier of the action router's
 * priority order — the POC ships a fixed catalog of high-value apps from the PRD.
 */
class AppCatalog(private val apps: List<KnownApp> = DEFAULT_APPS) {

    fun resolve(spokenAppName: String): KnownApp? {
        TODO("GREEN: normalise spokenAppName and match against aliases")
    }

    companion object {
        val DEFAULT_APPS: List<KnownApp> = listOf(
            KnownApp(
                packageName = "com.google.android.youtube",
                teluguLabel = "యూట్యూబ్",
                aliases = setOf("యూట్యూబ్", "youtube", "యూ ట్యూబ్"),
            ),
            KnownApp(
                packageName = "com.whatsapp",
                teluguLabel = "వాట్సాప్",
                aliases = setOf("వాట్సాప్", "whatsapp", "వాట్స్ యాప్"),
            ),
            KnownApp(
                packageName = "com.google.android.apps.maps",
                teluguLabel = "మ్యాప్స్",
                aliases = setOf("మ్యాప్స్", "maps", "గూగుల్ మ్యాప్స్", "google maps"),
            ),
            KnownApp(
                packageName = "com.android.dialer",
                teluguLabel = "ఫోన్",
                aliases = setOf("ఫోన్", "phone", "డయలర్", "dialer"),
            ),
        )
    }
}
