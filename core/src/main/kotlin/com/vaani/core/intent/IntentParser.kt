package com.vaani.core.intent

import com.vaani.core.model.ParsedIntent

/**
 * Converts raw transcribed Telugu text into a [ParsedIntent].
 *
 * The POC ships a deterministic, offline rule-based implementation behind this
 * interface so the agentic pipeline is fully testable without a model or network.
 * A Gemini Nano / cloud-backed parser can be swapped in later by implementing
 * this same interface.
 */
fun interface IntentParser {
    fun parse(text: String): ParsedIntent
}
