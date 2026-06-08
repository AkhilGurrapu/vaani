# వాణి — Vaani

**A Telugu-first, voice-driven agentic assistant for Android.**

Vaani lets a user speak naturally in Telugu and have their phone act on it — opening
apps, searching YouTube, navigating with Maps, calling contacts, preparing WhatsApp
messages, and handing off payments — through safe, guided, confirmation-gated actions.
It is an assistive layer, not a hidden autonomous controller.

See [`telugu-agentic-mobile-assistant-prd.md`](telugu-agentic-mobile-assistant-prd.md)
for the full product requirements.

## Architecture

Vaani is built as **native Kotlin** with a clean split between a pure, testable
agentic core and a thin Android platform shell:

```
            ┌─────────────────────── :app (Android) ───────────────────────┐
  speak ──► │  SpeechRecognizer (te-IN)                                     │
            │        │ transcribed Telugu text                              │
            │        ▼                                                      │
            │   ┌──────────────── :core (pure Kotlin/JVM) ──────────────┐   │
            │   │  IntentParser → PolicyEngine (mode)                   │   │
            │   │               → ActionRouter (action via AppCatalog)  │   │
            │   │               → TeluguResponder (speech)              │   │
            │   │            = AssistantPipeline.handle(text)           │   │
            │   └───────────────────────┬───────────────────────────────┘   │
            │        ┌──────────────────┴───────────────────┐              │
            │        ▼                                       ▼              │
            │  TextToSpeech (te-IN)                  ActionExecutor         │
  hear ◄────┤  "…తెరుస్తున్నాను"                     (launch / handoff)      │
            └───────────────────────────────────────────────────────────────┘
```

- **`:core`** — platform-agnostic intent understanding, safety policy, and action
  routing. No Android dependencies, fully unit-tested. The POC ships a deterministic
  rule-based intent parser behind an `IntentParser` interface so a Gemini Nano / cloud
  parser can be swapped in later.
- **`:app`** — Android UI, Telugu STT/TTS, and Android `Intent` execution.

## Execution modes (safety)

Per the PRD, every recognised intent is classified into one of three modes:

| Mode | Risk | Example |
|---|---|---|
| `DIRECT_EXECUTE` | low | open an app, search content |
| `CONFIRM_THEN_EXECUTE` | medium | send a message, start navigation |
| `GUIDED_ASSIST` | sensitive | payments, accessibility-driven flows |

## Building

```bash
./gradlew :core:test        # run the agentic-core unit tests
./gradlew :app:assembleDebug # build the Android APK
```

Requires JDK 17+ and the Android SDK (API 36). The Gradle wrapper pins all other
tooling.

## Status

Vertical slice 1 — **"open app" by voice** — is the first end-to-end tracer bullet
through every layer. Subsequent slices (call, Maps, YouTube search, WhatsApp,
payment handoff) extend the same pipeline. See the
[issue tracker](https://github.com/AkhilGurrapu/vaani/issues).
