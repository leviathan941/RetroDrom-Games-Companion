# RetroDrom Games Companion — agent guide

Native Android client for the [RetroDrom Games](https://retrodrom.games/) site (a WordPress
blog): browses the site's RSS categories/posts, opens articles, and receives push
notifications. Content is mostly Russian (`values-ru`); UI is Jetpack Compose only.

- Toolchain: Kotlin targeting JVM 17 (no Gradle toolchain is pinned, so it builds on whichever
  JDK runs Gradle). The Android Gradle Plugin supplies Kotlin compilation itself — no module
  applies `org.jetbrains.kotlin.android`. KSP runs in `:network:cache` only, for Room; DI is a
  compiler plugin (Metro) and needs none.
- `minSdk` is 26 — check it before reaching for a newer platform API. App id
  `org.segowski.retrodromgames`, code namespace `org.leviathan941.retrodromcompanion`.
- Key libs: Compose Multiplatform (`org.jetbrains.compose.*` coordinates) + Material 3, Navigation 3, Metro (DI), Ktor (WordPress REST API and the
  RSS feed), Room (feed cache), DataStore Preferences, Paging 3, Coil 3, Firebase Messaging,
  AboutLibraries.

## Modules

| Module | Purpose |
| --- | --- |
| `:app` | UI layer: Compose screens, drawer, top bar, navigation, view models, themes. |
| `:app:migration` | Versioned on-device app data migrations (stale files, orphaned databases). `:app` only starts the run and gates its first frame on it; the step registry is `internal`. |
| `:browser` | In-app browser: the `InAppBrowser` interface and `rememberInAppBrowser()`, on Custom Tabs (`androidx.browser`). |
| `:common` | Shared constants (site base URL), DI keys, request codes, the `kotlin.time.Clock` binding. |
| `:firebase` | FCM messaging service and the Firebase implementation of `PushMessaging` (topic subscriptions). |
| `:html-text`, `:html-text:api`, `:html-text:imagecontent` | Standalone Compose library (`org.leviathan941.compose.htmltext`) that renders HTML as `AnnotatedString`, with pluggable inline content (e.g. `<img>`). Keep it app-agnostic. |
| `:network` | Ktor client for the WordPress REST API and the RSS feed (`WpNetworkClient` / `WpKtorClient`). |
| `:network:cache` | Room database caching feed categories and channel items, read behind `FeedCacheProvider` and written behind `FeedCacheMutator`. |
| `:notification` | Notification channels, builders, helpers; the `Notifications` and `PushMessaging` interfaces. |
| `:permission` | Compose permission request and rationale UI on Calf (`calf-permissions`). |
| `:preferences` | DataStore-backed UI/promo preferences with migrations. |
| `:resources` | The first KMP module. UI strings (with `values-ru`) and drawables as compose resources in `commonMain/composeResources`, read through its public `Res`; plus, in `androidMain/res`, the Android resources Android itself reads for push (channel strings, notification icon and colour, default channel id). `:app` keeps only its identity: label, launcher icons, theme. |
| `:rss-reader` | Pages the cached feed with Paging 3 and refills it via a `RemoteMediator`. |

Versions live in `gradle/libs.versions.toml`; SDK/JVM/app-version constants live in
`build-logic/convention/src/main/kotlin/org/leviathan941/retrodromcompanion/` (bump `AppVersion`
for releases).
Read them from there rather than restating them here — version numbers in this file go stale.

## Conventions

- Build logic lives in the included build `build-logic` (ADR-0006), not `buildSrc`. Every module
  applies one of its convention plugins instead of `com.android.*` directly:
  `retrodrom.android-library` (every Android-only library), `retrodrom.kmp-library` (KMP modules:
  Android, `iosArm64`, `iosSimulatorArm64`, plus `com.android.lint`, which the KMP library plugin
  needs to register any lint task) or `retrodrom.android-application` (`:app`). They set
  the SDK levels, the JVM 17 targets and lint's `warningsAsErrors`; a module script keeps its
  `namespace` and whatever is its own. Its constants are visible only to a script that applies one
  of those plugins. AGP and KGP are `compileOnly` there: at runtime they come from the root
  script's `apply false` list, which is where their versions are set.
- Every library module enables `ExplicitApiMode.Strict` through its convention plugin —
  public declarations need explicit `public` and explicit return types. `:app` does not.
- Compose resources (`org.jetbrains.compose` plugin) generate `Res` only in a KMP or Kotlin/JVM
  module, never under AGP's built-in Kotlin, which is why they live in `:resources`. Their
  format arguments must be positional (`%1$s`, `%1$d`); `\n`, `\t` and `\uXXXX` are unescaped,
  `\'` is not. Their vector parser takes literal `#` colours only (an `@android:color/…` throws
  at runtime) and ignores `android:tint`, so a vector drawn by `Image` without a `colorFilter`
  must carry its final colour.
- Package layout per module: public API at the top level (or `api/`), implementation under
  `internal/` or `impl/`; Metro binding containers and the app graph under `di/`.
- DI is [Metro](https://github.com/ZacSweers/metro). `MainApplication` owns the single
  `@DependencyGraph(AppScope::class)`; framework-instantiated classes reach it through the
  `Application`. Any class carrying `@ContributesTo` / `@ContributesBinding` / `@ContributesIntoSet`
  must be **public** for Metro to merge it — a contribution from an `internal` class is rejected
  ("its module is not a friend module to this one") and shows up as a `[Metro/MissingBinding]`
  error naming it under `similar bindings:`. Its members may be `internal`, which is why the
  `@Binds`-only binding containers still exist: they keep the implementations internal. Hilt
  tolerated internal implementations only because Dagger generates Java, which ignores Kotlin
  `internal`. `@Provides` must sit in an `object` or a `companion object`, never directly in an
  abstract container. View models are multibound via `metrox-viewmodel`; retrieve them with
  `metroViewModel()` / `assistedMetroViewModel()`.
- Metro's graph reports are off by default: Metro marks them debugging-only because they add
  noticeable compile overhead. Request them with the `metro.reportsDestination` property (a path
  relative to the module's build directory). The path is not a compile input, so force the
  compilation: `./gradlew :app:compileDebugKotlin --rerun -Pmetro.reportsDestination=reports/metro`.
  `app/build/reports/metro/<variant>/graph-dump/` then names every binding, its kind, its scope
  and its source location — the practical way to diff the graph across a change.
- Source files and module `build.gradle.kts` scripts carry the GPL v3 header
  (`RetroDrom Games Companion / Copyright (C) …`). Only the root `build.gradle.kts`,
  `settings.gradle.kts`, `build-logic` and `app/build.gradle.kts` carry the Apache 2.0 header.
  Copy a neighbouring file's header for its shape, but set the year deliberately: a new file
  takes the current year, while a file that is moved, renamed or rewritten in place keeps the
  year it already had.
- Style is ktlint (`android_studio` code style) configured in `.editorconfig`: trailing commas
  allowed, multiline signatures from 2 parameters, import ordering rule disabled. The
  [compose-rules](https://mrmans0n.github.io/compose-rules/) ruleset runs alongside, minus
  `vm-injection-check`: new composables obtain view models in their body, not as default
  parameters, so they add no entries to the stability baseline.
- ktlint runs in the build through `org.jlleitschuh.gradle.ktlint`. The root `build.gradle.kts`
  applies it to every project in `allprojects`, with the ktlint version and the compose-rules
  ruleset, so a new module needs no line of its own. It skips `build/generated/`, which the
  compose resources generator adds to the KMP source sets. Keep it there: from a `buildSrc` convention
  plugin it could not see AGP and KGP and silently linted no Kotlin sources, and a `build-logic`
  convention has not been tried. `build-logic` itself is not checked.
- detekt runs in the build through `dev.detekt`, applied from the same root `allprojects` block
  with `detekt.yaml` on top of the default config and the compose-rules detekt ruleset (its
  `ViewModelInjection` is off, matching ktlint). It is a 2.0 alpha because compose-rules is built
  against it and 1.23 is built on Kotlin 2.0, behind this project's 2.4. 2.0 renamed rules and
  thresholds (e.g. `allowedLines`), and an unknown key in `detekt.yaml` fails the run. `check`
  runs only the plain `detekt` task (no type resolution), pointed at all of `src/` so KMP and
  Android variant source sets are covered. There is no baseline.

## Build & verify

```sh
./gradlew assembleDebug        # build the app
./gradlew lint                 # Android lint, every module
./gradlew ktlintCheck          # ktlint, every module and its *.gradle.kts
./gradlew detekt               # detekt, every module
./gradlew ktlintFormat         # apply ktlint's auto-fixes
./gradlew build                # compile everything (also runs lint, ktlintCheck and detekt)
```

ktlint's per-source-set tasks cache their findings: after deleting a file that had a violation,
the check still reports it until `./gradlew clean` (or `--rerun` on the `runKtlintCheckOver…`
task).

The only tests are JVM unit tests: `:network`'s cover the RSS response mapping and feed URL
building, `:notification`'s the feed-item deeplink string; `./gradlew test` runs them.
Compilation, Android lint, ktlint and detekt are otherwise the gate.

`build` also runs the Compose stability analyzer's `:app:{debug,release}StabilityCheck` against
the committed baseline in `app/stability/`: a composable whose parameters become less stable
fails the build. When a change is intended (a new or changed composable), regenerate the baseline
with `./gradlew :app:stabilityDump` and commit it with the change.

Every module sets `lint { warningsAsErrors = true }`, so a lint *warning* in any module
fails the build — the exit code is the gate, and per-module reports under
`<module>/build/reports/` only matter when diagnosing a failure. **Lint is clean: there is
no baseline file.** Fix a new finding, or suppress it narrowly at the declaration with
`@SuppressLint("IssueId")` plus a comment saying why; adding a baseline to hide one is a
last resort.

The one exception is the version-update checks (`NewerVersionAvailable`, `GradleDependency`,
`AndroidGradlePluginVersion`): the root `lint.xml`, which every module inherits, lowers them to
`informational`, which `warningsAsErrors` does not promote. A new upstream release therefore
shows up as a `Hint` in `:app`'s lint report (it flags `gradle/libs.versions.toml`) instead of
failing the build.

`:app` also sets `checkDependencies = true`, so its lint analyses the library modules'
sources as well. Library modules that need a resource depend on `:resources` and read it from
there (`org.leviathan941.retrodromcompanion.resources.R` or `Res`); no module declares
placeholder resources for `:app` to override.

`local.properties` (SDK path) and `app/google-services.json` are git-ignored but present
locally; a build without the latter fails in the Google Services plugin.

## KMP migration (in progress)

The project is being ported to Kotlin Multiplatform and Compose Multiplatform so it can ship on
iOS. Read [`docs/kmp/migration-plan.md`](docs/kmp/migration-plan.md) before starting any work
that touches dependencies, modules or the build — it holds the phase/stage breakdown and the
per-module status tables, and it is the source of truth for what is already done.

- [`docs/kmp/library-audit.md`](docs/kmp/library-audit.md) records the verified multiplatform
  status of every dependency. Check it before assuming a library does or does not support iOS,
  and re-verify the row against the artifact's Gradle Module Metadata before acting on it.
- [`docs/adr/`](docs/adr/README.md) holds the decisions that constrain later stages. Do not
  re-litigate an `Accepted` record; raise a new one instead.
- Working rules for the port: one stage per branch, each ending with a green gate and a working
  Android app; never combine a dependency swap with a source-set move; update the status tables
  in the same commit as the work.

## Plans & reports

Plan and report documents an agent produces go in `.local/` at the repository root —
plans in `.local/plans/`, reports in `.local/reports/` — one Markdown file per document,
named in short kebab-case. The directory is git-ignored, so these notes sit next to the
code they describe without ever entering a commit or a review.

Use it for per-task working notes worth re-reading later. Throwaway working files (build
logs, screenshots, one-off scripts) stay in the session scratchpad, which is discarded with the
session.

Documents that outlive a task belong in `docs/` and are committed: durable plans under
`docs/<topic>/`, decisions as numbered records under `docs/adr/`.
