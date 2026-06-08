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
        val tokens = text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val hasYouTubeTrigger = tokens.any { token ->
            YOUTUBE_TRIGGERS.any { it.equals(token, ignoreCase = true) }
        }
        val hasSearchVerb = tokens.any { token ->
            SEARCH_VERBS.any { it.equals(token, ignoreCase = true) }
        }

        if (!hasYouTubeTrigger || !hasSearchVerb) return null

        val droppedTokens = YOUTUBE_TRIGGERS + SEARCH_VERBS + FILLERS
        val query = tokens
            .filterNot { token -> droppedTokens.any { it.equals(token, ignoreCase = true) } }
            .joinToString(" ")
            .trim()
        val action = com.vaani.core.model.AppAction.DeepLink(
            uri = RESULTS_URL + java.net.URLEncoder.encode(query, "UTF-8"),
            teluguLabel = TELUGU_LABEL,
            androidAction = "android.intent.action.VIEW",
            packageName = null,
        )

        return AssistantResponse(
            action = action,
            mode = com.vaani.core.model.ExecutionMode.DIRECT_EXECUTE,
            teluguSpeech = "యూట్యూబ్ లో $query వెతుకుతున్నాను",
        )
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
