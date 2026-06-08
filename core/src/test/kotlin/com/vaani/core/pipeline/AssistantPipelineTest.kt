package com.vaani.core.pipeline

import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * End-to-end tracer-bullet test for the "open app" vertical slice:
 * transcribed Telugu text in -> full AssistantResponse out.
 */
class AssistantPipelineTest {

    private val pipeline = AssistantPipeline()

    @Test
    fun `open youtube command produces launch action, direct-execute mode and telugu speech`() {
        val response = pipeline.handle("యూట్యూబ్ తెరువు")

        assertTrue(response.action is AppAction.LaunchApp)
        assertEquals("com.google.android.youtube", (response.action as AppAction.LaunchApp).packageName)
        assertEquals(ExecutionMode.DIRECT_EXECUTE, response.mode)
        assertTrue(
            "telugu speech should mention opening",
            response.teluguSpeech.contains("తెరుస్తున్నాను"),
        )
        assertTrue(response.teluguSpeech.contains("యూట్యూబ్"))
    }

    @Test
    fun `unsupported app is guided, not direct executed`() {
        val response = pipeline.handle("ఇన్‌స్టాగ్రామ్ తెరువు")

        assertTrue(response.action is AppAction.Unsupported)
        assertEquals(ExecutionMode.GUIDED_ASSIST, response.mode)
    }

    @Test
    fun `unrecognised command is guided assist`() {
        val response = pipeline.handle("ఈరోజు వార్తలు చదువు")
        assertEquals(ExecutionMode.GUIDED_ASSIST, response.mode)
    }
}
