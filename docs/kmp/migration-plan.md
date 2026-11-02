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
| A, B (before any iOS target) | `./gradlew build` (compiles everything + Android lint with `warningsAsErrors` + ktlint) and `./gradlew test` |
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
| A7b | External links & email | **Done.** `openUrlByIntent` / `openEmailByIntent` became `UriHandler.openUrl` / `UriHandler.openEmail` in `ui/Utils.kt`, and the drawer and Feedback settings take Compose's multiplatform `LocalUriHandler` instead of a `Context` — no new dependency, no interface of our own, no `Intent` left in either. Both pre-checks passed. Compose UI 1.12.1's `AndroidUriHandler` turns `ActivityNotFoundException` into the `IllegalArgumentException` that the common `UriHandler` contract documents, which is caught and reported through A7a. Its `ACTION_VIEW mailto:` resolves to the same single activity as `ACTION_SENDTO` on an API 37 emulator (Gmail's compose screen; Chrome is not offered). | A7a |
| A7c | Permissions → Calf | **Done.** `accompanist-permissions` 0.37.3 → `calf-permissions-core` + `calf-permissions-notifications` 0.14.0, owned by `:permission` alone (`:app` had a stale direct Accompanist dependency, now dropped); no other resolved version changed. `PermissionView` takes a public `AppPermission` enum (`Notifications`), mapped to Calf's `Permission` privately, so Calf stays an `implementation` detail and the per-platform mapping has one home. The API 33 branch and `Manifest` left `NotificationPermissionView`: Calf maps `Permission.Notification` to `POST_NOTIFICATIONS` from API 33 only and reports `Granted` below. `openNotificationSettings` was deleted rather than put behind an interface — the rationale's confirm button now calls Calf's `PermissionState.openAppSettings()`, which opens `ACTION_APP_NOTIFICATION_SETTINGS` on API 33+, the only level where the rationale can show; `PermissionRationale.Data.onConfirm` went with it. Verified on API 37 (system dialog, rationale after a denial, confirm → the app's notification settings, toggle on after granting there) and API 26 (no dialog, toggle stays on). iOS note for C2/C3: Calf's `Notification` only requests `UNUserNotificationCenter` authorisation, while `RemoteNotification` also calls `registerForRemoteNotifications`, and has no Android mapping, so on Android it reports `Granted` without asking — Android must keep `Notification`; pick the iOS one in `AppPermission`'s mapping when push reaches iOS. | A4 |
| A7d | Local notifications | **Done.** `Notifications` is now a public interface with one method, `show(NotificationData)`. The `NotificationManagerCompat` code became the internal `AndroidNotifications` in `notification/internal/`, bound by the public `NotificationApplicationModule` binding container. It shows through the injected application `Context` instead of a per-call one, and `MessagingService` calls `show(it)`. `java.util.UUID` in `NotificationChannels` → `kotlin.uuid.Uuid`. Channels, per-channel visibility, the tap intent and the permission gate are unchanged. No library adopted: KMPNotifier 2.0.1 and Alarmee 2.7.1 were evaluated and rejected (see the audit). Neither can express our two channels with per-notification choice and visibility, and showing a received push is Android-only anyway, since iOS presents it itself. Verified on an API 36 emulator with injected FCM messages. Both channels keep their names, descriptions, default importance, badge and vibration. Nothing is shown while `POST_NOTIFICATIONS` is denied. Once it is granted, the posts channel is `PUBLIC`, the fallback goes to `misc` as `PRIVATE`, and tapping a notification with a `post_id` opens the article. Found, not caused, by this stage: every tap `PendingIntent` shares one request code and an `Intent` that differs only in extras, so while an older notification is still shown, a newer one opens the older one's extras. A7f fixes this. | A4 |
| A7e | Push messaging | **Done.** The static `Messaging` object became the public `PushMessaging` interface in `:notification` (`subscribeToTopic` / `unsubscribeFromTopic`, suspend, `true` once the push service confirms), with the `Topic` enum nested in it and `topicFromValue` now `Topic.fromValue`. The Firebase code became the internal `FirebasePushMessaging` in `firebase/push/internal/`, bound by the public `FirebaseApplicationModule` binding container; `:firebase` now applies the Metro plugin. `PushNotificationModel` takes the interface; the view models and the settings screen only renamed `Messaging.Topic` → `PushMessaging.Topic`. `MessagingService` gets `PushMessaging` and `PreferencesRepository` through `MessagingDependencies` instead of building a repository from `mainDataStore`. **No library adopted** (see the audit): GitLive `firebase-messaging` 2.7.0 and 3.0.0-alpha02 and KMPNotifier's `kmpnotifier-push-firebase` 2.0.2 both subscribe fire-and-forget, so a failed subscription would be persisted and never reported. Also fixed: `unsubscribeFromTopic` ignored its `topic` and always used `NEW_RETRODROM_POSTS`, as did both failure messages — no visible change while that is the only topic. Verified on an API 36 emulator: subscribe and unsubscribe from settings, subscribe from the feed promo, and re-subscription of the saved topic in `onRegistered` after resetting the Firebase installation. The failure path was not reproducible, as Firebase queues topic operations offline. | A7a, A7d |
| A7f | Deeplinks | **Done.** `android.net.Uri` and `Intent` left `:notification`: `createFeedItemDeeplink` now builds the feed-item deeplink as a `String` with Ktor's multiplatform `URLBuilder`, and `extractDeeplink(data, postId)` takes the launch's data string and push `post_id` instead of an `Intent`, checking the scheme through `parseUrl`. `:notification` gained `io.ktor:ktor-http`, already on the classpath through `:network`, so no new artifact entered the build. Reading both from the `Intent` moved to `MainActivity`, which still hands the result to Nav2 as a `Uri`. JVM tests in `:notification` pin the string to what `Uri.Builder` produced, so `navDeepLink` still matches it. Fixed the stale tap extras A7d found: each tap `Intent` now carries its deeplink as data, so two notifications shown at once get separate `PendingIntentRecord`s. **Nav2's `navDeepLink` stays until A8**, by decision: Navigation 3 1.2.0 has a multiplatform deep link API (see A8), so writing our own matcher now would be thrown away. Verified on an API 36 emulator with injected FCM messages: with two notifications shown, each tap opens its own post. A notification FCM posts while the app is in the background has no data, and its tap opens the post through the `post_id` extra. | A7d |
| A7g | Remaining platform calls | **Done.** `android.text.Html` left `:rss-reader`: `HtmlParser` became `findFirstImageSource`, which reads the first `<img src>` with ksoup-html's `KsoupHtmlParser`, the parser `:html-text` already uses, so no new artifact entered the build. A blank or missing `src` is skipped, so it can no longer become the feed image. `RssDescription.paragraphs` was dropped instead of ported: nothing has read it since `e6774ed` (Oct 2025), when the feed item stopped showing the first paragraph, and `html` still carries the whole description. JVM tests in `:rss-reader` pin the extraction against a real feed description: the featured image wins over inline ones, entities are decoded, and tag and attribute names match case-insensitively. Both Coil `ImageRequest.Builder(LocalContext.current)` calls (`RssFeedItem`, `SocialNetworkDrawerNavView`) now take `LocalPlatformContext.current`. Verified on an API 37 emulator: every feed item shows its featured image. The drawer's `SocialNetworkIcon.Url` branch has no caller today, so only compilation covers it there. | A4 |
| A7h | HTML rendering off AnnotatedText | **Done.** `io.github.aghajari:AnnotatedText` left the build, and `:html-text` dropped `androidx.appcompat`, which it only needed for `HtmlCompat`. The internal `htmlToContent` builds the `AnnotatedString` from ksoup-html events, following `android.text.Html`'s legacy layout: a blank line between blocks, bold relative sizes for `h1`–`h6`, the inline styles, `LinkAnnotation.Url` links, and bulleted list items. List items are now one line apart, where `Html` left a blank line, which has no effect on today's articles. Whitespace collapses, and `script`, `style` and `iframe` content is skipped. `HtmlText` renders it with Material 3's `Text`. Link taps still reach `onLinkClick`. **No library adopted** (see the audit). `InlineContentCreator.create(tag: HtmlTag)` replaced `create(id, span: Any, start, end, tags)`: every opening tag is offered in document order, and the first non-null `InlineTextContent` is placed there. `ImageContentCreator` no longer reads `ImageSpan` / `DynamicDrawableSpan` or `Build`. It takes `src`, `width` and `height` from the tag, keeps bottom alignment, and now uses `alt` as the content description. Found: ksoup-html 0.6.0 leaves numeric entities such as `&#8217;` undecoded in text. The real feed uses them throughout, so the parser runs with `decodeEntities = false`, and text and attributes are decoded once with `ksoup-entities`' `KsoupEntities.decodeHtml`. That artifact was already a runtime dependency of ksoup-html and is now declared, so no new artifact entered the build. JVM tests in `:html-text` pin the layout, entities, styles, links, skipped tags and inline placement. Verified on an API 37 emulator against a `main` build on two real articles (Moto-X news, Bug Too! review): text, headings, inline images, link styling and spacing match screen for screen, and an in-article link opens the Custom Tab. | — |
| A7i | `java.time` → `kotlinx-datetime` | **Done.** No `java.time` or `java.util.concurrent.TimeUnit` left. `:rss-reader` parses `pubDate` with `org.jetbrains.kotlinx:kotlinx-datetime`'s `DateTimeComponents.Formats.RFC_1123`, and `asDateTime(): ZonedDateTime` became `asInstant(): kotlin.time.Instant`. The stdlib type means `:app` needs no kotlinx-datetime: `toRssFeedPublicationTime` takes an `Instant` and measures its age as `Clock.System.now() - this`, which needs no time zone. The project's own `Clock` interface (`currentTimeMillis()`, backed by `System.currentTimeMillis()`) is gone: `:common`'s `ClockModule` provides the stdlib `kotlin.time.Clock` as `Clock.System`, and the feed cache injects that. `RssFeedItemsMediator` now compares the cache's age with a `1.hours` expiry as a `Duration`. The cache API still exposes epoch milliseconds, so the Room schema is unchanged. JVM tests in `:rss-reader` pin the parsing: the feed's `+0000` form, `GMT`, a non-zero offset, a one-digit day, no weekday, and rejection of a non-RFC date. A throwaway comparison with `RFC_1123_DATE_TIME` on 14 inputs gave the same instant for every form WordPress emits. kotlinx-datetime also accepts `UT` and `Z`, and it rejects lower-case names and two-digit years (java.time read those as year 0026). The `RssFeedItem` stability baseline was regenerated: `pubDate` was unstable as `ZonedDateTime` and is still unstable as a cross-module `Instant`. | — |
| A7j | In-app browser | **Done.** `openUrlInCustomTab` (`:app`'s `utils/CustomTabUtils.kt`) left `:app` for a new `:browser` module. It holds the public `InAppBrowser` interface, which has no Android imports, and `rememberInAppBrowser(toolbarColor)`, the composable that returns one. On Android it reads `LocalContext`, which is the Activity, so the tab opens in the app's task. It is a plain function now and becomes `expect`/`actual` in B7, so there is no `CompositionLocal` and nothing in `MainActivity`. The internal `CustomTabsInAppBrowser` owns `androidx.browser`, which left `:app`'s dependencies, and keeps the title and URL-bar hiding. The colour is the caller's choice: `RssItemDescriptionScreen` passes the theme's primary container, so `:browser` needs no Material 3. `open` returns `false` when no browser is installed, and the screen then posts A7b's "no application" message through `UserMessagesViewModel`, obtained in its body rather than as a parameter, so the stability baseline is unchanged. Before, that tap crashed with `ActivityNotFoundException`. **No library adopted** (see the audit): `webview-multiplatform-mobile` 1.3.0 and `kinappbrowser` 1.0.0 were evaluated and rejected. Verified on an API 37 emulator: the globe action opens the article in a Custom Tab with the themed toolbar and title, in the app's task. With Chrome disabled, the globe action and an in-article link each show the snackbar instead of crashing. | A4 |
| A8 | Navigation 2 → Navigation 3 | **Done.** `navigation-compose` left the build for Google's `androidx.navigation3:navigation3-runtime` + `navigation3-ui` 1.2.0 and `androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0 — 1.2.0 rather than ADR-0002's 1.1.7, since Google and the JetBrains mirror are both stable there (ADR amended). The app owns one `NavBackStack<NavKey>`, made in `MainView` with `rememberNavBackStack` and saved through an explicit `SavedStateConfiguration` that registers every `AppDestination` with `subclassesOfSealed`, so it needs no Android reflection. Routes implement `NavKey` through the now `@Serializable` `AppDestination`. The `MainDestination` graph keys and `MainViewState.destination` are gone: nothing switched between retained stacks, so the guide's multi-stack `NavigationState` did not apply. `MainNavActionsImpl` edits the list directly and keeps every Nav2 `popUpTo` / `launchSingleTop` rule. `navigateBack` never pops the last entry, because the display needs one. JVM tests in `:app` pin the stack each action leaves. `NavDisplay` replaced `NavHost`, with the same slide transitions (also for predictive back). The view-model-store entry decorator scopes view models per entry as the back stack entry did, so the explicit `viewModelStoreOwner` parameters went. The drawer reads the top key, so the Settings item now highlights on any settings screen; before, the check never matched. `navDeepLink` → a `UriDeepLinkMatcher` on `LoadingItem`. `MainActivity` matches the `extractDeeplink` string once in `onCreate`, and the back stack starts as `[start feed, LoadingItem]`, the stack `navigate(uri)` produced. A restored stack ignores it, so recreation no longer navigates again, and an unmatched link is ignored instead of crashing `navigate`. `RssLoadingItemScreen` navigates from `LaunchedEffect` instead of during composition. Nav2 was the only requester of `savedstate` 1.5.0 and `navigationevent` 1.1.2, which now resolve to 1.4.0 and 1.1.1. The stability baseline only lost unstable parameters. Verified on an API 37 emulator, debug and minified release: feed switching from the drawer with selection, article and back, settings sub-screen with the top-bar back and gesture back, back from the start feed exiting, the stack restored after process death (debug) and with "don't keep activities" (release), and cold-start deep links through both the data URI and the `post_id` extra. FCM injection itself was not repeated: that image cannot root. | A4, A7f, ADR-0002 |

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
| A9 | Toolchain spike | **Done.** Spike on `:common`, reverted; only docs landed. `org.jetbrains.kotlin.multiplatform` 2.4.21 + `com.android.kotlin.multiplatform.library` 9.4.1 in one module coexist with built-in Kotlin in the other 13 (AGP's hard-fail is per module), and `:common` compiled for `android`, `iosArm64` and `iosSimulatorArm64` with its Metro `ClockModule` generated on both. The full gate stayed green, including R8 release and the configuration cache. **Convention plugins belong in an included `build-logic` build, not `buildSrc`** (ADR-0006): with AGP and KGP on `buildSrc`'s classpath, every versioned `alias(…)` of an AGP or KGP plugin ID fails ("already on the classpath with an unknown version"), which meant rewriting all 14 scripts. `build-logic` takes them `compileOnly` and changed no other script. Findings the convention must carry: the KMP library plugin registers **no Android lint** until `com.android.lint` is applied (then `warningsAsErrors` holds, and `commonMain` is linted against minSdk 26); host tests need `withHostTest {}`; and root `./gradlew test` skips KMP modules, whose task is `testAndroidHostTest`, so the convention registers a `test` aggregate. ktlint already covers every KMP source set. **Xcode is needed before the first `commonTest` (B2):** iOS klibs compile with Command Line Tools only, but `build` runs `iosSimulatorArm64Test`, whose link fails with `MissingXcodeException`. KGP 2.4.21 adds one Gradle deprecation (`KotlinNativeBundleArtifactsTypes`) that fails under Gradle 10. | A1–A8 |
| A9b | `buildSrc` → `build-logic` | Per ADR-0006. `buildSrc`'s constants move into an included `build-logic` build with an Android-library and an application convention, which every module applies. Constants in `build-logic` are only visible to scripts that apply one of its plugins. `buildSrc` is deleted. The KMP convention from A9 lands here or in B1. Still Android-only. | A9, ADR-0006 |
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
| B1 | `:common` | First real KMP module, on A9b's `build-logic` KMP convention. | low |
| B2 | `:html-text:api`, `:html-text`, `:html-text:imagecontent` | Compose + ksoup + Coil, no DI. No Android API left since A7h; the converter's JVM tests move to `commonTest`. | low |
| B3 | `:preferences` | DataStore path via `expect`/`actual`. | low |
| B4 | `:network` | Source-set configuration only: the existing JVM tests move to `commonTest` and the dependencies move to `commonMain`. The engine question was settled in A5 (ADR-0005) — `ktor-client-engine-defaults` already resolves Darwin for the Apple targets, so no engine work is left here. | low |
| B5 | `:network:cache` | `expect`/`actual` database builder and KSP running for the iOS targets. The driver and the Room 3 API surface are already done in A6, so this stage is build configuration rather than code. | medium |
| B6 | `:rss-reader` | Paging is already multiplatform; mostly follows B4/B5. | low |
| B7 | `:permission`, `:browser` | Source-set moves. `:permission`: A7c already put it on Calf, which publishes iOS variants. `:browser`: `InAppBrowser` moves to `commonMain` and `rememberInAppBrowser` becomes `expect`, with the Custom Tabs `actual` on Android and an `SFSafariViewController` one on iOS, which iOS needs in order to compile. C2 then checks the iOS one on a device. | low |
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
| `:browser` | yes | no | Added in A7j. `androidx.browser` stays behind `InAppBrowser` deliberately |
| `:common` | yes | no | A7i replaced its `Clock` with the stdlib `kotlin.time.Clock` |
| `:firebase` | n/a | n/a | Android-only until C3 |
| `:html-text` | yes | no | A7h replaced AnnotatedText with its own ksoup converter |
| `:html-text:api` | yes | no | `InlineContentCreator` takes an `HtmlTag` since A7h |
| `:html-text:imagecontent` | yes | no | A7h removed the `android.text` spans |
| `:network` | yes | no | A5 removed the last Android-only dependency |
| `:network:cache` | yes | no | A6 put it on Room 3 with a driver-based builder |
| `:notification` | no | no | Common shape only |
| `:permission` | yes | no | A7c put it on Calf |
| `:preferences` | no | no | |
| `:rss-reader` | yes | no | A7g removed `android.text.Html`, A7i `java.time` |
| `:shared` | — | — | Does not exist yet (B9) |

## Open decisions

None. All six current ADRs are accepted; raise a new record if a stage turns up a decision that
constrains later work.
