package com.vaani.core.contact

/** A resolved contact: the name to speak back and the number to dial. */
data class Contact(
    val displayName: String,
    val phoneNumber: String,
)

/**
 * Resolves a spoken person name (as the user said it, Telugu) to a [Contact].
 * Returns null when no match is found.
 *
 * The core defines only this seam; the Android app supplies an implementation
 * backed by the device address book (ContactsContract). Tests use an in-memory fake.
 */
fun interface ContactResolver {
    fun resolve(spokenName: String): Contact?
}
