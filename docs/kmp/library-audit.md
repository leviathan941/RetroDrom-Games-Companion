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
| `androidx.lifecycle:lifecycle-viewmodel`, `-viewmodel-compose`, `-runtime-compose`, `-viewmodel-navigation3` | yes | Google's own artifacts are multiplatform, Compose integrations included: re-verified 2026-11-07 against the 2.11.0 module metadata, all four publish `ios_arm64` and `ios_simulator_arm64`, and their iOS variants depend on Google's `androidx.compose.runtime`, which is what Compose Multiplatform's runtime redirects to. The JetBrains fork is not needed: `org.jetbrains.androidx.lifecycle:*` 2.11.0 is an empty facade on every platform, iOS included (an ~8 KB klib that depends on `androidx.lifecycle:*` 2.11.0). `lifecycle-process` and `lifecycle-viewmodel-ktx` are Android-only. A4 added an explicit `lifecycle-viewmodel-compose` dependency: it used to arrive transitively through `androidx.hilt`, and the Compose BOM does not constrain the `androidx.lifecycle` group. |
| `io.coil-kt.coil3:coil-compose` | yes | |
| `io.coil-kt.coil3:coil-network-ktor3` | yes | Replaced `coil-network-okhttp` in A2. Since A5 it takes the app's injected base `HttpClient`, so it follows whatever engine `ktor-client-engine-defaults` resolves per target. |
| `com.mikepenz:aboutlibraries-compose-m3` | yes | |
| `io.ktor:ktor-client-*` (core, content-negotiation, engine-defaults, resources, serialization), `io.ktor:ktor-http` | 3.6.0 | yes | Engine is the only platform part, and since A5 it is resolved rather than named — see ADR-0005. `ktor-http` is declared directly by `:notification` since A7f, which builds the push deeplink with its `URLBuilder`. |
| `com.mohamedrejeb.ksoup:ksoup-html` | yes | Also used by `:rss-reader` since A7g, where it replaced `android.text.Html` for feed image extraction. Re-verified 2026-10-24: `ios_arm64` and `ios_simulator_arm64` variants published. Since A7h it is also the parser behind `:html-text`'s own `AnnotatedString` converter. |
| `com.mohamedrejeb.ksoup:ksoup-entities` | yes | Declared by `:html-text` since A7h, which decodes text and attributes with it because ksoup-html 0.6.0 leaves numeric entities undecoded. It was already on the runtime classpath through ksoup-html. Verified 2026-10-25: `ios_arm64` and `ios_simulator_arm64` variants published. |
| `org.jetbrains.kotlinx:*` (coroutines, serialization, collections-immutable, datetime) | — | yes | `kotlinx-coroutines-android` is the Android artifact of a multiplatform library. `kotlinx-datetime` came in with A7i. |
| `co.touchlab:kermit` | yes | Replaced `android.util.Log` in A1 (ADR-0003). Used through the `Logger` facade in `:common`; `:html-text:imagecontent` depends on it directly to stay app-agnostic. |


## Must be replaced

| Dependency | Problem | Replacement | Stage |
| --- | --- | --- | --- |
| `com.google.dagger:hilt-android` + `androidx.hilt` | Android-only | **Done in A4.** Replaced by **Metro** `dev.zacsweers.metro` — compile-time and multiplatform. The Hilt interop was deliberately not used; every annotation went straight to its native Metro equivalent and Hilt left the build. `hiltViewModel()` is replaced by `dev.zacsweers.metro:metrox-viewmodel` + `metrox-viewmodel-compose`, both `commonMain` KMP artifacts, so the view model wiring is already in its Phase B shape. | A4 (ADR-0001) |
| `androidx.room:*` (2.x) | Room 2.x is multiplatform in its artifacts, but Room 3 is the line Google develops and the one aimed at KMP | **Done in A6.** Now `androidx.room3` (`room3-runtime` / `room3-compiler` / `room3-paging` and the `androidx.room3` Gradle plugin, whose extension is `room3 { }`), plus `androidx.sqlite:sqlite-bundled` for `BundledSQLiteDriver`. Re-verified against Gradle Module Metadata on 2026-09-20: iOS variants published for every artifact used, and 3.0.3 was then the latest stable. `room-ktx` was dropped — there is no `room3-ktx`. | A6 (ADR-0004) |
| `androidx.navigation:navigation-compose` | Google's artifact publishes **only** android, jvmStubs and linux_x64 — no iOS | **Navigation 3**, in two steps. **A8 (done):** Google's `androidx.navigation3:navigation3-runtime` + `navigation3-ui` 1.2.0 and `androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0, on Android; Nav2 is gone. **A10 (done):** only `navigation3-ui` swapped, to `org.jetbrains.androidx.navigation3:navigation3-ui` 1.2.0. It publishes the same `androidx.navigation3.*` packages (verified by inspecting the JetBrains jar), so no import changed. On Android it is a facade onto Google's `navigation3-ui` 1.2.0; on iOS it ships its own klib. Google's `navigation3-runtime` 1.2.0 and `lifecycle-viewmodel-navigation3` 2.11.0 already publish `ios_arm64` and `ios_simulator_arm64` and stay; Google's `navigation3-ui` does not (re-verified 2026-11-07 for 1.2.1 and 1.3.0-alpha04), hence the swap. The planned `lifecycle-viewmodel-navigation3` swap was dropped: the JetBrains one is an empty facade onto Google's. Deep links use the runtime's multiplatform `androidx.navigation3.runtime.deeplink` API. Re-verified 2026-11-07. | A8 / A10 (ADR-0002) |
| `androidx.compose.*` via Compose BOM | Android-only artifacts: Google's `ui`, `foundation` 1.12.1, `material3` 1.4.0 and `material-icons-core` 1.7.8 publish no iOS variant | **Done in A10.** Direct `org.jetbrains.compose.*` coordinates in the catalog: `ui`, `foundation`, `ui-tooling(-preview)` at the Compose Multiplatform version, `material3` 1.9.0 and `material-icons-core` 1.7.3. Not the Gradle plugin's `compose.*` accessors: in `compose-gradle-plugin` 1.12.2 they are `@Deprecated("Specify dependency directly")`, so the plugin is not needed for dependencies and comes in with A11. On Android each JetBrains artifact is a facade onto an `androidx.compose` one: `ui` / `foundation` 1.12.2 → 1.12.1, the same as the BOM. `material3` has no stable release after 1.9.0 (the 1.10–1.12 line is alpha; 1.12.0-alpha03 → androidx 1.5.0-alpha22); 1.9.0 is what the 1.12.2 plugin pairs with and maps to androidx 1.4.0, the same as the BOM. It asks for ripple 1.9, so androidx `material-ripple` resolves to 1.9.3 instead of the BOM's 1.12.1; that is the pairing material3 1.4.0 itself declares, and no constraint overrides it. `material-icons-core` 1.7.3 → androidx 1.7.6 instead of 1.7.8, whose classes are byte-identical. Verified 2026-11-07. | A10 |
| `R.string` / `R.drawable` via `stringResource` / `painterResource` (~31 call sites, plus `values-ru`) | Android resource system | **Done in A11.** `org.jetbrains.compose.components:components-resources` + `composeResources/`, in the new KMP module `:resources`: the 1.12.2 plugin generates `Res` only under `org.jetbrains.kotlin.multiplatform` or `org.jetbrains.kotlin.jvm`, never in an AGP built-in-Kotlin module | A11 |
| `io.ktor:ktor-client-okhttp` | Android engine | **Done in A5.** No longer declared: `:network` depends on `io.ktor:ktor-client-engine-defaults`, a code-free aggregator whose Gradle Module Metadata maps JVM → `ktor-client-okhttp` and Apple targets → `ktor-client-darwin` (it publishes no Android variant, so Android resolves the JVM one, as `ktor-client-core` already does). DI provides a base `HttpClient`, since the aggregator's engine is only reachable through the no-arg `HttpClient()`. `org.slf4j:slf4j-api` still arrives with the OkHttp engine, as before. | A5 (ADR-0005) |
| `com.google.accompanist:accompanist-permissions` | Android-only | **Done in A7c.** Replaced by **Calf** `com.mohamedrejeb.calf:calf-permissions-core` + `calf-permissions-notifications` 0.14.0. Verified 2026-09-26 and re-verified 2026-10-07 against the 0.14.0 module metadata: iOS variants published, built on Kotlin 2.4.20, Android runtime dependencies are only `org.jetbrains.compose.foundation` 1.12.0 (the A10 target) and `androidx.activity:activity-compose`. Its composable API (`rememberPermissionState`, `isGranted`, `shouldShowRationale`, `launchPermissionRequest`) mirrors Accompanist, so `PermissionView` and its rationale dialog survive. Rejected: `dev.icerock.moko:permissions` 0.20.1 (last release 2025-08; controller + `BindEffect` API with exception-based denial) and `dev.brewkits:grant-core` 2.6.0 (repository only since 2026-01 with two contributors and four minor releases in three months; view-model controller API with its own dialogs; pulls `androidx.fragment`). | A7c |
| `io.github.aghajari:AnnotatedText` | Android-only (renders through `android.text` spans); last release 2024-01. `:html-text:imagecontent` also read `android.text.style.ImageSpan` from it. Missed by the first audit, found while splitting A7. | **Done in A7h.** No library: `:html-text` builds the `AnnotatedString` from ksoup-html events itself, and inline content is created from the `HtmlTag`. Evaluated 2026-10-25 against module metadata and sources: `be.digitalia.compose.htmlconverter:htmlconverter` 1.1.1 (iOS variants published; built on Compose Multiplatform 1.9.3; adds `org.kobjects.ktxml`) covers text and links but drops `<img>` ("inline images" is future work), and its handler is internal, so images would need a marker hack. `com.mohamedrejeb.richeditor:richeditor-compose` 1.2.1 (iOS variants published) renders `<img>` through its own `ImageLoader`, but it is an editor that pulls Material 2, `material3` 1.12.0-alpha03 and JetBrains `markdown`, and would replace `HtmlText` with its `RichText` model. Compose's `AnnotatedString.fromHtml` is Android-only: Compose Multiplatform `ui-text` 1.13.0-beta01 has no other implementation. | A7h |
| `java.time` (`ZonedDateTime`, `DateTimeFormatter`, `ChronoUnit`) and `java.util.concurrent.TimeUnit` | JVM-only APIs, used for RSS publication dates in `:rss-reader` and `:app`, and for the feed expiry in `:rss-reader` | **Done in A7i.** `org.jetbrains.kotlinx:kotlinx-datetime`, in `:rss-reader` only, for its `RFC_1123` format. It returns the stdlib's `kotlin.time.Instant`, so the other modules use `kotlin.time` (`Instant`, `Clock`, `Duration`) and need no new dependency. Verified 2026-10-24 against the 0.8.0 module metadata (released 2026-05-07): `ios_arm64`, `ios_simulator_arm64` and `ios_x64` variants published, and the JVM runtime depends on `kotlin-stdlib` ≥ 2.1.20 alone. | A7i |

## Stays platform-specific — deliberately

| Dependency | Why | iOS counterpart |
| --- | --- | --- |
| `androidx.browser` (Custom Tabs) | Since A7j it is confined to the `:browser` module, behind its `InAppBrowser` interface and reached through `rememberInAppBrowser()`. No library adopted. Evaluated 2026-10-24 against module metadata and sources: `io.github.final-class:webview-multiplatform-mobile` 1.3.0 (last release 2025-11; iOS variants published; built on Kotlin 1.9.23 and Compose Multiplatform 1.6.2) launches the tab as a side effect of composition through a command/consume controller and lets `ActivityNotFoundException` escape. `dev.yjyoon:kinappbrowser` 1.0.0 (its only release, 2025-10) is a global `object` that needs `init(activity)`, opens a default-styled tab with no toolbar colour, title or URL-bar options, and logs failures instead of reporting them. Each wraps the same two platform calls our own interface needs. | `SFSafariViewController` as the iOS `actual` of `rememberInAppBrowser()`, written in B7 |
| `com.google.firebase:firebase-messaging` + Google Services plugin | Android SDK. Since A7e it sits behind the `PushMessaging` interface in `:notification`, implemented in `:firebase`. No library adopted. Evaluated 2026-10-24 against module metadata and sources: `dev.gitlive:firebase-messaging` (2.7.0 and 3.0.0-alpha02; iOS variants published, Android side pulls `kotlinx-coroutines-play-services`) declares a non-suspending `subscribeToTopic(topic: String)` that drops the result on both platforms. `io.github.mirzemehdi:kmpnotifier-push-firebase` 2.0.2 is `suspend` but never awaits the Firebase `Task`, pulls `kmpnotifier-local` (rejected in A7d), registers its own `FirebaseMessagingService` and uses `onNewToken` rather than FID mode. Either would lose the failure that decides whether a subscription is persisted and reported. `io.github.corneloaie:firebase-messaging` and `io.github.the-best-is-best:kfirebase-messaging` were too new or unused to consider. | Firebase iOS SDK (Swift or cinterop) or GitLive behind `PushMessaging`, chosen in C3. GitLive's fire-and-forget topics would need its `ios` escape hatch (`FIRMessaging`) for the completion handler. Deferring push on iOS to after the first release is a legitimate option. |
| `androidx.core:core-splashscreen`, `androidx.activity`, `androidx.appcompat`, `com.google.android.material` | Android entry point | Native iOS launch screen and `UIViewController` host |
| `androidx.lifecycle:lifecycle-process` | Android process lifecycle | iOS app delegate callbacks |
| Android notification channels (`:notification`) | Platform model differs fundamentally. Since A7d it sits behind the `Notifications` interface. Showing a received push as a local notification is Android-only: FCM does not display notification messages while the app is in the foreground, whereas iOS presents them through `UNUserNotificationCenterDelegate`. No library adopted. Evaluated 2026-10-22 against module metadata and sources: `io.github.mirzemehdi:kmpnotifier-local` 2.0.1 has one channel, with `IMPORTANCE_HIGH` hard-coded, no channel choice per notification (requested since 2025-02, PRs still open) and no lock-screen visibility. `io.github.tweener:alarmee` 2.7.1 drops channel descriptions and custom tap extras, has no visibility, and pulls the author's `kmpkit`. Both push modules register their own `FirebaseMessagingService`, which collides with ours — relevant for A7e. | `UNUserNotificationCenter` |

## Toolchain notes

- The project applies no Kotlin Gradle plugin today — AGP supplies Kotlin compilation.
  Going multiplatform means applying `org.jetbrains.kotlin.multiplatform` explicitly, with
  `com.android.kotlin.multiplatform.library` for the Android target of library modules.
  A9 verified that this works per module beside built-in-Kotlin modules. KGP 2.4.21 is already
  on the root classpath through AGP. The KMP library plugin needs `com.android.lint` before
  Android lint runs at all.
- Metro is a Kotlin **compiler plugin**. A3 verified that its Gradle plugin hooks correctly into
  AGP-supplied Kotlin compilation: AGP 9's built-in Kotlin runs every
  `KotlinCompilerPluginSupportPlugin` the same way KGP does. No Kotlin Gradle plugin is needed —
  and none may be applied, since AGP 9 hard-fails on `kotlin.android`, `kapt` and
  `kotlin.multiplatform` alongside built-in Kotlin.
- Navigation 3's UI on non-Android targets requires CMP ≥ 1.10,
  which is why the JetBrains `navigation3-ui` coordinate only came in at A10. Google's
  `navigation3-runtime` needs no CMP and is multiplatform on its own.
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
