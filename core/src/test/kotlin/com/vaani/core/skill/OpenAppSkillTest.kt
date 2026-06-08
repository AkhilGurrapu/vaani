package com.vaani.core.skill

import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenAppSkillTest {

    private val skill = OpenAppSkill()

    @Test
    fun `handles a supported open-app utterance`() {
        val response = skill.handle("యూట్యూబ్ తెరువు")
        assertNotNull(response)
        assertTrue(response!!.action is AppAction.LaunchApp)
        assertEquals(ExecutionMode.DIRECT_EXECUTE, response.mode)
    }

    @Test
    fun `handles an unsupported app as guided, not declined`() {
        val response = skill.handle("ఇన్‌స్టాగ్రామ్ తెరువు")
        assertNotNull(response)
        assertTrue(response!!.action is AppAction.Unsupported)
        assertEquals(ExecutionMode.GUIDED_ASSIST, response.mode)
    }

    @Test
    fun `declines a non open-app utterance so another skill can try`() {
        assertNull(skill.handle("ఈరోజు వార్తలు చదువు"))
    }
}
