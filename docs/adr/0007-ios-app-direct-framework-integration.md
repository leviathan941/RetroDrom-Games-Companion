# 0007 — iOS app: an Xcode project at `iosApp/` with direct framework integration

**Status:** Accepted (2026-11-21)

## Context

C0 gave `:app:ios` a static `RetroDromCompanion` framework for `iosArm64` and
`iosSimulatorArm64`. C1 needs an Xcode app that links it and shows the shared UI. Neither the
migration plan nor an earlier record said where that project lives or how it gets the
framework. Kotlin offers three ways to hand a Kotlin framework to Xcode:

1. **Direct integration.** A Run Script phase of the app target calls
   `embedAndSignAppleFrameworkForXcode`, which builds the framework for the configuration and
   SDK Xcode is building and copies it to `build/xcode-frameworks/`, where the target's framework
   search path points. It needs nothing but Gradle and Xcode, and it is what the Compose
   Multiplatform template and the Kotlin docs set up by default.
2. **CocoaPods** through `kotlin("native.cocoapods")`: the module becomes a local pod. It adds
   CocoaPods, a Podfile and an `.xcworkspace` to the toolchain, for no gain while the app has no
   other pods.
3. **Swift Package Manager**, by publishing an XCFramework (KMMBridge, or a hand-rolled
   `assembleXCFramework`) as a package. It suits sharing the framework with an iOS app in another
   repository; this app lives next to its Kotlin code.

## Decision

- The Xcode project sits at the repository root as `iosApp/`, the Compose Multiplatform template
  layout: `iosApp/iosApp.xcodeproj` and the Swift sources in `iosApp/iosApp/`.
- It takes the framework by **direct integration**: its first build phase runs
  `./gradlew :app:ios:embedAndSignAppleFrameworkForXcode` from the repository root. The framework
  stays static.
- The app uses the SwiftUI lifecycle. Its `App` struct builds the one `SharedApplication` with
  `createSharedApplication()`, starts it, and hands it to the `ContentView`, which shows `:app:ios`'s
  `MainViewController` through `UIViewControllerRepresentable`.
- The project file uses Xcode 16's file-system-synchronized groups (`objectVersion = 77`): a file
  added under `iosApp/iosApp/` belongs to the target without a project file edit. Identity and
  version build settings live in `iosApp/Configuration/Config.xcconfig`.

## Consequences

- Every Xcode build runs Gradle. The Run Script phase writes outside the target's declared
  outputs, so the target turns `ENABLE_USER_SCRIPT_SANDBOXING` off. Gradle finds its JDK the way it
  does in a shell (`JAVA_HOME` or `/usr/libexec/java_home`).
- Opening the project needs Xcode 16 or newer.
- C3 has no CocoaPods to lean on: the Firebase iOS SDK comes in through Swift Package Manager in
  `iosApp`, or GitLive's Firebase SDK through Gradle. Choosing CocoaPods there means superseding
  this record. Push needs an app delegate, which a SwiftUI app adds with
  `@UIApplicationDelegateAdaptor`.
- Swift sees only what the framework's headers declare. `:app:shared` is not exported, so its
  `SharedApplication` reaches Swift as `SharedSharedApplication`, named after the module. Exporting
  it would rename it and grow the headers by the whole UI module; do it only for a type Swift
  must create or implement.
