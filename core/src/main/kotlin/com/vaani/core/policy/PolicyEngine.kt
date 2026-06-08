package com.vaani.core.policy

import com.vaani.core.model.ExecutionMode
import com.vaani.core.model.ParsedIntent

/**
 * Decides the [ExecutionMode] for a recognised intent, enforcing the PRD's
 * safety model: low-risk actions execute directly, medium-risk actions are
 * confirmed in Telugu first, sensitive actions are guided.
 */
class PolicyEngine {

    fun decide(intent: ParsedIntent): ExecutionMode {
        TODO("GREEN: map intent type to execution mode")
    }
}
