package com.vaani.core.conversation

import com.vaani.core.model.TurnContext
import org.junit.Assert.assertEquals
import org.junit.Test

class FollowUpResolverTest {

    private val resolver = FollowUpResolver()
    private val memory = TurnContext(
        recipientName = "రవి",
        messageBody = "నేను వస్తున్నా",
        appContext = "whatsapp",
    )

    @Test
    fun `expands 'same message, new recipient' follow-up`() {
        val expanded = resolver.resolve("అదే అమ్మకి పంపు", memory)
        assertEquals("అమ్మకి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు", expanded)
    }

    @Test
    fun `expands 'send to the same person' follow-up reusing recipient and body`() {
        val expanded = resolver.resolve("అతనికే పంపు", memory)
        assertEquals("రవికి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు", expanded)
    }

    @Test
    fun `leaves a non-follow-up utterance unchanged`() {
        assertEquals("యూట్యూబ్ తెరువు", resolver.resolve("యూట్యూబ్ తెరువు", memory))
    }

    @Test
    fun `leaves text unchanged when there is no memory`() {
        assertEquals("అదే అమ్మకి పంపు", resolver.resolve("అదే అమ్మకి పంపు", null))
    }
}
