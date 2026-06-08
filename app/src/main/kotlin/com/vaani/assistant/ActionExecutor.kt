package com.vaani.assistant

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.vaani.core.model.AppAction

/**
 * Turns a platform-agnostic [AppAction] from the core pipeline into a real
 * Android action. Kept separate from [MainActivity] so the execution policy
 * (e.g. fall back to Play Store when an app is missing) lives in one place.
 */
class ActionExecutor(private val context: Context) {

    sealed interface Result {
        data object Launched : Result
        data object AppNotInstalled : Result
        data object NothingToDo : Result
    }

    fun execute(action: AppAction): Result = when (action) {
        is AppAction.LaunchApp -> launch(action.packageName)
        is AppAction.Unsupported -> Result.NothingToDo
    }

    private fun launch(packageName: String): Result {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            Result.Launched
        } else {
            // App not installed — guide the user to the Play Store listing.
            val store = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (store.resolveActivity(context.packageManager) != null) {
                context.startActivity(store)
            }
            Result.AppNotInstalled
        }
    }
}
