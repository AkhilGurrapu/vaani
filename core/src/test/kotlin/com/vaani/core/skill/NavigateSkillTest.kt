package com.vaani.core.skill

import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import java.net.URLDecoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigateSkillTest {

    private val skill = NavigateSkill()

    @Test
    fun `handles a navigation command and extracts the destination`() {
        val response = skill.handle("నన్ను దగ్గరలోని హాస్పిటల్ కి తీసుకెళ్ళు")
        assertNotNull(response)
        val action = response!!.action
        assertTrue(action is AppAction.DeepLink)
        action as AppAction.DeepLink
        assertTrue(action.uri.startsWith("google.navigation:q="))
        val dest = URLDecoder.decode(action.uri.substringAfter("q="), "UTF-8")
        assertEquals("దగ్గరలోని హాస్పిటల్", dest)
    }

    @Test
    fun `starting navigation is medium-risk confirm-then-execute`() {
        val response = skill.handle("రైల్వే స్టేషన్ కి దారి చూపించు")
        assertEquals(ExecutionMode.CONFIRM_THEN_EXECUTE, response!!.mode)
    }

    @Test
    fun `targets the maps package`() {
        val response = skill.handle("బస్ స్టాండ్ కి తీసుకెళ్ళు")
        assertEquals("com.google.android.apps.maps", (response!!.action as AppAction.DeepLink).packageName)
    }

    @Test
    fun `telugu speech names the destination`() {
        val response = skill.handle("ఎయిర్‌పోర్ట్ కి తీసుకెళ్ళు")
        assertTrue(response!!.teluguSpeech.contains("ఎయిర్‌పోర్ట్"))
    }

    @Test
    fun `declines an utterance with no navigation trigger`() {
        assertNull(skill.handle("యూట్యూబ్ తెరువు"))
    }
}
