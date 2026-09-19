# Dependency audit for the KMP migration

Every dependency currently used by the project, with its verified multiplatform status.

**Verified on 2026-09-13** by reading Gradle Module Metadata (`*.module`) from Google Maven and
Maven Central and checking for `ios_arm64` / `ios_simulator_arm64` variants. Re-verify before
starting a stage that depends on a row — versions move.

## Already multiplatform — no replacement needed

| Dependency | Version in catalog | iOS variants published | Note |
| --- | --- | --- | --- |
| `androidx.paging:paging-common` | 3.5.1 | yes | Also `paging-compose` 3.5.1 publishes iOS. No version migration needed. Room's paging integration comes from `androidx.room3:room3-paging` instead of `androidx.room:room-paging`. |
| `androidx.paging:paging-compose` | 3.5.1 | yes | |
| `androidx.datastore:datastore-preferences` | 1.2.1 | yes | Needs an `expect` for the file path only. |
| `androidx.lifecycle:lifecycle-viewmodel` | 2.11.0 | yes | androidx `ViewModel` itself is multiplatform; the JetBrains fork is not needed. `lifecycle-process` is Android-only. |
| `io.coil-kt.coil3:coil-compose` | 3.6.3 | yes | |
| `io.coil-kt.coil3:coil-network-ktor3` | 3.6.3 | yes | Replaced `coil-network-okhttp` in A2. Built on the app's injected `HttpClientEngine`, so it follows whatever engine A5/C2 choose per platform. |
| `com.mikepenz:aboutlibraries-compose-m3` | 15.2.0 | yes | |
| `io.ktor:ktor-client-*` (core, content-negotiation, resources, serialization) | 3.6.0 | yes | Engine is the only platform part. |
| `com.mohamedrejeb.ksoup:ksoup-html` | 0.6.0 | yes | |
| `org.jetbrains.kotlinx:*` (coroutines, serialization, collections-immutable) | — | yes | `kotlinx-coroutines-android` is the Android artifact of a multiplatform library. |
| `co.touchlab:kermit` | 2.2.0 | yes | Replaced `android.util.Log` in A1 (ADR-0003). Used through the `Logger` facade in `:common`; `:html-text:imagecontent` depends on it directly to stay app-agnostic. |


## Must be replaced

| Dependency | Problem | Replacement | Stage |
| --- | --- | --- | --- |
| `com.google.dagger:hilt-android` + `androidx.hilt` | Android-only | **Metro** `dev.zacsweers.metro` 1.4.3 — compile-time, multiplatform, has Dagger/Hilt interop for a gradual migration | A4 (ADR-0001) |
| `androidx.room:*` 2.8.5 | Room 2.x is multiplatform in its artifacts, but Room 3 is the line Google develops and the one aimed at KMP | **Room 3** — group `androidx.room3`, artifacts `room3-runtime` / `room3-compiler` / `room3-paging` / `room3-migration` and the `androidx.room3` Gradle plugin, 3.0.3 stable (2026-09-09); iOS variants verified. Needs `androidx.sqlite:sqlite-bundled` 2.7.1 for the driver. | A6 (ADR-0004) |
| `androidx.navigation:navigation-compose` 2.10.1 | Google's artifact publishes **only** android, jvmStubs and linux_x64 — no iOS | **Navigation 3**, in two steps. A8: Google's `androidx.navigation3:navigation3-ui` 1.1.7 + `androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0, on Android. A10: coordinates swapped to `org.jetbrains.androidx.navigation3:navigation3-ui` 1.1.1 + `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0. Both publish the same `androidx.navigation3.*` packages (verified by inspecting the JetBrains jar), so the second step touches no imports. `navigation3-common` arrives transitively. | A8 / A10 (ADR-0002) |
| `androidx.compose.*` via Compose BOM | Android-only artifacts | Compose Multiplatform Gradle plugin (`org.jetbrains.compose` 1.12.0) and its `compose.*` accessors | A9 |
| `R.string` / `R.drawable` via `stringResource` / `painterResource` (~31 call sites, plus `values-ru`) | Android resource system | `org.jetbrains.compose.resources` + `composeResources/` | A10 |
| `io.ktor:ktor-client-okhttp` | Android engine | Keep on Android, add `io.ktor:ktor-client-darwin` 3.6.0 for iOS, engine chosen through DI | A5 / C2 |
| `com.google.accompanist:accompanist-permissions` | Android-only | Own interface in `:permission`; `dev.icerock.moko:permissions` 0.20.1 is the candidate implementation | A7 / B7 |

## Stays platform-specific — deliberately

| Dependency | Why | iOS counterpart |
| --- | --- | --- |
| `androidx.browser` (Custom Tabs) | No multiplatform equivalent | `SFSafariViewController` behind a common `ArticleOpener` interface |
| `com.google.firebase:firebase-messaging` + Google Services plugin | Android SDK | Firebase iOS SDK via Swift, or `dev.gitlive:firebase-messaging` 2.7.0. Deferring push on iOS to after the first release is a legitimate option. |
| `androidx.core:core-splashscreen`, `androidx.activity`, `androidx.appcompat`, `com.google.android.material` | Android entry point | Native iOS launch screen and `UIViewController` host |
| `androidx.lifecycle:lifecycle-process` | Android process lifecycle | iOS app delegate callbacks |
| Android notification channels (`:notification`) | Platform model differs fundamentally | `UNUserNotificationCenter` |

## Toolchain notes

- The project applies no Kotlin Gradle plugin today — AGP 9.4 supplies Kotlin compilation.
  Going multiplatform means applying `org.jetbrains.kotlin.multiplatform` explicitly, with
  `com.android.kotlin.multiplatform.library` for the Android target of library modules.
  This is the subject of stage A9.
- Metro is a Kotlin **compiler plugin**. Whether its Gradle plugin hooks correctly into
  AGP-supplied Kotlin compilation is unverified — that is what the A3 spike is for.
- Compose Multiplatform stable is 1.12.0. Navigation 3 on non-Android targets requires CMP ≥ 1.10,
  which is why the JetBrains Navigation 3 coordinates only come in at A10. Google's
  `androidx.navigation3` needs no CMP and is used on Android from A8.
- Hilt has no Navigation 3 integration — there is no `androidx.hilt:hilt-navigation3` artifact —
  which is why A8 is scheduled after the Metro migration rather than before it.
- Room 3 generates Kotlin only and requires KSP — both already true here. It drops the
  SupportSQLite APIs entirely, so the database is built through a `SQLiteDriver`.
