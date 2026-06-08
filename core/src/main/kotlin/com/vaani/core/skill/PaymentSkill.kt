package com.vaani.core.skill

import com.vaani.core.model.AssistantResponse

/**
 * "UPI payment handoff by voice" skill (slice #7).
 *
 * Handles utterances like "రవికి 500 రూపాయలు పంపాలి" by extracting the payee and the
 * amount and producing a [com.vaani.core.model.ExecutionMode.GUIDED_ASSIST] UPI deep
 * link (`upi://pay?…`). This is the most sensitive flow: Vaani only **opens** the
 * payment screen with the amount prefilled — the actual transfer is reviewed and
 * approved by the user inside the UPI/PhonePe app. Vaani never completes a payment.
 *
 * Keyed on a numeric amount, so it never collides with the messaging skills.
 * Declines anything without an amount.
 */
class PaymentSkill : Skill {

    override fun handle(text: String): AssistantResponse? {
        TODO("GREEN (#7): detect amount + pay/currency marker, extract payee+amount, emit GUIDED upi:// DeepLink")
    }

    private companion object {
        const val TELUGU_LABEL = "చెల్లింపు"
        const val UPI_URI = "upi://pay"

        /** Currency words / pay verbs that confirm a payment intent. */
        val PAY_MARKERS = listOf("రూపాయలు", "రూపాయిలు", "రూ", "rupees", "₹", "పంపాలి", "పంపు", "చెల్లించు", "pay", "ఇవ్వు")
        /** Filler/postposition tokens to drop when isolating the payee. */
        val FILLERS = listOf("కి", "కు", "ని", "ను", "రూపాయలు", "రూపాయిలు", "రూ", "rupees", "₹")
        val POSTPOSITIONS = listOf("కి", "కు")
    }
}
