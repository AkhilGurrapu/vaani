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
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) {
            return ParsedIntent.Unknown(text)
        }

        val tokens = trimmedText.split(Regex("\\s+"))
        val hasOpenTrigger = tokens.any { token ->
            val normalizedToken = token.trim().trim('.', ',', '?', '!').lowercase()
            OPEN_TRIGGERS.any { trigger -> trigger.lowercase() == normalizedToken }
        }

        if (!hasOpenTrigger) {
            return ParsedIntent.Unknown(text)
        }

        val spokenAppName = tokens
            .filterNot { token ->
                val normalizedToken = token.trim().trim('.', ',', '?', '!').lowercase()
                OPEN_TRIGGERS.any { trigger -> trigger.lowercase() == normalizedToken } ||
                    FILLERS.any { filler -> filler.lowercase() == normalizedToken }
            }
            .joinToString(" ")
            .trim()

        return if (spokenAppName.isBlank()) {
            ParsedIntent.Unknown(text)
        } else {
            ParsedIntent.OpenApp(spokenAppName, text)
        }
    }

    private companion object {
        /** Telugu + transliterated verbs that signal "open this app". */
        val OPEN_TRIGGERS = listOf("తెరువు", "తెరు", "ఓపెన్", "open", "చూపించు")
        /** Filler words to drop, e.g. "చేయి" (do), "ని", "ను". */
        val FILLERS = listOf("చేయి", "చెయ్యి", "ని", "ను", "ను.", "యాప్", "app")
    }
}
