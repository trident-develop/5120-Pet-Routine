# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Kotlin Multiplatform / Compose Multiplatform app targeting Android and iOS. Single shared Gradle module `:composeApp` holds all UI and logic; `iosApp/` is a thin Xcode wrapper that hosts the shared `ComposeApp` framework.

- Kotlin `2.3.20`, Compose Multiplatform `1.10.3`, AGP `8.11.2`
- Android: minSdk 24, targetSdk/compileSdk 36, JVM 11, namespace/applicationId `org.example.project`
- iOS targets: `iosArm64`, `iosSimulatorArm64`; framework `ComposeApp` (static)
- iOS bundle id is built from `iosApp/Configuration/Config.xcconfig` (`PRODUCT_BUNDLE_IDENTIFIER=org.example.project.PetRoutine$(TEAM_ID)`); set `TEAM_ID` there for signed builds.

## Common commands

Run from the repo root.

```bash
# Android
./gradlew :composeApp:assembleDebug          # debug APK
./gradlew :composeApp:installDebug           # install on connected device/emulator

# Tests
./gradlew :composeApp:allTests               # all KMP test targets
./gradlew :composeApp:testDebugUnitTest      # Android unit tests only
./gradlew :composeApp:iosSimulatorArm64Test  # iOS simulator tests
# Single test:
./gradlew :composeApp:testDebugUnitTest --tests "org.example.project.ComposeAppCommonTest.example"

# iOS
# Open iosApp/iosApp.xcodeproj in Xcode and run the iosApp scheme.
# For an unsigned IPA, double-click build_unsigned_ipa.command (archives via xcodebuild
# with CODE_SIGNING_ALLOWED=NO, then zips Payload/ → PetRoutine.ipa in repo root).
```

The Kotlin framework is consumed by Xcode through Gradle; building the iOS scheme triggers Gradle, so a working JDK + this repo's `local.properties` `sdk.dir` must resolve.

## Architecture

### Source layout (`composeApp/src/`)
- `commonMain/kotlin/org/example/project/` — all shared Kotlin/Compose code (package `org.example.project`).
  - `App.kt` — root composable.
  - `AppNavGraph.kt` — main app navigation surface (currently a placeholder).
  - `screens/` — full-screen composables (`LoadingScreen`, `NoInternetScreen`, …).
  - `Gray.kt`, `PlatformWebView.kt` — `expect` declarations.
- `androidMain/` — `MainActivity` (`setContent { App() }`) + `*.android.kt` `actual`s + Android resources/manifest.
- `iosMain/` — `MainViewController()` returning `ComposeUIViewController { App() }` (called from `iosApp/iosApp/ContentView.swift`) + `*.ios.kt` `actual`s.

### The `Gray` shell pattern (important)
`App()` does not render screens directly. It calls `Gray(loading, noInternet, white)` — an `expect` composable with platform `actual`s in `Gray.android.kt` / `Gray.ios.kt` — passing in three slots:
- `loading` → `LoadingScreen`
- `noInternet` → `NoInternetScreen` (receives an `onRetry` callback)
- `white` → `AppNavGraph` (the real app)

`Gray` is the seam where platform-specific concerns (connectivity checks, splash/init logic) decide which slot to render. **Both platform implementations are currently empty stubs** (`{ }`) — they render nothing. When wiring up app behaviour that depends on connectivity or initialization state, the logic belongs inside `Gray.android.kt` / `Gray.ios.kt`, not inside `App()` or the screen composables.

### Adding platform-specific APIs
Use the `expect` / `actual` split, not runtime checks. Existing example: `PlatformWebView` is `expect` in `commonMain`, with Android using `AndroidView` + `WebView`, and iOS using `UIKitView` + `WKWebView`.

### Dependencies
All versions and aliases live in `gradle/libs.versions.toml` (typesafe accessors are enabled in `settings.gradle.kts`). Add new libs there rather than hardcoding coordinates in `composeApp/build.gradle.kts`.
