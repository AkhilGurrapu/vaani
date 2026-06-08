package com.vaani.core.skill

import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentSkillTest {

    private val skill = PaymentSkill()

    @Test
    fun `handles a payment command and extracts amount and payee`() {
        val response = skill.handle("రవికి 500 రూపాయలు పంపాలి")
        assertNotNull(response)
        val action = response!!.action
        assertTrue(action is AppAction.DeepLink)
        action as AppAction.DeepLink
        assertTrue(action.uri.startsWith("upi://pay"))
        assertTrue(action.uri.contains("am=500"))
        assertTrue(action.uri.contains("cu=INR"))
        assertTrue("payee name should be in the deep link", action.uri.contains("రవి"))
    }

    @Test
    fun `payment is the sensitive guided-assist mode`() {
        val response = skill.handle("అమ్మకి 200 రూపాయలు పంపు")
        assertEquals(ExecutionMode.GUIDED_ASSIST, response!!.mode)
    }

    @Test
    fun `telugu speech confirms payee and amount`() {
        val response = skill.handle("రవికి 500 రూపాయలు పంపాలి")
        assertTrue(response!!.teluguSpeech.contains("రవి"))
        assertTrue(response.teluguSpeech.contains("500"))
    }

    @Test
    fun `declines an utterance with no amount`() {
        assertNull(skill.handle("రవికి వాట్సాప్ లో హాయ్ అని పంపు"))
    }

    @Test
    fun `declines a non-payment utterance`() {
        assertNull(skill.handle("యూట్యూబ్ తెరువు"))
    }
}
