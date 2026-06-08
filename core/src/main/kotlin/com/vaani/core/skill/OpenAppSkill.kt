package com.vaani.core.skill

import com.vaani.core.app.AppCatalog
import com.vaani.core.intent.IntentParser
import com.vaani.core.intent.RuleBasedIntentParser
import com.vaani.core.model.AppAction
import com.vaani.core.model.AssistantResponse
import com.vaani.core.model.ExecutionMode
import com.vaani.core.model.ParsedIntent
import com.vaani.core.policy.PolicyEngine
import com.vaani.core.response.TeluguResponder
import com.vaani.core.router.ActionRouter

/**
 * The "open app by voice" skill — wraps the slice-1 components behind the [Skill]
 * seam without changing their behaviour. Handles any utterance the parser reads as
 * an open-app intent (even when the app is unsupported, so the user is guided rather
 * than silently ignored); declines everything else.
 */
class OpenAppSkill(
    private val parser: IntentParser = RuleBasedIntentParser(),
    catalog: AppCatalog = AppCatalog(),
    private val policy: PolicyEngine = PolicyEngine(),
    private val router: ActionRouter = ActionRouter(catalog),
    private val responder: TeluguResponder = TeluguResponder(),
) : Skill {

    override fun handle(text: String): AssistantResponse? {
        val intent = parser.parse(text)
        if (intent !is ParsedIntent.OpenApp) return null

        val action = router.route(intent)
        // An unsupported app is recognised intent but no path → guide, never execute.
        val mode = if (action is AppAction.Unsupported) {
            ExecutionMode.GUIDED_ASSIST
        } else {
            policy.decide(intent)
        }
        return AssistantResponse(action, mode, responder.speechFor(action))
    }
}
