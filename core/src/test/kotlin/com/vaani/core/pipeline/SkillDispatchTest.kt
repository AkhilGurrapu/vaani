package com.vaani.core.pipeline

import com.vaani.core.model.AppAction
import com.vaani.core.model.AssistantResponse
import com.vaani.core.model.ExecutionMode
import com.vaani.core.skill.Skill
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Verifies the pipeline's dispatch contract independent of any concrete skill,
 * so new slices can rely on it: first matching skill wins, and an unhandled
 * utterance is safely guided, never executed.
 */
class SkillDispatchTest {

    private fun skillReturning(response: AssistantResponse?) = Skill { response }

    private val sampleResponse = AssistantResponse(
        action = AppAction.DeepLink("tel:123", "ఫోన్", "android.intent.action.DIAL"),
        mode = ExecutionMode.CONFIRM_THEN_EXECUTE,
        teluguSpeech = "చేస్తున్నాను",
    )

    @Test
    fun `returns the response from the first skill that handles the utterance`() {
        val pipeline = AssistantPipeline(skills = listOf(skillReturning(sampleResponse)))
        assertSame(sampleResponse, pipeline.handle("ఏదైనా"))
    }

    @Test
    fun `skips declining skills and uses the first non-null`() {
        val pipeline = AssistantPipeline(
            skills = listOf(skillReturning(null), skillReturning(sampleResponse)),
        )
        assertSame(sampleResponse, pipeline.handle("ఏదైనా"))
    }

    @Test
    fun `falls back to guided assist when no skill handles the utterance`() {
        val pipeline = AssistantPipeline(skills = listOf(skillReturning(null)))
        val response = pipeline.handle("ఏదో అర్థంకాని మాట")
        assertEquals(ExecutionMode.GUIDED_ASSIST, response.mode)
        assertEquals(AppAction.Unsupported("unrecognised"), response.action)
    }

    @Test
    fun `default registry handles the open-app utterance end to end`() {
        val response = AssistantPipeline().handle("యూట్యూబ్ తెరువు")
        assertEquals("com.google.android.youtube", (response.action as AppAction.LaunchApp).packageName)
        assertEquals(ExecutionMode.DIRECT_EXECUTE, response.mode)
    }
}
