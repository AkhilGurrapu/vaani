package com.vaani.core.policy

import com.vaani.core.model.ExecutionMode
import com.vaani.core.model.ParsedIntent
import org.junit.Assert.assertEquals
import org.junit.Test

class PolicyEngineTest {

    private val policy = PolicyEngine()

    @Test
    fun `open app is low-risk direct execute`() {
        val mode = policy.decide(ParsedIntent.OpenApp("యూట్యూబ్", "యూట్యూబ్ తెరువు"))
        assertEquals(ExecutionMode.DIRECT_EXECUTE, mode)
    }

    @Test
    fun `unknown intent is guided assist`() {
        val mode = policy.decide(ParsedIntent.Unknown("ఏదో ఒకటి"))
        assertEquals(ExecutionMode.GUIDED_ASSIST, mode)
    }
}
