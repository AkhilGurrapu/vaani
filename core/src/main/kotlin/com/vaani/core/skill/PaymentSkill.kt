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
        val tokens = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val amountIndex = tokens.indexOfFirst { token -> token.matches(Regex("\\d+")) }
        if (amountIndex < 0) return null

        val amount = tokens[amountIndex]
        val hasPayMarker = tokens.any { token ->
            PAY_MARKERS.any { marker -> token.equals(marker, ignoreCase = true) }
        }
        if (!hasPayMarker) return null

        fun isAmountToken(index: Int) = index == amountIndex
        fun isPayMarker(token: String) = PAY_MARKERS.any { marker -> token.equals(marker, ignoreCase = true) }

        val payee = tokens.withIndex().firstNotNullOfOrNull { (index, token) ->
            if (isAmountToken(index) || isPayMarker(token)) {
                null
            } else if (POSTPOSITIONS.any { postposition -> token.equals(postposition, ignoreCase = true) }) {
                tokens.getOrNull(index - 1)
                    ?.takeUnless { previous -> isPayMarker(previous) || previous == amount }
            } else {
                POSTPOSITIONS.firstOrNull { postposition -> token.endsWith(postposition) }
                    ?.let { postposition -> token.dropLast(postposition.length) }
                    ?.takeIf { it.isNotBlank() }
            }
        } ?: return null

        val action = com.vaani.core.model.AppAction.DeepLink(
            uri = UPI_URI + "?pn=" + payee + "&am=" + amount + "&cu=INR",
            teluguLabel = TELUGU_LABEL,
            androidAction = "android.intent.action.VIEW",
            packageName = null,
        )
        val mode = com.vaani.core.model.ExecutionMode.GUIDED_ASSIST
        val teluguSpeech = payee + " కి " + amount + " రూపాయలు చెల్లించాలా? చివరి నిర్ధారణ చెల్లింపు యాప్‌లో చేయండి"

        return AssistantResponse(action, mode, teluguSpeech)
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
