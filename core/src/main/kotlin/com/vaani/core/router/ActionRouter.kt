package com.vaani.core.router

import com.vaani.core.app.AppCatalog
import com.vaani.core.model.AppAction
import com.vaani.core.model.ParsedIntent

/**
 * Selects a concrete [AppAction] for a [ParsedIntent], following the PRD's
 * path-priority order (official integration → deep link → intent → accessibility).
 *
 * For the open-app slice this resolves the spoken app name against the
 * [AppCatalog] and emits a [AppAction.LaunchApp], or [AppAction.Unsupported]
 * when the app is not in the catalog.
 */
class ActionRouter(private val appCatalog: AppCatalog = AppCatalog()) {

    fun route(intent: ParsedIntent): AppAction {
        return when (intent) {
            is ParsedIntent.OpenApp -> {
                val app = appCatalog.resolve(intent.spokenAppName)
                if (app == null) {
                    AppAction.Unsupported("unsupported_app")
                } else {
                    AppAction.LaunchApp(app.packageName, app.teluguLabel)
                }
            }
            is ParsedIntent.Unknown -> AppAction.Unsupported("unsupported_intent")
        }
    }
}
