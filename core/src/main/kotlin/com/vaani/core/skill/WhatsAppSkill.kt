package com.vaani.core.skill

import com.vaani.core.contact.ContactResolver
import com.vaani.core.model.AssistantResponse

/**
 * "Prepare a WhatsApp message by voice" skill (slice #6).
 *
 * Handles utterances like "రవికి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు" by extracting
 * the recipient and the message body, resolving the recipient via [ContactResolver],
 * and producing a [com.vaani.core.model.ExecutionMode.CONFIRM_THEN_EXECUTE] `wa.me`
 * deep link with the text **prefilled**. Vaani never sends automatically — WhatsApp
 * opens with the message ready and the user taps send (PRD safety stance).
 *
 * Declines plain "వాట్సాప్ తెరువు" (open-app owns that) and anything without a send
 * marker. If the recipient can't be resolved or the body is empty, it returns a
 * guided clarification rather than preparing a wrong message.
 */
class WhatsAppSkill(private val contacts: ContactResolver) : Skill {

    override fun handle(text: String): AssistantResponse? {
        TODO("GREEN (#6): detect whatsapp+send, extract recipient+body, resolve, emit CONFIRM wa.me DeepLink or guided")
    }

    private companion object {
        const val WHATSAPP_PACKAGE = "com.whatsapp"
        const val TELUGU_LABEL = "వాట్సాప్"
        const val WA_ME_URL = "https://wa.me/"

        /** Tokens that signal a WhatsApp context. */
        val WHATSAPP_TRIGGERS = listOf("వాట్సాప్", "whatsapp", "వాట్స్")
        /** Send/message markers that distinguish "send a message" from "open app". */
        val SEND_MARKERS = listOf("పంపు", "పంపించు", "మెసేజ్", "message", "send", "సందేశం")
        /** Quotative particle that ends the spoken message body. */
        const val QUOTATIVE = "అని"
        /** Tokens to drop when isolating recipient/body. */
        val FILLERS = listOf("లో", "కి", "కు", "ని", "ను")
        val POSTPOSITIONS = listOf("కి", "కు")
    }
}
