package com.vaani.core.skill

import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import java.net.URLDecoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class YouTubeSearchSkillTest {

    private val skill = YouTubeSearchSkill()

    @Test
    fun `handles a youtube search command and extracts the query`() {
        val response = skill.handle("యూట్యూబ్ లో అన్నమయ్య పాటలు ప్లే చెయ్యి")
        assertNotNull(response)
        val action = response!!.action
        assertTrue(action is AppAction.DeepLink)
        action as AppAction.DeepLink
        assertTrue(action.uri.startsWith("https://www.youtube.com/results?search_query="))
        val query = URLDecoder.decode(action.uri.substringAfter("search_query="), "UTF-8")
        assertEquals("అన్నమయ్య పాటలు", query)
    }

    @Test
    fun `youtube search is low-risk direct execute`() {
        val response = skill.handle("యూట్యూబ్ లో క్రికెట్ వీడియో సెర్చ్")
        assertEquals(ExecutionMode.DIRECT_EXECUTE, response!!.mode)
    }

    @Test
    fun `telugu speech names what is being searched`() {
        val response = skill.handle("యూట్యూబ్ లో భక్తి పాటలు ప్లే చెయ్యి")
        assertTrue(response!!.teluguSpeech.contains("భక్తి పాటలు"))
    }

    @Test
    fun `declines a plain open-app utterance (no search verb)`() {
        assertNull(skill.handle("యూట్యూబ్ తెరువు"))
    }

    @Test
    fun `declines an utterance with no youtube context`() {
        assertNull(skill.handle("అమ్మకి కాల్ చేయి"))
    }
}
