package com.vaani.core.conversation

import com.vaani.core.model.TurnContext

/**
 * Expands anaphoric Telugu follow-ups into a concrete command using the remembered
 * [TurnContext], so the existing skills can handle them unchanged.
 *
 * Examples (given a prior WhatsApp turn to రవి with body "నేను వస్తున్నా"):
 *   - "అదే అమ్మకి పంపు"  (send the same to Amma) → reuse the body, new recipient.
 *   - "అతనికే పంపు"      (send to him)           → reuse recipient + body.
 *
 * If the text has no follow-up marker, or there is no usable memory, the original
 * text is returned unchanged.
 */
class FollowUpResolver {

    fun resolve(text: String, memory: TurnContext?): String {
        val body = memory?.messageBody ?: return text
        val tokens = text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

        val sameMessage = tokens.any { it == SAME_MESSAGE }
        val samePerson = tokens.any { token -> SAME_PERSON.any { it == token } }

        return when {
            // "అదే <name>కి పంపు" — reuse the body, take the new recipient from this turn.
            sameMessage -> {
                val recipient = newRecipient(tokens) ?: memory.recipientName ?: return text
                messageCommand(recipient, body)
            }
            // "అతనికే పంపు" — reuse both the recipient and the body.
            samePerson && memory.recipientName != null -> messageCommand(memory.recipientName, body)
            else -> text
        }
    }

    /** The recipient named in this follow-up: a token bearing a కి/కు postposition. */
    private fun newRecipient(tokens: List<String>): String? = tokens.firstNotNullOfOrNull { token ->
        if (token == SAME_MESSAGE || SAME_PERSON.contains(token)) return@firstNotNullOfOrNull null
        POSTPOSITIONS.firstOrNull { token.endsWith(it) }?.let { token.dropLast(it.length) }
    }

    private fun messageCommand(recipient: String, body: String) =
        "${recipient}కి వాట్సాప్ లో $body అని మెసేజ్ పంపు"

    private companion object {
        /** "the same [message]" — reuse the remembered body. */
        const val SAME_MESSAGE = "అదే"
        /** "to the same person" — reuse the remembered recipient. */
        val SAME_PERSON = listOf("అతనికే", "ఆమెకే", "వాళ్లకే", "వాళ్ళకే")
        val POSTPOSITIONS = listOf("కి", "కు")
    }
}
