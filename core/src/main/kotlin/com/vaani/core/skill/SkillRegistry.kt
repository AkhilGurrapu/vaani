package com.vaani.core.skill

import com.vaani.core.contact.ContactResolver

/**
 * The ordered set of skills the assistant tries, most specific first.
 *
 * Order matters: [OpenAppSkill] runs first because it is keyed on explicit
 * open-verbs (తెరువు/ఓపెన్/…), which disambiguates words like "ఫోన్" that mean both
 * "the phone app" (open) and "call". Each remaining skill declines utterances it
 * doesn't own, so the first match wins.
 *
 * A new vertical slice registers its skill here with a single line — the only
 * shared edit a new capability needs.
 *
 * [contactResolver] is supplied by the platform (the Android app backs it with the
 * device address book); it defaults to a no-op so pure-core tests and the default
 * pipeline construct without platform dependencies.
 */
object SkillRegistry {
    fun default(
        contactResolver: ContactResolver = ContactResolver { null },
    ): List<Skill> = listOf(
        OpenAppSkill(),
        WhatsAppSkill(contactResolver),
        CallSkill(contactResolver),
        YouTubeSearchSkill(),
        NavigateSkill(),
    )
}
