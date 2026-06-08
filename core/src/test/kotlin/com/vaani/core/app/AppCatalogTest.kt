package com.vaani.core.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppCatalogTest {

    private val catalog = AppCatalog()

    @Test
    fun `resolves telugu alias to package`() {
        assertEquals("com.google.android.youtube", catalog.resolve("యూట్యూబ్")?.packageName)
    }

    @Test
    fun `resolves transliterated alias case-insensitively`() {
        assertEquals("com.whatsapp", catalog.resolve("WhatsApp")?.packageName)
    }

    @Test
    fun `resolves alias ignoring surrounding whitespace`() {
        assertEquals("com.google.android.apps.maps", catalog.resolve("  maps  ")?.packageName)
    }

    @Test
    fun `returns telugu label for resolved app`() {
        assertEquals("యూట్యూబ్", catalog.resolve("youtube")?.teluguLabel)
    }

    @Test
    fun `returns null for an unknown app`() {
        assertNull(catalog.resolve("instagram"))
    }
}
