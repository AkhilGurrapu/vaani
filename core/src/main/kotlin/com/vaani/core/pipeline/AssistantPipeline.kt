package com.vaani.core.pipeline

import com.vaani.core.intent.IntentParser
import com.vaani.core.intent.RuleBasedIntentParser
import com.vaani.core.model.AssistantResponse
import com.vaani.core.policy.PolicyEngine
import com.vaani.core.response.TeluguResponder
import com.vaani.core.router.ActionRouter

/**
 * The end-to-end agentic core: transcribed Telugu text in, [AssistantResponse] out.
 *
 *   text -> IntentParser -> PolicyEngine (mode)
 *                        -> ActionRouter (action)
 *                        -> TeluguResponder (speech)
 *
 * Platform layers (STT, TTS, Android Intent execution) sit on either side of this;
 * this class is pure and fully unit-testable.
 */
class AssistantPipeline(
    private val intentParser: IntentParser = RuleBasedIntentParser(),
    private val policyEngine: PolicyEngine = PolicyEngine(),
    private val actionRouter: ActionRouter = ActionRouter(),
    private val responder: TeluguResponder = TeluguResponder(),
) {

    fun handle(transcribedText: String): AssistantResponse {
        TODO("GREEN: orchestrate parse -> policy + route -> response")
    }
}
