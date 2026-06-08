package com.vaani.core.skill

import com.vaani.core.contact.ContactResolver
import com.vaani.core.model.AppAction
import com.vaani.core.model.AssistantResponse
import com.vaani.core.model.ExecutionMode

/**
 * "Call a contact by voice" skill (slice #3).
 *
 * Handles utterances like "అమ్మకి కాల్ చేయి" by extracting the person name,
 * resolving it via [ContactResolver], and producing a
 * [com.vaani.core.model.ExecutionMode.CONFIRM_THEN_EXECUTE] dial deep link
 * (`tel:` + ACTION_DIAL) — the user still taps the call button, per the PRD's
 * user-confirmed safety stance. If the contact can't be resolved, it returns a
 * guided "not found" response rather than dialing. Declines non-call utterances.
 */
class CallSkill(private val contacts: ContactResolver) : Skill {

    override fun handle(text: String): AssistantResponse? {
        val tokens = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val hasCallTrigger = tokens.any { token ->
            CALL_TRIGGERS.any { trigger -> token.equals(trigger, ignoreCase = true) }
        }
        if (!hasCallTrigger) return null

        val personName = tokens
            .filterNot { token ->
                CALL_TRIGGERS.any { trigger -> token.equals(trigger, ignoreCase = true) } ||
                    FILLERS.any { filler -> token.equals(filler, ignoreCase = true) }
            }
            .map { token ->
                POSTPOSITIONS.firstOrNull { postposition -> token.endsWith(postposition) }
                    ?.let { postposition -> token.dropLast(postposition.length) }
                    ?: token
            }
            .joinToString(" ")
            .trim()

        val contact = contacts.resolve(personName)
        return if (contact != null) {
            AssistantResponse(
                action = AppAction.DeepLink(
                    uri = "tel:" + contact.phoneNumber,
                    teluguLabel = contact.displayName,
                    androidAction = DIAL_ACTION,
                    packageName = null,
                ),
                mode = ExecutionMode.CONFIRM_THEN_EXECUTE,
                teluguSpeech = contact.displayName + " కి కాల్ చేస్తున్నారా?",
            )
        } else {
            AssistantResponse(
                action = AppAction.Unsupported("contact_not_found"),
                mode = ExecutionMode.GUIDED_ASSIST,
                teluguSpeech = personName + " అనే కాంటాక్ట్ దొరకలేదు",
            )
        }
    }

    private companion object {
        const val DIAL_ACTION = "android.intent.action.DIAL"

        /** Tokens that signal a call request. */
        val CALL_TRIGGERS = listOf("కాల్", "call", "ఫోన్", "ఫోన్‌చేయి", "డయల్", "dial")
        /**
         * Filler/postposition tokens to drop when isolating the person name.
         * A trailing కి/కు postposition glued onto the name (e.g. "అమ్మకి") must
         * also be stripped to leave "అమ్మ".
         */
        val FILLERS = listOf("చేయి", "చెయ్యి", "కి", "కు", "ని", "ను")
        val POSTPOSITIONS = listOf("కి", "కు")
    }
}
