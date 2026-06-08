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
        TODO("GREEN: resolve OpenApp via catalog; Unknown -> Unsupported")
    }
}
