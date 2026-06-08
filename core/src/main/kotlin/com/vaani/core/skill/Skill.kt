package com.vaani.core.skill

import com.vaani.core.model.AssistantResponse

/**
 * A single voice capability ("skill").
 *
 * Given transcribed Telugu, a skill either **fully handles** the utterance —
 * returning a complete [AssistantResponse] (action + safety mode + Telugu speech) —
 * or **declines** by returning `null`, letting the next skill in the registry try.
 *
 * Each skill owns its own trigger matching, slot extraction, safety mode, action
 * routing, and Telugu response. Adding a new capability (call, search, navigate, …)
 * is therefore a new [Skill] class plus its registration — never an edit to shared
 * intent/policy/router/responder logic. This is what makes vertical slices additive
 * and independently buildable.
 */
fun interface Skill {
    fun handle(text: String): AssistantResponse?
}
