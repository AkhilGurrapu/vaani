package com.vaani.core.pipeline

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
) {

    fun handle(transcribedText: String): AssistantResponse {
        for (skill in skills) {
            skill.handle(transcribedText)?.let { return it }
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
