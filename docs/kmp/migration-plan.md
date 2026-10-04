# KMP / Compose Multiplatform migration plan

**Goal:** ship this app on iOS. Move as much of the codebase as possible to Kotlin Multiplatform
and Compose Multiplatform, and keep the remaining platform-specific code minimal and behind
narrow interfaces.

This is the living master document. Update the status tables at the end of every stage.
Durable decisions go to [`docs/adr/`](../adr/README.md); throwaway working notes stay in `.local/`.

## Strategy

Three phases, in order. The point of the ordering is that nothing risky is ever stacked on
anything else risky.

**Phase A — become KMP-ready while staying Android-only.** No multiplatform source sets yet.
Replace every dependency that has no multiplatform story with one that does, and push the
genuinely platform-specific APIs behind interfaces. Every stage is verifiable with the existing
Android gate and leaves a shippable app.

**Phase B — multiplatformise modules bottom-up.** Convert modules to KMP source sets following
the dependency graph from the leaves. Android stays the only running target; iOS compiles.

**Phase C — the iOS app.** Xcode project, platform `actual`s, first launch, release plumbing.

## Working agreement

- **One stage = one branch = a green build.** A stage that cannot end with a working Android app
  is too big; split it.
- **One change at a time.** Never combine a dependency swap with a source-set move. If a stage
  has to touch both, it is two stages.
- **Decisions are written down.** Anything that constrains later stages gets an ADR before the
  code lands, not after.
- **The status tables below are the source of truth** for what is done. Update them in the same
  commit as the work.
- Per-stage working notes (what was tried, what broke) go to `.local/plans/` and `.local/reports/`.
  They are not part of the repository history.

## Verification gates

| Phase | Gate |
| --- | --- |
| A, B (before any iOS target) | `./gradlew build` (compiles everything + Android lint with `warningsAsErrors`) and `./gradlew test` |
| B (once a module has iOS targets) | the above **plus** `./gradlew iosSimulatorArm64Test` for that module |
| C onwards | the above **plus** `./gradlew linkDebugFrameworkIosSimulatorArm64`, and a manual smoke run on both platforms |

Android lint is clean and has no baseline. Keep it that way — the exit code is the gate.

## Phase A — KMP-ready on Android

Two parts. Everything that can be done without touching the build toolchain is done first, under
the unchanged Android gate; the toolchain change then happens once, against a codebase that is
already on its final libraries.

### A1–A8 — dependency migration, toolchain untouched

No Gradle plugin changes beyond what a library swap requires. AGP keeps supplying Kotlin
compilation, the Compose BOM stays, and every stage is fully verified by `./gradlew build`.

| # | Stage | Outcome | Blocked by |
| --- | --- | --- | --- |
| A1 | Logging → Kermit | **Done.** `android.util.Log` gone from all 15 call sites; `Logger` facade in `:common` (`common.logging.Logger`), tagged instances instead of per-call-site tags, with `Logger.setMinLevel(LogLevel)` as the single config entry point. `:html-text:imagecontent` uses Kermit directly to stay app-agnostic. | — |
| A2 | Coil network layer | **Done.** `coil-network-okhttp` → `coil-network-ktor3`. `MainApplication` is Coil's `SingletonImageLoader.Factory` and builds its `HttpClient` on the `HttpClientEngine` `:network` provides (now `@Singleton`), so images and the WordPress client share one engine. Coil shares the engine rather than the WordPress client, which carries content negotiation and a site-specific `defaultRequest`. | — |
| A3 | Metro spike | **Done.** Metro 1.4.4 verified on `:preferences` under AGP-supplied Kotlin: the compiler plugin reaches the kotlinc command line, FIR/IR codegen and graph validation run, `javax.inject` qualifiers and cross-module Hilt `@Module`s are honoured, and Metro coexists with `:app`'s Hilt/KSP. R8, `explicitApi = Strict`, lint and the configuration cache are all clean. Spike code reverted; only these notes and the ADR amendment landed. | — |
| A4 | Hilt → Metro | **Done.** Hilt, `androidx.hilt` and the Hilt KSP processor are gone; KSP now runs only in `:network:cache`, for Room. `MainApplication` owns a single `@DependencyGraph(AppScope::class)`; `MainActivity` and `:firebase`'s `MessagingService` reach it through the `Application` instead of `@AndroidEntryPoint`. The 6 view models and 8 call sites moved to `dev.zacsweers.metro:metrox-viewmodel` + `metrox-viewmodel-compose` 1.4.4 (`metroViewModel()` / `assistedMetroViewModel()`). `DiKeys`' string `@Named`s became typed `@Qualifier`s in `:common`, alongside a project `@ApplicationContext`. | A3, ADR-0001 |
| A5 | Ktor engine behind DI | **Done.** No module names an engine any more: `:network` depends on `io.ktor:ktor-client-engine-defaults`, whose metadata maps JVM → OkHttp and Apple → Darwin, and DI provides a base `HttpClient` (`HttpClientModule`) instead of an `HttpClientEngine`. `HttpClientFactory` became `WpHttpClientFactory` and derives with `baseClient.config { … }`; Coil takes the base client through the app graph. One engine, one connection pool, no `expect`/`actual`. R8 keeps the ServiceLoader wiring — verified on the minified release build, no keep rule. | A4, ADR-0005 |
| A6 | Room 2.x → Room 3 | **Done.** `androidx.room` 2.8.5 → `androidx.room3` 3.0.3 across `:network:cache` (10 files), `room { }` → `room3 { }`, and a driver-based builder on `BundledSQLiteDriver` (`androidx.sqlite:sqlite-bundled` 2.7.1) with `setQueryCoroutineContext(Dispatchers.IO)`. `RoomFeedCategoryDao.addAll`/`clear` are now suspend, so every DAO operation is. Three renames the ADR had not foreseen: `@TypeConverter(s)` → `@ColumnTypeConverter(s)`, `withTransaction` → `withWriteTransaction` (there is no `room3-ktx`), and `PagingSource` now needs `room3-paging`'s `PagingSourceDaoReturnTypeConverter` registered via `@DaoReturnTypeConverters`. Schema files and `user_version` unchanged; upgrade over a Room 2 install with real cached data verified offline on an API 37 emulator. Costs ~1.2 MB per install in bundled-SQLite native libs. | A4, ADR-0004 |
| A7 | Platform APIs isolated | Custom Tabs, permissions, notifications, FCM and anything taking a `Context` sit behind interfaces declared in common-ready modules. Split by functionality into A7a–A7j below; each sub-stage is its own branch. | A4 |
| A7a | Toasts → Snackbar | **Done.** No `android.widget.Toast` left. An app-scoped `UserMessages` (`app/message/`, a `Channel` read through `receiveAsFlow`, so a message posted while nothing collects is shown once later rather than dropped) carries a `UserMessage` — resolved `Text` or a `Resource` with format args, which A11 turns into a `StringResource`. The app-scoped `PushNotificationModel` posts to it directly; the UI goes through `UserMessagesViewModel`, a stateless view model any screen can obtain: screens post with `show`, and `MainView` is the single collector, feeding one `SnackbarHost` above the drawer (`SnackbarDuration.Short`). No `CompositionLocal`. `copyToClipboard` lost its `Context`; `PushNotificationModel` lost its `Application`. `openUrlByIntent` / `openEmailByIntent` still take a `Context` until A7b. Verified on an API 26 emulator (clipboard label, no mail app, no browser for a drawer link); the push-failure path was not reproducible, as Firebase queues topic operations offline. | A4 |
| A7b | External links & email | Opening a link in an external app (`openUrlByIntent`: the drawer, Feedback settings) and composing an email (`openEmailByIntent`: Feedback settings) stop taking a `Context` and building `Intent`s in `ui/Utils.kt`; "no app found" goes to A7a. Candidate: Compose's own multiplatform `LocalUriHandler`, no new dependency. Verify first what its Android implementation does when no app handles the URI (we must still be able to report it), and whether `mailto:` through it reaches mail apps the way today's `ACTION_SENDTO` does; if either fails, a small interface of our own in `:app` without Android imports, provided by a `CompositionLocal` from `MainActivity`. | A7a |
| A7c | Permissions → Calf | `accompanist-permissions` → `com.mohamedrejeb.calf:calf-permissions-core` + `calf-permissions-notifications` (see [`library-audit.md`](library-audit.md) for the comparison with moko-permissions and Grant). Dependency swap only. `PermissionView` takes a platform-neutral permission instead of a `Manifest` string, and the POST_NOTIFICATIONS / API 33 branch leaves `NotificationPermissionView`; check that Calf reports "granted" below API 33. `openNotificationSettings` moves behind an interface. | A4 |
| A7d | Local notifications | `Notifications` becomes a public interface (show a `NotificationData`) with an internal Android implementation bound through a Metro binding container. The per-call `Context` parameter goes, and `java.util.UUID` in `NotificationChannels` → `kotlin.uuid.Uuid`. **No KMP library chosen yet:** discuss and search for one when the stage starts, before designing our own interface. | A4 |
| A7e | Push messaging | The static `Messaging` object becomes an injectable interface (topics, subscribe/unsubscribe) declared in `:notification`, with the Firebase implementation in `:firebase` bound through Metro. `PushNotificationModel` and the settings UI depend on the interface. `MessagingService` takes `PreferencesRepository` from the graph instead of building it from `mainDataStore`. **No KMP library chosen yet:** discuss and search for one when the stage starts, before designing our own interface. | A7a, A7d |
| A7f | Deeplinks | `android.net.Uri` and `Intent` leave `:notification`: the feed-item deeplink is built and parsed from a `String` into a common model, and reading it from the `Intent` stays in `MainActivity`. Navigation goes by typed route instead of Nav2's `navDeepLink`, removing a Nav2 feature A8 would otherwise have to replace. **No KMP library chosen yet:** discuss and search for one when the stage starts, before designing our own interface. | A7d |
| A7g | Remaining platform calls | `:rss-reader`'s `HtmlParser` moves from `android.text.Html` to ksoup (already in the catalog, used by `:html-text`), with JVM tests pinning the paragraph and image extraction. Coil's `ImageRequest.Builder(LocalContext.current)` → `LocalPlatformContext.current`. | A4 |
| A7h | HTML rendering off AnnotatedText | `io.github.aghajari:AnnotatedText` 1.0.3 (Android-only, last release 2024-01) leaves `:html-text`, where `HtmlText.kt` renders through its `fromHtml`. `:html-text:imagecontent` stops reading `android.text.style.ImageSpan` / `DynamicDrawableSpan`, and `:html-text:api`'s `InlineContentCreator` stops receiving the Android span as `span: Any` — the inline-content contract gets a platform-neutral shape. `:html-text` stays app-agnostic. **No KMP library chosen yet:** discuss and search for one when the stage starts; building the `AnnotatedString` from ksoup, which `:html-text` already uses, is the fallback. Rendering of real articles (text styles, links, inline images) compared before and after. | — |
| A7i | `java.time` → `kotlinx-datetime` | `ZonedDateTime` / `DateTimeFormatter.RFC_1123_DATE_TIME` in `:rss-reader`'s `Utils.kt` (the public `asDateTime()`), and `ZonedDateTime` / `ChronoUnit` in `:app` (`toRssFeedPublicationTime`, `RssFeedItem`) move to `org.jetbrains.kotlinx:kotlinx-datetime` — its RFC 1123 format and multiplatform status to be verified against Gradle Module Metadata first. `java.util.concurrent.TimeUnit` in `RssFeedItemsMediator` → `kotlin.time.Duration`. JVM tests pin the RSS date parsing. | — |
| A7j | In-app browser | `openUrlInCustomTab` (`utils/CustomTabUtils.kt`, used by `RssItemDescriptionScreen`) goes behind an interface declared in `:app` without Android imports, provided by a `CompositionLocal` from `MainActivity` (Custom Tabs need an Activity context). The Android implementation owns `androidx.browser`; iOS gets `SFSafariViewController` in C2. Split out of A7b, which had bundled it with external links and the clipboard. **No KMP library chosen yet:** discuss and search for one when the stage starts, before designing our own interface. | A4 |
| A8 | Navigation 2 → Navigation 3 | Back stack owned by the app as a `SnapshotStateList`; drawer and top bar reworked onto it. Type-safe `@Serializable` routes survive as back stack keys. Uses Google's `androidx.navigation3` 1.1.7 — the JetBrains coordinate swap happens in A10. Largest Phase A stage after A4. | A4, A7f, ADR-0002 |

**A7 scope boundary.** Not part of A7, so that nobody looks for it there: string and plural
resources read through `Context` / `Resources` (`MainViewModel`, the notification channel names,
`toRssFeedPublicationTime`) go with A11; the DataStore and Room `Context`s go with B3 and B5; dynamic
colour and the `Build.VERSION` theme checks become `expect`/`actual` in B9; `MainActivity`,
`MainApplication`, `MessagingService` and `:app:migration` are Android entry points and stay so.
The clipboard needs no A7 stage either: `Clipboard` is already multiplatform, and the two Android-only
bits left in `copyToClipboard` after A7a — building the `ClipEntry` from a `ClipData` (iOS uses
`ClipEntry.withPlainText`) and skipping the confirmation from API 33, where the system shows its own —
become `expect`/`actual` in B9.

**A7h and A7i were found while splitting A7.** The first audit had missed both gaps. They are
dependency and API replacements rather than isolations, but they belong in this part of Phase A
(before the toolchain change) and the stage numbers A9–A11 are referenced by ADR-0002, so they
take A7 suffixes instead of renumbering.

### A9–A11 — toolchain and Compose

These cannot move earlier: the Compose Multiplatform plugin is precisely what requires the new
toolchain, and the resource migration depends on it.

| # | Stage | Outcome | Blocked by |
| --- | --- | --- | --- |
| A9 | Toolchain spike | `org.jetbrains.kotlin.multiplatform` + `com.android.kotlin.multiplatform.library` applied alongside AGP 9.4; convention plugins in `buildSrc` so 13 modules do not each hand-roll a KMP block. Success = one module compiles for `android` and `iosSimulatorArm64`. | A1–A8 |
| A10 | Compose BOM → Compose Multiplatform | Compose artifacts come from the CMP Gradle plugin's `compose.*` accessors. Includes swapping the Navigation 3 coordinates to the JetBrains ones — a version catalog change, no imports touched. Still Android-only at runtime. Note A4 already put `org.jetbrains.androidx.lifecycle:*` and `org.jetbrains.compose.*` on the Android classpath through `metrox-viewmodel-compose`; they resolve as metadata-only facades onto the `androidx.*` artifacts and changed no resolved version. | A9 |
| A11 | Resources → `compose.resources` | `R.string` / `R.drawable` replaced at ~31 call sites; `values-ru` becomes `composeResources/values-ru`. | A10 |

**A4 finding worth carrying forward.** Metro merges a contribution only when the class carrying
`@ContributesTo` / `@ContributesBinding` is visible to the merging module: an `internal` one is
discovered and then rejected ("its module is not a friend module to this one"). Hilt tolerated
`internal` implementations because Dagger generates Java, which ignores Kotlin `internal`. The
working shape is a **public** binding container with `internal` members — which is what the Hilt
modules already were — so `@Binds`-only containers were kept rather than folded into
`@ContributesBinding` on the implementations. Also: `@Provides` cannot live in an abstract
container; it belongs in an `object` or the container's `companion object`.

**Risk retired.** This ordering carried one risk: that Metro would need a Kotlin Gradle plugin
applied explicitly, forcing a piece of A9 forward. **A3 settled it — it does not.** AGP's built-in
Kotlin runs `KotlinCompilerPluginSupportPlugin`s exactly as KGP does, and Metro is one. Note also
that the escape hatch ADR-0001 named was never available: AGP 9 hard-fails if
`org.jetbrains.kotlin.android`, `kotlin-kapt` or `org.jetbrains.kotlin.multiplatform` is applied
alongside built-in Kotlin. Had A3 failed, the only routes would have been a project-wide toolchain
regression or pulling A9 forward wholesale.

**Exit criterion for Phase A:** no Android-only dependency remains except the ones listed as
deliberate in [`library-audit.md`](library-audit.md).

## Phase B — multiplatformise modules

Bottom-up along the real dependency graph.

| # | Module | Main work | Risk |
| --- | --- | --- | --- |
| B1 | `:common` | First real KMP module; validates the A9 convention plugins. | low |
| B2 | `:html-text:api`, `:html-text`, `:html-text:imagecontent` | Compose + ksoup + Coil, no DI. Pure once A7h has replaced the Android-only `AnnotatedText` library and `android.text.style` spans. | low |
| B3 | `:preferences` | DataStore path via `expect`/`actual`. | low |
| B4 | `:network` | Source-set configuration only: the existing JVM tests move to `commonTest` and the dependencies move to `commonMain`. The engine question was settled in A5 (ADR-0005) — `ktor-client-engine-defaults` already resolves Darwin for the Apple targets, so no engine work is left here. | low |
| B5 | `:network:cache` | `expect`/`actual` database builder and KSP running for the iOS targets. The driver and the Room 3 API surface are already done in A6, so this stage is build configuration rather than code. | medium |
| B6 | `:rss-reader` | Paging is already multiplatform; mostly follows B4/B5. | low |
| B7 | `:permission` | Source-set move only: A7c already put it on Calf, which publishes iOS variants. | low |
| B8 | `:notification` | Android channels vs `UNUserNotificationCenter`; only the common shape moves. | medium |
| B9 | UI extraction | Compose screens, view models and themes move from `:app` to a new `:shared`; `:app` becomes a thin Android entry point. | high |

`:app:migration` and `:firebase` stay Android-only for now — see the deliberate list.

## Phase C — the iOS app

| # | Stage | Outcome |
| --- | --- | --- |
| C1 | `iosApp` | Xcode project, `MainViewController` hosting the shared Compose UI, first launch on the simulator. |
| C2 | iOS `actual`s | `SFSafariViewController`, permissions, local notifications, Darwin Ktor engine wired. |
| C3 | Push on iOS | APNs via the Firebase iOS SDK or GitLive. Optional for the first release. |
| C4 | Release plumbing | Bundle id, signing, versioning shared with `buildSrc`, store metadata. |

## Module status

| Module | Phase A ready | Phase B done | Notes |
| --- | --- | --- | --- |
| `:app` | no | no | Becomes a thin Android host in B9 |
| `:app:migration` | n/a | n/a | Android-only by design |
| `:common` | no | no | |
| `:firebase` | n/a | n/a | Android-only until C3 |
| `:html-text` | no | no | |
| `:html-text:api` | no | no | |
| `:html-text:imagecontent` | no | no | |
| `:network` | yes | no | A5 removed the last Android-only dependency |
| `:network:cache` | yes | no | A6 put it on Room 3 with a driver-based builder |
| `:notification` | no | no | Common shape only |
| `:permission` | no | no | |
| `:preferences` | no | no | |
| `:rss-reader` | no | no | |
| `:shared` | — | — | Does not exist yet (B9) |

## Open decisions

None. All five current ADRs are accepted; raise a new record if a stage turns up a decision that
constrains later work.
