package com.vaani.core.skill

/**
 * The ordered set of skills the assistant tries, most specific first.
 *
 * Each vertical slice registers its skill here with a single line — the only
 * shared edit a new capability needs. Everything else about a slice lives in its
 * own [Skill] class.
 */
object SkillRegistry {
    fun default(): List<Skill> = listOf(
        OpenAppSkill(),
        // ↓ new skills register here per slice (call #3, youtube #4, navigate #5, …)
    )
}
