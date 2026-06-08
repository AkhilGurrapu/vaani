package com.vaani.core.skill

import com.vaani.core.model.AssistantResponse

/**
 * "Search YouTube by voice" skill (slice #4).
 *
 * Handles utterances like "యూట్యూబ్ లో అన్నమయ్య పాటలు ప్లే చెయ్యి" by extracting the
 * search query and producing a low-risk [com.vaani.core.model.ExecutionMode.DIRECT_EXECUTE]
 * deep link to the YouTube results page. Declines utterances that don't mention
 * YouTube together with a search/play verb (e.g. plain "యూట్యూబ్ తెరువు", which the
 * open-app skill owns).
 */
class YouTubeSearchSkill : Skill {

    override fun handle(text: String): AssistantResponse? {
        TODO("GREEN (#4): detect youtube + search/play verb, extract query, emit DeepLink")
    }

    private companion object {
        const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        const val TELUGU_LABEL = "యూట్యూబ్"
        const val RESULTS_URL = "https://www.youtube.com/results?search_query="

        /** Tokens that signal a YouTube context. */
        val YOUTUBE_TRIGGERS = listOf("యూట్యూబ్", "youtube")
        /** Search/play verbs that distinguish a search from a plain "open app". */
        val SEARCH_VERBS = listOf("ప్లే", "play", "వెతుకు", "సెర్చ్", "search", "పెట్టు", "వినిపించు")
        /** Filler tokens to drop when isolating the query. */
        val FILLERS = listOf("లో", "చెయ్యి", "చేయి", "ని", "ను")
    }
}
