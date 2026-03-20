# VITAL Android 🤖

VITAL is a modern, offline-first health tracking Android application designed to flawlessly match the minimalist and elegant aesthetic of the VITAL Web App. It allows users to meticulously log their daily health metrics, such as weight, blood pressure, and heart rate, with seamless cloud synchronization.

## Key Features

- **Elegant UI/UX**: Built entirely using Jetpack Compose with a sleek cream-and-black design language.
- **Typography**: Uses Google's **Playfair Display** (headers) and **Inter** (body) seamlessly rendered via Downloadable Fonts to perfectly mimic the web interface.
- **Supabase Authentication**: Full email/password login and registration flows built securely on the Supabase V3 Kotlin SDK.
- **Robust Local Caching**: Fully offline-capable architecture leveraging Android's **Room Database (SQLite)**. Data is securely cached locally so the app runs instantly on launch.
- **Automatic Session Management**: Integrated with reactive `SessionStatus` streams to handle seamless auto-logins via `SharedPreferences`.
- **Intelligent Syncing**: A dedicated cloud-sync mechanism that automatically pushes locally queued health logs directly to your Supabase PostgreSQL database.
- **Smart Form Validation**: Context-aware input fields (like dynamic, multi-field split views for Systolic/Diastolic Blood Pressure) protected by strict regex patterns.
- **Adaptive Iconography**: Custom-designed, infinitely scalable Android Vector adaptive launcher icon (`favicon.svg` translation).

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Dependency Injection**: Dagger Hilt
- **Local Storage**: Room (SQLite)
- **Network & Cloud**: Supabase Kotlin SDK (v3.0.0) + Ktor Client
- **Asynchronous Processing**: Kotlin Coroutines & Flows

## Getting Started

### Prerequisites

- Android Studio (Iguana / Jellyfish or newer)
- Supabase Project (for Backend as a Service)

### Configuration

By default, the application is tightly integrated with a specific Supabase backend. If you wish to use your own Supabase instance, update the configuration keys:

1. Open `app/src/main/java/com/vital/health/di/AppModule.kt`.
2. Locate the `provideSupabase()` method.
3. Replace the `supabaseUrl` and `supabaseKey` (Anon Key) parameters with your own project's credentials. Note: Never commit hardcoded production secrets to public version control.

### Installation & Build

1. Open the `/VITAL-Android` folder in Android Studio.
2. Wait for Gradle to fully sync the updated dependencies (specifically the Hilt logic and Supabase SDKs).
3. Connect an Android Emulator or a physical device.
4. Click **Run** (`Shift + F10`) or run the following in your terminal:
   ```bash
   ./gradlew assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

## Rate Limits & Testing Note

When repeatedly signing up new users during testing, you may encounter a Supabase _Email Rate Limit_. To bypass this, simply navigate to your Supabase Project Settings -> Auth -> Rate Limits, and temporarily increase the limits, or use the "Log In" functionality with pre-created accounts directly from your Supabase Dashboard.
