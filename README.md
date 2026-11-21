# ScamShield Edge

**Detect it. Explain it. Prevent it.**

Privacy-first, India-focused on-device SMS and URL scam detection using risk analysis and Llama with ExecuTorch.

## About the Project

ScamShield Edge is an Android application designed to help users identify potentially fraudulent SMS messages and suspicious URLs.

It analyzes message content, identifies scam indicators, calculates a risk score, and provides an understandable explanation with recommended safe actions.

The project focuses on privacy by performing detection and AI inference directly on the Android device.

## Key Features

- **SMS Scam Detection:** Analyze manually pasted SMS messages.
- **Risk Scoring:** Calculate a risk score based on suspicious indicators.
- **Risk Classification:** Categorize messages into Low, Medium, and High risk.
- **URL Analysis:** Identify suspicious URL patterns and indicators.
- **AI-Powered Explanations:** Use an on-device Llama model through ExecuTorch.
- **Privacy-First Design:** Designed for on-device processing without requiring cloud-based message analysis.
- **Safe Recommendations:** Provide guidance to help users avoid suspicious links, OTP requests, and fraudulent payment requests.

## Technology Stack

| Component | Technology |
|---|---|
| Platform | Android |
| Programming Language | Kotlin |
| Development Environment | Android Studio |
| AI Model | Llama 3.2 1B Instruct |
| On-device AI Runtime | ExecuTorch |
| Model Optimization | XNNPACK / SpinQuant |
| Build System | Gradle |

## How It Works

1. The user pastes an SMS message into the application.
2. The detection engine analyzes suspicious words, links, and scam indicators.
3. A risk score and risk classification are generated.
4. The on-device Llama model generates an explanation when available.
5. A safety fallback provides a predefined explanation if AI output is unavailable or unsafe.
6. The application displays the result and recommended safe actions.

**Important:** The detection engine determines the risk verdict. Llama is used to explain the result, not to independently decide whether a message is safe.

## System Architecture

```text
       User
        |
        v
  Android Application
        |
        v
   SMS / URL Input
        |
        v
    Detection Engine
        |
        v
  Risk Score & Verdict
        |
        v
  On-device Llama AI
     (ExecuTorch)
        |
        v
 Explanation & Safe Actions
```

## Model Setup

The quantized Llama model is not included in this GitHub repository because of its large file size.

The application requires the model files to be available on the Android device.

Required files:

- `model.pte`
- `tokenizer.model`

Expected device directory:

```text
Android/data/com.example.scamshieldedge/files/llm/
```

Place both model files in this directory before testing the on-device AI functionality.

## Installation and Setup

1. Clone the repository:

```bash
git clone https://github.com/manivannandhanalakshmi908-ux/ScamShield-Edge.git
```

2. Open the project in Android Studio.

3. Allow Gradle to synchronize the project.

4. Connect an Android device with USB debugging enabled.

5. Build and install the application.

6. Set up the required model files on the device to enable on-device Llama inference.

## Privacy and Security

- Designed for local message analysis.
- No automatic SMS interception is required.
- Users manually provide messages for analysis.
- AI explanations are generated using an on-device model.
- Safe fallback explanations are provided when AI output cannot be used.

## Project Objective

To make scam detection more accessible, privacy-conscious, and understandable for smartphone users, with a focus on India.

## Repository

GitHub: https://github.com/manivannandhanalakshmi908-ux/ScamShield-Edge

## Project Tagline

**ScamShield Edge — Detect it. Explain it. Prevent it.**
