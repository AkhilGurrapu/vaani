package com.vaani.core.skill

import com.vaani.core.model.AssistantResponse

/**
 * "Navigate with Maps by voice" skill (slice #5).
 *
 * Handles utterances like "నన్ను దగ్గరలోని హాస్పిటల్ కి తీసుకెళ్ళు" by extracting the
 * destination and producing a [com.vaani.core.model.ExecutionMode.CONFIRM_THEN_EXECUTE]
 * deep link that starts turn-by-turn navigation (`google.navigation:q=<dest>`).
 * Starting navigation is medium-risk, so it is confirmed in Telugu first.
 * Declines utterances with no navigation trigger.
 */
class NavigateSkill : Skill {

    override fun handle(text: String): AssistantResponse? {
        if (NAVIGATE_TRIGGERS.none { text.contains(it, ignoreCase = true) }) return null

        val ignoredWords = NAVIGATE_TRIGGERS + FILLERS
        val destination = text
            .trim()
            .split(Regex("\\s+"))
            .filterNot { word -> ignoredWords.any { ignored -> word.equals(ignored, ignoreCase = true) } }
            .joinToString(" ")
            .trim()

        val action = com.vaani.core.model.AppAction.DeepLink(
            uri = NAVIGATION_URI + java.net.URLEncoder.encode(destination, "UTF-8"),
            teluguLabel = TELUGU_LABEL,
            androidAction = "android.intent.action.VIEW",
            packageName = MAPS_PACKAGE,
        )
        val mode = com.vaani.core.model.ExecutionMode.CONFIRM_THEN_EXECUTE
        val teluguSpeech = "$destination కి దారి చూపిస్తున్నాను"

        return AssistantResponse(action, mode, teluguSpeech)
    }

    private companion object {
        const val MAPS_PACKAGE = "com.google.android.apps.maps"
        const val TELUGU_LABEL = "మ్యాప్స్"
        const val NAVIGATION_URI = "google.navigation:q="

        /** Phrases that signal a navigation request. */
        val NAVIGATE_TRIGGERS = listOf("తీసుకెళ్ళు", "తీసుకెళ్లు", "దారి", "నావిగేట్", "navigate", "వెళ్దాం")
        /**
         * Filler/postposition tokens to drop when isolating the destination.
         * NB: keep meaningful query words like "దగ్గరలోని" (nearby) — they belong in the query.
         */
        val FILLERS = listOf("నన్ను", "కి", "కు", "లో", "మ్యాప్స్", "maps", "చూపించు", "చెయ్యి", "చేయి")
    }
}
