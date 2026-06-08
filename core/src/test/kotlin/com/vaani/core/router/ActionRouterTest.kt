package com.vaani.core.router

import com.vaani.core.model.AppAction
import com.vaani.core.model.ParsedIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActionRouterTest {

    private val router = ActionRouter()

    @Test
    fun `routes resolvable open-app to LaunchApp with package`() {
        val action = router.route(ParsedIntent.OpenApp("యూట్యూబ్", "యూట్యూబ్ తెరువు"))
        assertTrue(action is AppAction.LaunchApp)
        assertEquals("com.google.android.youtube", (action as AppAction.LaunchApp).packageName)
        assertEquals("యూట్యూబ్", action.teluguLabel)
    }

    @Test
    fun `routes unknown app to Unsupported`() {
        val action = router.route(ParsedIntent.OpenApp("ఇన్‌స్టాగ్రామ్", "ఇన్‌స్టాగ్రామ్ తెరువు"))
        assertTrue(action is AppAction.Unsupported)
    }

    @Test
    fun `routes Unknown intent to Unsupported`() {
        val action = router.route(ParsedIntent.Unknown("ఏదో"))
        assertTrue(action is AppAction.Unsupported)
    }
}
