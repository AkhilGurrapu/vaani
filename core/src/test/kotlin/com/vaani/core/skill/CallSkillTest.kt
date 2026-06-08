package com.vaani.core.skill

import com.vaani.core.contact.Contact
import com.vaani.core.contact.ContactResolver
import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CallSkillTest {

    private val directory = mapOf(
        "అమ్మ" to Contact("అమ్మ", "+919876543210"),
        "రవి" to Contact("రవి", "+918765432109"),
    )
    private val fakeResolver = ContactResolver { name -> directory[name.trim()] }
    private val skill = CallSkill(fakeResolver)

    @Test
    fun `handles a call command, strips the postposition, and dials the contact`() {
        val response = skill.handle("అమ్మకి కాల్ చేయి")
        assertNotNull(response)
        val action = response!!.action
        assertTrue(action is AppAction.DeepLink)
        action as AppAction.DeepLink
        assertEquals("tel:+919876543210", action.uri)
        assertEquals("android.intent.action.DIAL", action.androidAction)
    }

    @Test
    fun `calling a contact is medium-risk confirm-then-execute`() {
        val response = skill.handle("రవి కి కాల్ చేయి")
        assertEquals(ExecutionMode.CONFIRM_THEN_EXECUTE, response!!.mode)
    }

    @Test
    fun `telugu speech names the person being called`() {
        val response = skill.handle("అమ్మకి కాల్ చేయి")
        assertTrue(response!!.teluguSpeech.contains("అమ్మ"))
    }

    @Test
    fun `an unresolved contact is guided, not dialed`() {
        val response = skill.handle("సురేష్ కి కాల్ చేయి")
        assertNotNull(response)
        assertEquals(ExecutionMode.GUIDED_ASSIST, response!!.mode)
        assertTrue(response.action is AppAction.Unsupported)
    }

    @Test
    fun `declines a non-call utterance`() {
        assertNull(skill.handle("యూట్యూబ్ తెరువు"))
    }
}
