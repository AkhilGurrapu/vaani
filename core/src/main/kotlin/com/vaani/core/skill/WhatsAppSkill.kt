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
        val tokens = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val hasWhatsAppTrigger = tokens.any { token ->
            WHATSAPP_TRIGGERS.any { trigger -> token.equals(trigger, ignoreCase = true) }
        }
        val hasSendMarker = tokens.any { token ->
            SEND_MARKERS.any { marker -> token.equals(marker, ignoreCase = true) }
        }
        if (!hasWhatsAppTrigger || !hasSendMarker) return null

        val recipientName = tokens.withIndex().firstNotNullOfOrNull { (index, token) ->
            val standalonePostposition = POSTPOSITIONS.any { postposition ->
                token.equals(postposition, ignoreCase = true)
            }
            if (standalonePostposition && index > 0) {
                tokens[index - 1]
            } else {
                val gluedPostposition = POSTPOSITIONS.firstOrNull { postposition -> token.endsWith(postposition) }
                val isWhatsAppTrigger = WHATSAPP_TRIGGERS.any { trigger ->
                    token.equals(trigger, ignoreCase = true)
                }
                if (gluedPostposition != null && !isWhatsAppTrigger) {
                    token.dropLast(gluedPostposition.length)
                } else {
                    null
                }
            }
        } ?: ""

        val aniIndex = tokens.indexOfFirst { token -> token == QUOTATIVE }
        val whatsappTriggerIndex = tokens.indexOfFirst { token ->
            WHATSAPP_TRIGGERS.any { trigger -> token.equals(trigger, ignoreCase = true) }
        }
        val loIndex = tokens.indexOfFirst { token -> token == "లో" }.takeIf { it >= 0 } ?: whatsappTriggerIndex
        val bodyEndIndex = if (aniIndex >= 0) {
            aniIndex
        } else {
            tokens.withIndex().firstOrNull { (index, token) ->
                index > loIndex && SEND_MARKERS.any { marker -> token.equals(marker, ignoreCase = true) }
            }?.index ?: tokens.size
        }
        val body = if (bodyEndIndex > loIndex) {
            tokens.subList(loIndex + 1, bodyEndIndex).joinToString(" ").trim()
        } else {
            ""
        }

        val contact = contacts.resolve(recipientName)
        return if (contact != null && body.isNotBlank()) {
            val digits = contact.phoneNumber.filter { it.isDigit() }
            AssistantResponse(
                action = com.vaani.core.model.AppAction.DeepLink(
                    uri = WA_ME_URL + digits + "?text=" + java.net.URLEncoder.encode(body, "UTF-8"),
                    teluguLabel = TELUGU_LABEL,
                    androidAction = "android.intent.action.VIEW",
                    packageName = WHATSAPP_PACKAGE,
                ),
                mode = com.vaani.core.model.ExecutionMode.CONFIRM_THEN_EXECUTE,
                teluguSpeech = contact.displayName + " కి '" + body + "' అని వాట్సాప్ మెసేజ్ సిద్ధం చేస్తున్నాను",
            )
        } else {
            AssistantResponse(
                action = com.vaani.core.model.AppAction.Unsupported("whatsapp_recipient_or_message_unclear"),
                mode = com.vaani.core.model.ExecutionMode.GUIDED_ASSIST,
                teluguSpeech = "ఎవరికి, ఏ మెసేజ్ పంపాలో స్పష్టంగా చెప్పండి",
            )
        }
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
