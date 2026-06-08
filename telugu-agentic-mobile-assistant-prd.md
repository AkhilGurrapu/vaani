# Product Requirements Document: Telugu-First Agentic Mobile Assistant

## Overview
This document defines the product requirements for a Telugu-first, voice-driven Android assistant designed for users who struggle with smartphone navigation, English interfaces, or multi-app workflows. The assistant should accept spoken commands in Telugu, understand user intent, and help complete tasks across apps such as Maps, WhatsApp, YouTube, calling, contacts, and payment apps through safe, guided, agentic interactions.[cite:2][cite:36][cite:39]

The product is intended as an assistive mobile layer rather than a fully autonomous hidden controller. The core design principle is to reduce smartphone complexity for users by combining voice understanding, app handoff, confirmations, and selective automation while preserving user trust and safety.[cite:33][cite:36]

## Product goal
The primary goal is to let a user speak naturally in Telugu and have the app convert that request into an actionable workflow on Android. For the initial POC, the app should reliably support common, high-value tasks such as opening apps, searching YouTube, navigating with Maps, placing calls, opening WhatsApp chats, and preparing payment handoff flows.[cite:36][cite:39]

A secondary goal is to support privacy-sensitive and low-connectivity scenarios by using on-device AI where practical. Gemini Nano is designed for on-device use through Android’s AI stack and can enable local inference without sending data to the cloud, making it a strong fit for common commands and privacy-first actions.[cite:2]

## Target users
The primary users are Telugu-speaking Android users who may be elderly, first-time smartphone users, low-literacy users, or users who are uncomfortable navigating English interfaces and app-specific UX patterns. The product should assume low technical confidence and should minimize the need for typing, reading, and manual app navigation.[cite:2]

Secondary users include family members or caregivers who may help set up the assistant and configure trusted contacts, preferred apps, language defaults, and confirmation behavior. The onboarding and permissions model should therefore be simple, transparent, and explainable in Telugu.[cite:33]

## Problem statement
Many smartphone tasks require users to know which app to open, how to navigate the interface, what each icon means, and how to complete multi-step flows in English or icon-heavy interfaces. This creates a steep usability barrier for regional-language users, especially when tasks span multiple apps such as finding a place in Maps, messaging someone on WhatsApp, or opening a payment flow in PhonePe.[cite:36][cite:39]

Existing assistants can answer questions, but reliable cross-app task completion depends heavily on app integration paths such as deep links, intents, APIs, and accessibility-based assistance. The main challenge is therefore not only language understanding but also safe orchestration across Android and third-party app surfaces.[cite:33][cite:36]

## Product vision
The product should behave like a voice-first smartphone guide and task assistant that understands Telugu naturally and helps the user take action in the right app. The long-term vision is a multilingual Indian assistant supporting Telugu first, then English, Hindi, Tamil, Malayalam, and Kannada, with a guided agentic layer for the most common mobile tasks.[cite:13]

The assistant should feel trustworthy, predictable, and simple. It should confirm important actions, explain what it is doing in Telugu, and escalate to guided mode when a task cannot be safely automated end-to-end.[cite:33]

## Scope
### In scope for POC
- Telugu voice input and Telugu voice output.[cite:2]
- Intent understanding for common task categories: calling, contacts, Maps, WhatsApp, YouTube, and payment handoff.[cite:36][cite:39]
- Hybrid AI routing with Gemini Nano for selected on-device tasks and Gemini cloud fallback for harder requests.[cite:2][cite:30]
- Android deep links, intents, and app links where supported.[cite:36][cite:39]
- Accessibility-guided fallback for supported assistive flows where direct integrations are unavailable.[cite:33]
- Confirmation-based execution for sensitive actions such as messaging and payment handoff.[cite:33]

### Out of scope for POC
- Fully autonomous end-to-end payment completion without user confirmation.
- Broad uncontrolled automation across all third-party apps.
- iOS support.
- Multi-language parity beyond Telugu in the first prototype.
- Enterprise admin controls or large-scale account management.

## User stories
- As a Telugu-speaking user, the user wants to say “అమ్మకి కాల్ చేయి” and have the app place a call to the correct contact with minimal taps.
- As a Telugu-speaking user, the user wants to say “యూట్యూబ్ లో అన్నమయ్య పాటలు ప్లే చెయ్యి” and have the app open YouTube with the right search or playback flow.[cite:39]
- As a Telugu-speaking user, the user wants to say “నన్ను దగ్గరలోని హాస్పిటల్ కి తీసుకెళ్ళు” and have the app open Maps navigation to a relevant result.[cite:36]
- As a Telugu-speaking user, the user wants to say “రవికి వాట్సాప్ లో నేను వస్తున్నా అని మెసేజ్ పంపు” and have the app prepare a message and ask for confirmation before sending.[cite:39][cite:33]
- As a Telugu-speaking user, the user wants to say “రవికి 500 రూపాయలు పంపాలి” and have the app open the correct payment handoff screen with strong confirmation and manual final approval.[cite:33]

## Core experience
The core interaction loop should be simple: the user speaks in Telugu, the system transcribes the request, extracts intent and slots, decides whether it can handle the task locally or needs cloud reasoning, selects an integration path, explains the planned action in Telugu, then executes or guides the user to completion. This creates a controlled agentic flow rather than an opaque fully automated system.[cite:2][cite:36]

The app should default to one of three execution modes:
1. Direct execute for low-risk tasks such as opening an app or searching content.
2. Confirm then execute for medium-risk tasks such as sending a message or starting navigation.
3. Guided assist for sensitive or unreliable tasks such as payments or UI-driven flows that depend on Accessibility automation.[cite:33][cite:36]

## Functional requirements
### Voice and language
- The system must accept spoken Telugu as the primary input language.
- The system must produce spoken Telugu confirmations and feedback.
- The system should maintain a simple Telugu-first UI with large labels and minimal English.
- The system should be architected to add English, Hindi, Tamil, Malayalam, and Kannada later.[cite:13]

### Intent understanding
- The system must classify incoming requests into supported intents such as call, message, navigate, search video, open app, and payment handoff.
- The system must extract required parameters such as person name, destination, query, app name, amount, or message body.
- The system must detect ambiguity and ask a Telugu clarification question before proceeding.
- The system must return structured output suitable for deterministic action routing.

### Agent orchestration
- The system must choose the best available execution path in this order: official integration, deep link/app link, Android intent, guided Accessibility flow.[cite:36][cite:39][cite:33]
- The system must apply confirmation before irreversible or sensitive actions.
- The system must gracefully fail and explain the next step in Telugu if an integration path is unavailable.
- The system should maintain short conversational context for follow-up commands such as “అదే రవికి పంపు”.

### App integrations
- The system must support Maps deep links or app links for search and navigation flows.[cite:36][cite:39]
- The system must support YouTube launch and search handoff where available.[cite:39]
- The system must support phone and contact intents for calling and contact resolution.
- The system must support WhatsApp message preparation through supported launch paths and confirmation gates.[cite:39]
- The system should support PhonePe or UPI handoff to a payment initiation screen, with final user confirmation left to the payment app where required.[cite:33]

### Accessibility support
- The system may use Accessibility-based assistance only after explicit user permission and clear Telugu explanation of what access means.[cite:33]
- The system must not hide the use of accessibility features from the user.
- The system should use Accessibility as a fallback when no reliable intent or deep link path exists.
- The system should log UI automation failures for later flow hardening.

## Non-functional requirements
### Safety and trust
- The app must clearly tell the user what action it is about to perform before medium-risk and high-risk operations.
- The app must require explicit confirmation for sending messages, initiating payments, or other potentially harmful actions.
- The app must avoid silent autonomous execution for sensitive actions.[cite:33]

### Privacy
- The system should process common commands on-device where possible using Gemini Nano to reduce cloud dependence and protect user data.[cite:2]
- The system must send minimal required data to cloud models when fallback inference is needed.
- The system should maintain transparent disclosures about what is processed locally versus remotely.

### Performance
- Low-risk actions should feel immediate, with fast local response for common tasks.
- Cloud fallback should be optimized for short prompts and structured outputs to reduce latency and cost.[cite:30]
- The app should remain usable on mid-range Android phones where supported integrations are available.

### Accessibility and usability
- All primary UI controls must have large touch targets and strong Telugu labels.
- The app must support users with low literacy through voice prompts and minimal text dependence.
- Permission requests must be explained in plain Telugu.

## Model strategy
### Recommended architecture
The recommended architecture is hybrid. Gemini Nano should be used for selected on-device tasks such as common intent understanding, short command handling, or privacy-sensitive flows because it is designed for Android on-device generative AI through AICore and related developer APIs.[cite:2]

A cloud Gemini model should be used as fallback for harder reasoning, ambiguity resolution, and more robust multilingual understanding. Gemini Developer API pricing indicates free-tier access exists for several models, with paid usage charged mainly by input and output tokens depending on the model tier.[cite:30][cite:31]

### Model options
| Model / option | Role in product | Advantages | Constraints | Commercial notes |
|---|---|---|---|---|
| Gemini Nano | On-device common commands, privacy-first tasks | Local inference, no network needed, strong Android fit [cite:2] | Device support limits, smaller scope for complex orchestration [cite:2] | Not billed like cloud token usage through Gemini Developer API pricing [cite:2] |
| Gemini 2.5 Flash-Lite | Cheapest cloud fallback | Low cost, suitable for short intent routing [cite:30] | Less capable on hard reasoning [cite:30] | Token-based pricing with free tier [cite:30] |
| Gemini 2.5 Flash | Main cloud fallback for POC | Strong balance of cost and capability [cite:30] | Higher cost than Lite [cite:30] | Token-based pricing with free tier [cite:30] |
| Gemini 2.5 Pro | Escalation path for hard multi-step requests | Best reasoning in the compared set [cite:30] | More expensive [cite:30] | Token-based pricing with free tier / paid tiers [cite:30] |
| Gemma 4 | Future self-hosted or experimental local option | Open-model flexibility [cite:16][cite:18] | More runtime, deployment, and mobile optimization burden; less natural Android-native fit for this product than Gemini Nano [cite:18][cite:2] | Cost depends on self-hosting path, not standard Gemini API billing [cite:18] |

### Why Gemini first instead of Gemma first
Gemma can be used, especially for later experimentation with open local models, but the first version should prioritize reliable Android-native integration and fast execution over model customization. The product’s core challenge is safe action orchestration across apps, not only running a local model, which is why Gemini Nano plus cloud Gemini fallback is the recommended primary stack.[cite:2][cite:18]

## Integrations feasibility
| App / capability | Feasibility | Primary path | Notes |
|---|---|---|---|
| Calling / contacts | Easy | Android intents and contacts lookup | Strong POC candidate |
| Google Maps | Easy | Deep links / app links / intents [cite:36][cite:39] | Strong POC candidate |
| YouTube | Easy to medium | Search handoff / launch intent [cite:39] | Good POC candidate |
| WhatsApp | Medium | Launch + prefilled message + confirmation [cite:39] | Final send flow may vary |
| PhonePe / UPI apps | Medium to hard | Payment handoff / deep link where supported | Final sensitive steps should remain user-confirmed [cite:33] |
| Arbitrary third-party apps | Hard | Accessibility fallback [cite:33] | Fragile and UI-dependent |

## Technical architecture
### High-level flow
1. Capture Telugu speech.
2. Convert speech to text.
3. Parse the text into intent plus parameters.
4. Choose local model or cloud model based on complexity and connectivity.
5. Select the best supported action path.
6. Announce the planned action in Telugu.
7. Execute, confirm, or guide the user.
8. Record outcome for analytics and flow improvement.

### Main components
- Android client application in Kotlin.
- Speech input layer.
- Intent parser and slot extractor.
- Policy engine for safety and confirmation.
- Action router for intents, deep links, app links, and Accessibility fallback.[cite:36][cite:39][cite:33]
- Telugu TTS response layer.
- Session memory for short multi-turn context.
- Telemetry and audit log for failures and escalation.

## Permissions and compliance
The app will likely require microphone access, contacts access for name resolution and calling, and optional Accessibility permissions for assistive automation. Because Accessibility permissions are highly sensitive, the product must explain the permission in Telugu, show examples of how it is used, and allow users to disable it easily.[cite:33]

The product should avoid deceptive automation patterns and should align with Android platform expectations for user consent, background behavior, and app interaction boundaries. Sensitive financial actions should always remain visible and user-approved inside the relevant payment app.[cite:33]

## Pricing and cost considerations
Gemini Nano is suitable for reducing cloud cost because it executes on-device and avoids standard cloud token billing for those interactions.[cite:2] Gemini cloud models are charged primarily by input and output tokens, with multiple models offering free tiers and then paid usage on standard or other service tiers once volume or advanced usage increases.[cite:30][cite:31]

For a POC, cost can be kept low by routing only ambiguous or complex requests to the cloud and keeping prompts short with structured JSON outputs. The main cost escalators are long prompts, large output generations, live audio models, grounded search/maps requests beyond free allowance, and high request volume.[cite:30]

## Success metrics
### POC metrics
- Intent parsing accuracy on supported Telugu commands.
- Task completion rate for supported flows.
- Clarification rate on ambiguous requests.
- Percentage of tasks completed without manual re-navigation.
- User confirmation acceptance rate.
- Time to complete common tasks such as calling, Maps navigation, and WhatsApp message preparation.

### Quality metrics
- Accessibility automation failure rate.
- Fallback rate from local to cloud model.
- Average latency by task type.
- Cost per successful task for cloud-routed requests.[cite:30]

## Milestones
### Milestone 1: Voice and intent POC
- Telugu speech input.
- Basic intent parser.
- Calling, Maps, and YouTube flows.
- Telugu spoken confirmations.

### Milestone 2: Messaging and guided actions
- WhatsApp message preparation.
- Confirmation flow.
- Short conversational context.
- Error handling and Telugu clarifications.

### Milestone 3: Payment handoff and Accessibility fallback
- Payment initiation handoff.
- Accessibility-guided support for selected flows.
- Permission education screens.
- Telemetry for automation failures.

### Milestone 4: Hybrid model optimization
- Gemini Nano local routing.
- Cloud fallback policy.
- Cost tuning and prompt optimization.[cite:2][cite:30]

## Open questions
- Which exact Android devices and OS versions will be supported in the first rollout for Gemini Nano-enabled flows?[cite:2]
- Which payment apps will be officially targeted in the POC beyond PhonePe, and what deep-link or handoff capability is available on those apps?
- Should the first version send WhatsApp messages automatically after confirmation, or only prepare them for final user approval?
- How much user history or context should be retained locally, and for how long?
- Which family member or caregiver setup flows are required for onboarding trusted contacts and preferred apps?

## Recommended build brief for the implementation agent
Build an Android-native Telugu-first voice assistant that converts spoken Telugu into structured intents and app actions. Use a hybrid model strategy: Gemini Nano for supported on-device tasks and Gemini 2.5 Flash as the default cloud fallback, with Gemini 2.5 Pro reserved for hard multi-step reasoning only when necessary.[cite:2][cite:30]

Implement the action router using this priority order: official app integration, Android intent/deep link, app link, then Accessibility fallback. Restrict the initial scope to calling, contacts, Maps, YouTube, WhatsApp message preparation, and payment handoff flows. Require confirmation for any message send, navigation start, or payment-related action, and keep all UI and system prompts Telugu-first with large accessible controls.[cite:33][cite:36][cite:39]
