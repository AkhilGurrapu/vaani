package com.vaani.core.intent

import com.vaani.core.model.ParsedIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleBasedIntentParserTest {

    private val parser = RuleBasedIntentParser()

    @Test
    fun `parses telugu open-app command with తెరువు`() {
        val result = parser.parse("యూట్యూబ్ తెరువు")
        assertTrue(result is ParsedIntent.OpenApp)
        assertEquals("యూట్యూబ్", (result as ParsedIntent.OpenApp).spokenAppName)
    }

    @Test
    fun `parses open-app command with ఓపెన్ చేయి and strips filler`() {
        val result = parser.parse("వాట్సాప్ ఓపెన్ చేయి")
        assertTrue(result is ParsedIntent.OpenApp)
        assertEquals("వాట్సాప్", (result as ParsedIntent.OpenApp).spokenAppName)
    }

    @Test
    fun `parses transliterated open command`() {
        val result = parser.parse("maps open")
        assertTrue(result is ParsedIntent.OpenApp)
        assertEquals("maps", (result as ParsedIntent.OpenApp).spokenAppName)
    }

    @Test
    fun `drops the యాప్ noise word`() {
        val result = parser.parse("యూట్యూబ్ యాప్ తెరువు")
        assertTrue(result is ParsedIntent.OpenApp)
        assertEquals("యూట్యూబ్", (result as ParsedIntent.OpenApp).spokenAppName)
    }

    @Test
    fun `preserves raw text on the parsed intent`() {
        val raw = "మ్యాప్స్ తెరువు"
        val result = parser.parse(raw)
        assertEquals(raw, result.rawText)
    }

    @Test
    fun `returns Unknown when no open trigger is present`() {
        val result = parser.parse("ఈరోజు వాతావరణం ఎలా ఉంది")
        assertTrue(result is ParsedIntent.Unknown)
    }

    @Test
    fun `returns Unknown for blank input`() {
        assertTrue(parser.parse("   ") is ParsedIntent.Unknown)
    }
}
