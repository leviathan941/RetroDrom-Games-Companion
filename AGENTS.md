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
- Key libs: Compose + Material 3, Navigation Compose, Metro (DI), Ktor (WordPress REST API and the
  RSS feed), Room (feed cache), DataStore Preferences, Paging 3, Coil 3, Firebase Messaging,
  AboutLibraries.

## Modules

| Module | Purpose |
| --- | --- |
| `:app` | UI layer: Compose screens, drawer, top bar, navigation, view models, themes. |
| `:app:migration` | Versioned on-device app data migrations (stale files, orphaned databases). `:app` only starts the run and gates its first frame on it; the step registry is `internal`. |
| `:common` | Shared constants (site base URL), DI keys, request codes. |
| `:firebase` | FCM messaging service and push token handling. |
| `:html-text`, `:html-text:api`, `:html-text:imagecontent` | Standalone Compose library (`org.leviathan941.compose.htmltext`) that renders HTML as `AnnotatedString`, with pluggable inline content (e.g. `<img>`). Keep it app-agnostic. |
| `:network` | Ktor client for the WordPress REST API and the RSS feed (`WpNetworkClient` / `WpKtorClient`). |
| `:network:cache` | Room database caching feed categories and channel items, read behind `FeedCacheProvider` and written behind `FeedCacheMutator`. |
| `:notification` | Notification channels, builders, helpers. |
| `:permission` | Compose permission rationale UI (Accompanist permissions). |
| `:preferences` | DataStore-backed UI/promo preferences with migrations. |
| `:rss-reader` | Pages the cached feed with Paging 3 and refills it via a `RemoteMediator`. |

Versions live in `gradle/libs.versions.toml`; SDK/JVM/app-version constants live in
`buildSrc/src/main/kotlin/org/leviathan941/retrodromcompanion/` (bump `AppVersion` for releases).
Read them from there rather than restating them here — version numbers in this file go stale.

## Conventions

- Every library module enables `ExplicitApiMode.Strict` — public declarations need explicit
  `public` and explicit return types. `:app` does not.
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
- Source files and module `build.gradle.kts` scripts carry the GPL v3 header
  (`RetroDrom Games Companion / Copyright (C) …`). Only the root `build.gradle.kts`,
  `settings.gradle.kts`, `buildSrc` and `app/build.gradle.kts` carry the Apache 2.0 header.
  Copy a neighbouring file's header for its shape, but set the year deliberately: a new file
  takes the current year, while a file that is moved, renamed or rewritten in place keeps the
  year it already had.
- Style is ktlint (`android_studio` code style) configured in `.editorconfig`: trailing commas
  allowed, multiline signatures from 2 parameters, import ordering rule disabled.
- `detekt.yaml` exists for standalone/IDE detekt runs; no detekt or ktlint Gradle plugin is
  wired into the build, so neither runs as part of `./gradlew build`.

## Build & verify

```sh
./gradlew assembleDebug        # build the app
./gradlew lint                 # Android lint, every module
./gradlew build                # compile everything (also runs lint)
```

The only tests are `:network`'s JVM unit tests, which cover the RSS response mapping and feed
URL building; `./gradlew test` runs them. Compilation plus Android lint is otherwise the gate.

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
sources as well. That is what keeps `UnusedResources` quiet: `:notification`, `:permission`
and `:firebase` each declare an empty placeholder resource in their own `res/values/ids.xml`
so their code and manifests compile standalone, and `:app` supplies the real value and wins
the merge. Without `checkDependencies` lint cannot see the library-side reference and reports
every one of those `:app` values as unused. Never "fix" such a report by deleting the
resource — that ships blank strings and missing icons at runtime.

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
