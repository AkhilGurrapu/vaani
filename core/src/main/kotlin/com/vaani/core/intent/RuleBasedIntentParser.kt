package com.vaani.core.intent

import com.vaani.core.model.ParsedIntent

/**
 * Deterministic Telugu intent parser for the "open app" slice.
 *
 * Recognises open-app phrasings such as:
 *   - "యూట్యూబ్ తెరువు"      (youtube open)
 *   - "వాట్సాప్ ఓపెన్ చేయి"  (whatsapp open do)
 *   - "మ్యాప్స్ తెరువు"
 *
 * The open-app trigger words are stripped to leave the spoken app name.
 */
class RuleBasedIntentParser : IntentParser {

    override fun parse(text: String): ParsedIntent {
        TODO("GREEN: implement rule-based open-app parsing")
    }

    private companion object {
        /** Telugu + transliterated verbs that signal "open this app". */
        val OPEN_TRIGGERS = listOf("తెరువు", "తెరు", "ఓపెన్", "open", "చూపించు")
        /** Filler words to drop, e.g. "చేయి" (do), "ని", "ను". */
        val FILLERS = listOf("చేయి", "చెయ్యి", "ని", "ను", "ను.", "యాప్", "app")
    }
}
