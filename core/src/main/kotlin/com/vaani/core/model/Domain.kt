package com.vaani.core.model

/**
 * The structured result of understanding a Telugu utterance.
 *
 * The intent parser converts raw transcribed Telugu text into one of these.
 * Each concrete intent carries the slots required for deterministic routing.
 */
sealed interface ParsedIntent {
    val rawText: String

    /** "open app" intent — e.g. "యూట్యూబ్ తెరువు". [spokenAppName] is the app as the user said it. */
    data class OpenApp(
        val spokenAppName: String,
        override val rawText: String,
    ) : ParsedIntent

    /** Recognised as Telugu speech but no supported intent matched. */
    data class Unknown(override val rawText: String) : ParsedIntent
}

/**
 * How the policy engine decides a recognised intent should be executed,
 * per the PRD's three execution modes.
 */
enum class ExecutionMode {
    /** Low-risk: open an app, search content. Execute immediately. */
    DIRECT_EXECUTE,

    /** Medium-risk: send a message, start navigation. Confirm in Telugu first. */
    CONFIRM_THEN_EXECUTE,

    /** Sensitive / unreliable: payments, Accessibility-driven flows. Guide the user. */
    GUIDED_ASSIST,
}

/**
 * A concrete, executable action the Android layer can carry out.
 * The core module is platform-agnostic, so this describes *what* to do,
 * not *how* (the app module turns this into an Android Intent).
 */
sealed interface AppAction {
    /** Launch an installed app by its Android package name. */
    data class LaunchApp(
        val packageName: String,
        val teluguLabel: String,
    ) : AppAction

    /**
     * A generic intent on a URI — the additive action that covers deep links,
     * app links, dial (`tel:` + ACTION_DIAL), Maps navigation (`google.navigation:`),
     * YouTube search, etc. The Android shell builds a real Intent from this spec,
     * so new skills need no new action types or executor branches.
     */
    data class DeepLink(
        val uri: String,
        val teluguLabel: String,
        val androidAction: String = "android.intent.action.VIEW",
        val packageName: String? = null,
    ) : AppAction

    /** No supported execution path exists; [reason] is a Telugu-facing explanation key. */
    data class Unsupported(val reason: String) : AppAction
}

/**
 * The full output of the assistant pipeline for one utterance:
 * the action to perform, how to perform it, and what to say back in Telugu.
 */
data class AssistantResponse(
    val action: AppAction,
    val mode: ExecutionMode,
    val teluguSpeech: String,
)
