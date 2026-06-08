package com.vaani.core.pipeline

import com.vaani.core.contact.Contact
import com.vaani.core.contact.ContactResolver
import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import com.vaani.core.skill.SkillRegistry
import java.net.URLDecoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * End-to-end: a WhatsApp turn followed by a context-dependent follow-up
 * ("అదే అమ్మకి పంపు") reuses the prior message body for the new recipient.
 */
class FollowUpPipelineTest {

    private val resolver = ContactResolver { name ->
        when (name.trim()) {
            "రవి" -> Contact("రవి", "+918765432109")
            "అమ్మ" -> Contact("అమ్మ", "+919876543210")
            else -> null
        }
    }
    private val pipeline = AssistantPipeline(SkillRegistry.default(resolver))

    @Test
    fun `a follow-up reuses the previous message body for a new recipient`() {
        // First turn establishes the message body and recipient.
        val first = pipeline.handle("రవికి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు")
        assertTrue(first.action is AppAction.DeepLink)
        assertTrue((first.action as AppAction.DeepLink).uri.startsWith("https://wa.me/918765432109"))

        // Follow-up: "send the same to Amma" — no body spoken, reused from context.
        val followUp = pipeline.handle("అదే అమ్మకి పంపు")
        assertTrue(followUp.action is AppAction.DeepLink)
        val link = followUp.action as AppAction.DeepLink
        assertTrue(link.uri.startsWith("https://wa.me/919876543210?text="))
        assertEquals("com.whatsapp", link.packageName)
        assertEquals(ExecutionMode.CONFIRM_THEN_EXECUTE, followUp.mode)
        val body = URLDecoder.decode(link.uri.substringAfter("text="), "UTF-8")
        assertEquals("నేను వస్తున్నా", body)
    }

    @Test
    fun `a follow-up with no prior context is safely guided, not executed`() {
        val r = pipeline.handle("అదే అమ్మకి పంపు")
        assertEquals(ExecutionMode.GUIDED_ASSIST, r.mode)
    }
}
