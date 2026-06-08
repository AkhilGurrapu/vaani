package com.vaani.core.response

import com.vaani.core.model.AppAction

/**
 * Builds the Telugu spoken-feedback string for an action, so the assistant can
 * announce in Telugu what it is about to do (PRD: "explain what it is doing in Telugu").
 */
class TeluguResponder {

    /** e.g. LaunchApp("…","యూట్యూబ్") -> "యూట్యూబ్ తెరుస్తున్నాను" (opening YouTube). */
    fun speechFor(action: AppAction): String {
        return when (action) {
            is AppAction.LaunchApp -> "${action.teluguLabel} తెరుస్తున్నాను"
            is AppAction.Unsupported -> "క్షమించండి, ఈ పని ప్రస్తుతం చేయలేను"
        }
    }
}
