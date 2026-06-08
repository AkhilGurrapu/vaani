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
        TODO("GREEN (#5): detect navigate trigger, extract destination, emit CONFIRM DeepLink")
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
