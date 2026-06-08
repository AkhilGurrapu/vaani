package com.vaani.core.conversation

import com.vaani.core.model.TurnContext

/**
 * Short-lived memory of the last meaningful turn, so follow-up utterances can refer
 * back to it. The POC retains only the single most recent [TurnContext]; it is
 * cleared on demand (e.g. once a flow completes) and is not persisted across sessions.
 */
class ConversationMemory {
    private var last: TurnContext? = null

    fun remember(context: TurnContext) {
        last = context
    }

    fun recall(): TurnContext? = last

    fun clear() {
        last = null
    }
}
