package com.vaani.core.skill

import com.vaani.core.contact.Contact
import com.vaani.core.contact.ContactResolver
import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import java.net.URLDecoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsAppSkillTest {

    private val directory = mapOf(
        "రవి" to Contact("రవి", "+918765432109"),
        "అమ్మ" to Contact("అమ్మ", "+919876543210"),
    )
    private val resolver = ContactResolver { name -> directory[name.trim()] }
    private val skill = WhatsAppSkill(resolver)

    @Test
    fun `prepares a whatsapp message with recipient number and prefilled body`() {
        val response = skill.handle("రవికి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు")
        assertNotNull(response)
        val action = response!!.action
        assertTrue(action is AppAction.DeepLink)
        action as AppAction.DeepLink
        assertTrue(action.uri.startsWith("https://wa.me/918765432109?text="))
        assertEquals("com.whatsapp", action.packageName)
        val body = URLDecoder.decode(action.uri.substringAfter("text="), "UTF-8")
        assertEquals("నేను వస్తున్నా", body)
    }

    @Test
    fun `messaging is confirm-then-execute, never auto-send`() {
        val response = skill.handle("అమ్మకి వాట్సాప్ లో హాయ్ అని పంపు")
        assertEquals(ExecutionMode.CONFIRM_THEN_EXECUTE, response!!.mode)
    }

    @Test
    fun `telugu speech echoes the message body`() {
        val response = skill.handle("రవికి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు")
        assertTrue(response!!.teluguSpeech.contains("నేను వస్తున్నా"))
    }

    @Test
    fun `an unresolved recipient is guided, not prepared`() {
        val response = skill.handle("సురేష్ కి వాట్సాప్ లో హాయ్ అని పంపు")
        assertNotNull(response)
        assertEquals(ExecutionMode.GUIDED_ASSIST, response!!.mode)
        assertTrue(response.action is AppAction.Unsupported)
    }

    @Test
    fun `declines plain open-app (no send marker)`() {
        assertNull(skill.handle("వాట్సాప్ తెరువు"))
    }

    @Test
    fun `declines an utterance with no whatsapp context`() {
        assertNull(skill.handle("అమ్మకి కాల్ చేయి"))
    }
}
