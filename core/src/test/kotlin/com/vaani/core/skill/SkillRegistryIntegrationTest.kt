package com.vaani.core.skill

import com.vaani.core.contact.Contact
import com.vaani.core.contact.ContactResolver
import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import com.vaani.core.pipeline.AssistantPipeline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies the wired default registry routes each Milestone-1 capability to the
 * right skill, including the ఫోన్ open-vs-call disambiguation handled by ordering.
 */
class SkillRegistryIntegrationTest {

    private val resolver = ContactResolver { name ->
        when (name.trim()) {
            "అమ్మ" -> Contact("అమ్మ", "+919876543210")
            "రవి" -> Contact("రవి", "+918765432109")
            else -> null
        }
    }
    private val pipeline = AssistantPipeline(SkillRegistry.default(resolver))

    @Test
    fun `routes open-app to a launch`() {
        val r = pipeline.handle("యూట్యూబ్ తెరువు")
        assertTrue(r.action is AppAction.LaunchApp)
        assertEquals("com.google.android.youtube", (r.action as AppAction.LaunchApp).packageName)
    }

    @Test
    fun `routes phone-app open to launch, not to a call`() {
        val r = pipeline.handle("ఫోన్ తెరువు")
        assertTrue(r.action is AppAction.LaunchApp)
        assertEquals(ExecutionMode.DIRECT_EXECUTE, r.mode)
    }

    @Test
    fun `routes a call to a confirm dial`() {
        val r = pipeline.handle("అమ్మకి కాల్ చేయి")
        assertTrue(r.action is AppAction.DeepLink)
        assertEquals("tel:+919876543210", (r.action as AppAction.DeepLink).uri)
        assertEquals(ExecutionMode.CONFIRM_THEN_EXECUTE, r.mode)
    }

    @Test
    fun `routes a youtube search to direct execute`() {
        val r = pipeline.handle("యూట్యూబ్ లో పాటలు ప్లే చెయ్యి")
        assertTrue(r.action is AppAction.DeepLink)
        assertTrue((r.action as AppAction.DeepLink).uri.startsWith("https://www.youtube.com/results"))
        assertEquals(ExecutionMode.DIRECT_EXECUTE, r.mode)
    }

    @Test
    fun `routes a whatsapp message to a confirm prefilled prepare`() {
        val r = pipeline.handle("రవికి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు")
        assertTrue(r.action is AppAction.DeepLink)
        val link = r.action as AppAction.DeepLink
        assertTrue(link.uri.startsWith("https://wa.me/918765432109?text="))
        assertEquals("com.whatsapp", link.packageName)
        assertEquals(ExecutionMode.CONFIRM_THEN_EXECUTE, r.mode)
    }

    @Test
    fun `routes whatsapp open to launch, not to a message`() {
        val r = pipeline.handle("వాట్సాప్ తెరువు")
        assertTrue(r.action is AppAction.LaunchApp)
        assertEquals("com.whatsapp", (r.action as AppAction.LaunchApp).packageName)
    }

    @Test
    fun `routes a payment to a guided upi handoff`() {
        val r = pipeline.handle("రవికి 500 రూపాయలు పంపాలి")
        assertTrue(r.action is AppAction.DeepLink)
        assertTrue((r.action as AppAction.DeepLink).uri.startsWith("upi://pay"))
        assertEquals(ExecutionMode.GUIDED_ASSIST, r.mode)
    }

    @Test
    fun `routes navigation to confirm then execute`() {
        val r = pipeline.handle("నన్ను దగ్గరలోని హాస్పిటల్ కి తీసుకెళ్ళు")
        assertTrue(r.action is AppAction.DeepLink)
        assertTrue((r.action as AppAction.DeepLink).uri.startsWith("google.navigation:q="))
        assertEquals(ExecutionMode.CONFIRM_THEN_EXECUTE, r.mode)
    }

    @Test
    fun `unrecognised utterance is guided, never executed`() {
        val r = pipeline.handle("ఈరోజు వాతావరణం ఎలా ఉంది")
        assertEquals(ExecutionMode.GUIDED_ASSIST, r.mode)
        assertTrue(r.action is AppAction.Unsupported)
    }
}
