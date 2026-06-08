package com.vaani.assistant

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.vaani.core.contact.Contact
import com.vaani.core.contact.ContactResolver

/**
 * Resolves a spoken name against the device address book (ContactsContract).
 *
 * Backs the core [ContactResolver] seam used by `CallSkill`. Returns null when the
 * READ_CONTACTS permission is not granted or no contact matches — `CallSkill` then
 * produces a guided "not found" response rather than dialing a wrong number.
 */
class AndroidContactResolver(private val context: Context) : ContactResolver {

    override fun resolve(spokenName: String): Contact? {
        val name = spokenName.trim()
        if (name.isEmpty() || !hasPermission()) return null

        val resolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        )
        // Case-insensitive substring match on the display name.
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val args = arrayOf("%$name%")

        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            args,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC",
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName = cursor.getString(0) ?: name
                val number = cursor.getString(1)?.replace("\\s".toRegex(), "")
                if (!number.isNullOrEmpty()) return Contact(displayName, number)
            }
        }
        return null
    }

    private fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
}
