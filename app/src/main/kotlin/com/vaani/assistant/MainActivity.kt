package com.vaani.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vaani.assistant.databinding.ActivityMainBinding
import com.vaani.core.model.AppAction
import com.vaani.core.model.ExecutionMode
import com.vaani.core.pipeline.AssistantPipeline
import java.util.Locale

/**
 * The Telugu-first home screen and platform glue for the "open app" slice.
 *
 *   mic tap -> Telugu speech-to-text (te-IN)
 *           -> [AssistantPipeline] understands + routes (pure core)
 *           -> Telugu text-to-speech announces the action
 *           -> [ActionExecutor] performs it (direct-execute for low-risk).
 */
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech
    private lateinit var executor: ActionExecutor

    private val pipeline = AssistantPipeline()

    private val teluguLocale = Locale("te", "IN")

    private val micPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startListening() else toast(getString(R.string.mic_permission_needed))
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

        binding.micButton.setOnClickListener { ensurePermissionThenListen() }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts.language = teluguLocale
    }

    private fun ensurePermissionThenListen() {
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) startListening() else micPermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            toast(getString(R.string.speech_not_available))
            return
        }
        binding.statusText.text = getString(R.string.listening)
        speechLauncher.launch(buildRecognizeIntent(teluguLocale))
    }

    /** Drive the pure core pipeline, then announce + execute per the chosen mode. */
    private fun handleUtterance(text: String) {
        binding.statusText.text = text
        val response = pipeline.handle(text)

        speak(response.teluguSpeech)

        when (response.mode) {
            ExecutionMode.DIRECT_EXECUTE -> runAction(response.action)
            // Confirm / guided modes arrive with later slices; for now we just speak.
            ExecutionMode.CONFIRM_THEN_EXECUTE,
            ExecutionMode.GUIDED_ASSIST -> Unit
        }
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
