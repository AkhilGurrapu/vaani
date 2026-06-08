package com.vaani.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vaani.assistant.databinding.ActivityMainBinding
import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import com.vaani.core.pipeline.AssistantPipeline
import com.vaani.core.skill.SkillRegistry
import java.util.Locale

/**
 * The Telugu-first home screen and platform glue for Vaani.
 *
 *   mic tap -> Telugu speech-to-text (te-IN)
 *           -> [AssistantPipeline] understands + routes across all skills (pure core)
 *           -> Telugu text-to-speech announces the action
 *           -> [ActionExecutor] performs it (per the safety mode).
 *
 * The pipeline is built from [SkillRegistry] with an [AndroidContactResolver] so the
 * call skill can resolve device contacts; every other skill is pure.
 */
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech
    private lateinit var executor: ActionExecutor
    private lateinit var pipeline: AssistantPipeline

    private val teluguLocale = Locale("te", "IN")

    /** Vaani needs the mic to listen and (optionally) contacts to place calls. */
    private val permissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            if (grants[Manifest.permission.RECORD_AUDIO] == true) {
                startListening()
            } else {
                toast(getString(R.string.mic_permission_needed))
            }
        }

    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val spoken = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                ?.trim()
                .orEmpty()
            if (spoken.isEmpty()) {
                binding.statusText.text = getString(R.string.didnt_understand)
            } else {
                handleUtterance(spoken)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = ActionExecutor(this)
        tts = TextToSpeech(this, this)
        pipeline = AssistantPipeline(SkillRegistry.default(AndroidContactResolver(this)))

        binding.micButton.setOnClickListener { ensurePermissionThenListen() }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts.language = teluguLocale
    }

    private fun ensurePermissionThenListen() {
        val micGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        if (micGranted) {
            startListening()
        } else {
            permissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS))
        }
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            toast(getString(R.string.speech_not_available))
            return
        }
        binding.statusText.text = getString(R.string.listening)
        speechLauncher.launch(buildRecognizeIntent(teluguLocale))
    }

    /**
     * Drive the pure core pipeline, then announce + act per the safety mode.
     * This is mode-generic: every current and future skill flows through here,
     * so new slices add no shell code.
     */
    private fun handleUtterance(text: String) {
        binding.statusText.text = text
        val response = pipeline.handle(text)

        speak(response.teluguSpeech)

        when (response.mode) {
            // Low-risk: act immediately (e.g. open an app, search a video).
            ExecutionMode.DIRECT_EXECUTE -> runAction(response.action)
            // Medium-risk: confirm in Telugu first (e.g. call, start navigation).
            ExecutionMode.CONFIRM_THEN_EXECUTE -> confirmThenRun(response.teluguSpeech, response.action)
            // Sensitive / unrecognised: we have already spoken the guidance; do not execute.
            ExecutionMode.GUIDED_ASSIST -> Unit
        }
    }

    private fun confirmThenRun(teluguSpeech: String, action: AppAction) {
        AlertDialog.Builder(this)
            .setMessage(teluguSpeech)
            .setPositiveButton("అవును") { _, _ -> runAction(action) }
            .setNegativeButton("వద్దు", null)
            .show()
    }

    private fun runAction(action: AppAction) {
        when (executor.execute(action)) {
            ActionExecutor.Result.AppNotInstalled ->
                binding.statusText.text = getString(R.string.app_not_installed)
            else -> Unit
        }
    }

    private fun speak(text: String) {
        if (text.isNotBlank()) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vaani-utterance")
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
    /** Builds a Telugu speech-recognition intent for the given [locale]. */
    private fun buildRecognizeIntent(locale: Locale) =
        android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "మాట్లాడండి")
        }
}
