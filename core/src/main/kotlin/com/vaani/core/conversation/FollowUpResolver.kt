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
        TODO("GREEN (#10): expand అదే / అతనికే follow-ups against memory")
    }

    private companion object {
        /** "the same [message]" — reuse the remembered body. */
        const val SAME_MESSAGE = "అదే"
        /** "to the same person" — reuse the remembered recipient. */
        val SAME_PERSON = listOf("అతనికే", "ఆమెకే", "వాళ్లకే", "వాళ్ళకే")
        val POSTPOSITIONS = listOf("కి", "కు")
    }
}
