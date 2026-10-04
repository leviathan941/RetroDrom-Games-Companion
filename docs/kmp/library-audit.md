# Dependency audit for the KMP migration

Every dependency currently used by the project, with its verified multiplatform status.

**Verified on 2026-09-13** by reading Gradle Module Metadata (`*.module`) from Google Maven and
Maven Central and checking for `ios_arm64` / `ios_simulator_arm64` variants. Re-verify before
starting a stage that depends on a row — versions move.

Versions in use live in `gradle/libs.versions.toml`, not here, so a dependency bump never touches
this file. A version appears below only as a constraint (a minimum version, a version line) or as
dated evidence for a library not adopted yet or rejected; once a stage adopts a library, its
version leaves this file for the catalog.

## Already multiplatform — no replacement needed

| Dependency | iOS variants published | Note |
| --- | --- | --- |
| `androidx.paging:paging-common` | yes | No version migration needed. Room's paging integration comes from `androidx.room3:room3-paging` instead of `androidx.room:room-paging`. |
| `androidx.paging:paging-compose` | yes | |
| `androidx.datastore:datastore-preferences` | yes | Needs an `expect` for the file path only. |
| `androidx.lifecycle:lifecycle-viewmodel` | yes | androidx `ViewModel` itself is multiplatform; the JetBrains fork is not needed. `lifecycle-process` is Android-only. A4 added an explicit `lifecycle-viewmodel-compose` dependency: it used to arrive transitively through `androidx.hilt`, and the Compose BOM does not constrain the `androidx.lifecycle` group. |
| `io.coil-kt.coil3:coil-compose` | yes | |
| `io.coil-kt.coil3:coil-network-ktor3` | yes | Replaced `coil-network-okhttp` in A2. Since A5 it takes the app's injected base `HttpClient`, so it follows whatever engine `ktor-client-engine-defaults` resolves per target. |
| `com.mikepenz:aboutlibraries-compose-m3` | yes | |
| `io.ktor:ktor-client-*` (core, content-negotiation, engine-defaults, resources, serialization) | 3.6.0 | yes | Engine is the only platform part, and since A5 it is resolved rather than named — see ADR-0005. |
| `com.mohamedrejeb.ksoup:ksoup-html` | yes | |
| `org.jetbrains.kotlinx:*` (coroutines, serialization, collections-immutable) | — | yes | `kotlinx-coroutines-android` is the Android artifact of a multiplatform library. |
| `co.touchlab:kermit` | yes | Replaced `android.util.Log` in A1 (ADR-0003). Used through the `Logger` facade in `:common`; `:html-text:imagecontent` depends on it directly to stay app-agnostic. |


## Must be replaced

| Dependency | Problem | Replacement | Stage |
| --- | --- | --- | --- |
| `com.google.dagger:hilt-android` + `androidx.hilt` | Android-only | **Done in A4.** Replaced by **Metro** `dev.zacsweers.metro` — compile-time and multiplatform. The Hilt interop was deliberately not used; every annotation went straight to its native Metro equivalent and Hilt left the build. `hiltViewModel()` is replaced by `dev.zacsweers.metro:metrox-viewmodel` + `metrox-viewmodel-compose`, both `commonMain` KMP artifacts, so the view model wiring is already in its Phase B shape. | A4 (ADR-0001) |
| `androidx.room:*` (2.x) | Room 2.x is multiplatform in its artifacts, but Room 3 is the line Google develops and the one aimed at KMP | **Done in A6.** Now `androidx.room3` (`room3-runtime` / `room3-compiler` / `room3-paging` and the `androidx.room3` Gradle plugin, whose extension is `room3 { }`), plus `androidx.sqlite:sqlite-bundled` for `BundledSQLiteDriver`. Re-verified against Gradle Module Metadata on 2026-09-20: iOS variants published for every artifact used, and 3.0.3 was then the latest stable. `room-ktx` was dropped — there is no `room3-ktx`. | A6 (ADR-0004) |
| `androidx.navigation:navigation-compose` | Google's artifact publishes **only** android, jvmStubs and linux_x64 — no iOS | **Navigation 3**, in two steps. A8: Google's `androidx.navigation3:navigation3-ui` 1.1.7 + `androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0, on Android. A10: coordinates swapped to `org.jetbrains.androidx.navigation3:navigation3-ui` 1.1.1 + `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0. Both publish the same `androidx.navigation3.*` packages (verified by inspecting the JetBrains jar), so the second step touches no imports. `navigation3-common` arrives transitively. | A8 / A10 (ADR-0002) |
| `androidx.compose.*` via Compose BOM | Android-only artifacts | Compose Multiplatform Gradle plugin (`org.jetbrains.compose` 1.12.0) and its `compose.*` accessors | A9 |
| `R.string` / `R.drawable` via `stringResource` / `painterResource` (~31 call sites, plus `values-ru`) | Android resource system | `org.jetbrains.compose.resources` + `composeResources/` | A10 |
| `io.ktor:ktor-client-okhttp` | Android engine | **Done in A5.** No longer declared: `:network` depends on `io.ktor:ktor-client-engine-defaults`, a code-free aggregator whose Gradle Module Metadata maps JVM → `ktor-client-okhttp` and Apple targets → `ktor-client-darwin` (it publishes no Android variant, so Android resolves the JVM one, as `ktor-client-core` already does). DI provides a base `HttpClient`, since the aggregator's engine is only reachable through the no-arg `HttpClient()`. `org.slf4j:slf4j-api` still arrives with the OkHttp engine, as before. | A5 (ADR-0005) |
| `com.google.accompanist:accompanist-permissions` | Android-only | **Calf** `com.mohamedrejeb.calf:calf-permissions-core` + `calf-permissions-notifications` 0.14.0. Verified 2026-09-26: iOS variants published, built on Kotlin 2.4.20, Android runtime dependencies are only `org.jetbrains.compose.foundation` 1.12.0 (the A10 target) and `androidx.activity:activity-compose`. Its composable API (`rememberPermissionState`, `isGranted`, `shouldShowRationale`, `launchPermissionRequest`) mirrors Accompanist, so `PermissionView` and its rationale dialog survive. Rejected: `dev.icerock.moko:permissions` 0.20.1 (last release 2025-08; controller + `BindEffect` API with exception-based denial) and `dev.brewkits:grant-core` 2.6.0 (repository only since 2026-01 with two contributors and four minor releases in three months; view-model controller API with its own dialogs; pulls `androidx.fragment`). | A7c |
| `io.github.aghajari:AnnotatedText` | Android-only (renders through `android.text` spans); last release 2024-01. `:html-text:imagecontent` also reads `android.text.style.ImageSpan` from it. Missed by the first audit, found while splitting A7. | TBD — no KMP library chosen yet: discuss and search for one when the stage starts. `:html-text` already parses with ksoup, which could build the `AnnotatedString` directly as a fallback | A7h |
| `java.time` (`ZonedDateTime`, `DateTimeFormatter`, `ChronoUnit`) and `java.util.concurrent.TimeUnit` | JVM-only APIs, used for RSS publication dates in `:rss-reader` and `:app`, and for the feed expiry in `:rss-reader` | `org.jetbrains.kotlinx:kotlinx-datetime` (to verify); `TimeUnit` → `kotlin.time.Duration` | A7i |

## Stays platform-specific — deliberately

| Dependency | Why | iOS counterpart |
| --- | --- | --- |
| `androidx.browser` (Custom Tabs) | No multiplatform equivalent found yet — search again when A7b starts | `SFSafariViewController` behind the link-opening interface introduced in A7b |
| `com.google.firebase:firebase-messaging` + Google Services plugin | Android SDK | Firebase iOS SDK via Swift, or `dev.gitlive:firebase-messaging` 2.7.0. Deferring push on iOS to after the first release is a legitimate option. Search for a KMP library again when A7e starts. |
| `androidx.core:core-splashscreen`, `androidx.activity`, `androidx.appcompat`, `com.google.android.material` | Android entry point | Native iOS launch screen and `UIViewController` host |
| `androidx.lifecycle:lifecycle-process` | Android process lifecycle | iOS app delegate callbacks |
| Android notification channels (`:notification`) | Platform model differs fundamentally; no KMP library looked for yet — search when A7d starts | `UNUserNotificationCenter` |

## Toolchain notes

- The project applies no Kotlin Gradle plugin today — AGP supplies Kotlin compilation.
  Going multiplatform means applying `org.jetbrains.kotlin.multiplatform` explicitly, with
  `com.android.kotlin.multiplatform.library` for the Android target of library modules.
  This is the subject of stage A9.
- Metro is a Kotlin **compiler plugin**. A3 verified that its Gradle plugin hooks correctly into
  AGP-supplied Kotlin compilation: AGP 9's built-in Kotlin runs every
  `KotlinCompilerPluginSupportPlugin` the same way KGP does. No Kotlin Gradle plugin is needed —
  and none may be applied, since AGP 9 hard-fails on `kotlin.android`, `kapt` and
  `kotlin.multiplatform` alongside built-in Kotlin.
- Navigation 3 on non-Android targets requires CMP ≥ 1.10,
  which is why the JetBrains Navigation 3 coordinates only come in at A10. Google's
  `androidx.navigation3` needs no CMP and is used on Android from A8.
- Hilt had no Navigation 3 integration — there is no `androidx.hilt:hilt-navigation3` artifact —
  which is why A8 was scheduled after the Metro migration rather than before it. A4 has since
  landed, so view model retrieval now goes through `metroViewModel()` and that blocker is gone.
- `metrox-viewmodel-compose` pulls `org.jetbrains.androidx.lifecycle:*` and
  `org.jetbrains.compose.*` into the Android build. Verified harmless: their `androidJvm` variants
  are metadata-only facades that redirect onto the `androidx.*` artifacts and download no code of
  their own, and A4's resolved `androidx.compose.*` and `androidx.lifecycle:*` versions are
  byte-identical to the previous ones. `org.jetbrains.androidx.lifecycle` was already on the
  classpath before A4 in any case.
- Room 3 generates Kotlin only and requires KSP — both already true here. It drops the
  SupportSQLite APIs entirely, so the database is built through a `SQLiteDriver`. On Android it
  falls back to `AndroidSQLiteDriver` from `androidx.sqlite:sqlite-framework`, which
  `room3-runtime` already pulls in; `sqlite-bundled` is an extra dependency needed only because
  ADR-0004 chose the bundled driver. `room3-paging` no longer registers itself — its
  `PagingSourceDaoReturnTypeConverter` has to be named in `@DaoReturnTypeConverters`.
