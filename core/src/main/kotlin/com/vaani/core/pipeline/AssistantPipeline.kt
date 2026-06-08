package com.vaani.core.pipeline

import com.vaani.core.conversation.ConversationMemory
import com.vaani.core.conversation.FollowUpResolver
import com.vaani.core.model.AppAction
import com.vaani.core.model.AssistantResponse
import com.vaani.core.model.ExecutionMode
import com.vaani.core.skill.Skill
import com.vaani.core.skill.SkillRegistry

/**
 * The end-to-end agentic core: transcribed Telugu text in, [AssistantResponse] out.
 *
 * The pipeline is a thin dispatcher over an ordered list of [Skill]s — it asks each
 * in turn and returns the first that handles the utterance. If none do, it returns a
 * safe [ExecutionMode.GUIDED_ASSIST] "didn't understand" response and never executes
 * anything. All capability-specific logic lives in the skills, so this class never
 * changes as new slices land.
 *
 * Platform layers (STT, TTS, Android Intent execution) sit on either side of this;
 * this class is pure and fully unit-testable.
 */
class AssistantPipeline(
    private val skills: List<Skill> = SkillRegistry.default(),
    private val followUpResolver: FollowUpResolver = FollowUpResolver(),
    private val memory: ConversationMemory = ConversationMemory(),
) {

    fun handle(transcribedText: String): AssistantResponse {
        // Expand anaphoric follow-ups ("అదే అమ్మకి పంపు") against the last turn first.
        val text = followUpResolver.resolve(transcribedText, memory.recall())
        for (skill in skills) {
            skill.handle(text)?.let { response ->
                response.memory?.let(memory::remember)
                return response
            }
        }
        return AssistantResponse(
            action = AppAction.Unsupported("unrecognised"),
            mode = ExecutionMode.GUIDED_ASSIST,
            teluguSpeech = DID_NOT_UNDERSTAND,
        )
    }

    private companion object {
        const val DID_NOT_UNDERSTAND = "క్షమించండి, అర్థం కాలేదు. మళ్ళీ చెప్పండి."
    }
}
