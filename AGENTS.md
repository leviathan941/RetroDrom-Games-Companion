# RetroDrom Games Companion — agent guide

Native Android client for the [RetroDrom Games](https://retrodrom.games/) site (a WordPress
blog): browses the site's RSS categories/posts, opens articles, and receives push
notifications. Content is mostly Russian (`values-ru`); UI is Jetpack Compose only.

- Toolchain: Kotlin with KSP, targeting JVM 17 (no Gradle toolchain is pinned, so it builds
  on whichever JDK runs Gradle). The Android Gradle Plugin supplies Kotlin compilation itself
  — no module applies `org.jetbrains.kotlin.android`.
- `minSdk` is 26 — check it before reaching for a newer platform API. App id
  `org.segowski.retrodromgames`, code namespace `org.leviathan941.retrodromcompanion`.
- Key libs: Compose + Material 3, Navigation Compose, Hilt, Ktor (WordPress REST API),
  Room (feed-category cache), DataStore Preferences, Paging 3, Coil 3, Firebase Messaging,
  KtRssReader, AboutLibraries.

## Modules

| Module | Purpose |
| --- | --- |
| `:app` | UI layer: Compose screens, drawer, top bar, navigation, view models, themes. |
| `:common` | Shared constants (site base URL), DI keys, request codes. |
| `:firebase` | FCM messaging service and push token handling. |
| `:html-text`, `:html-text:api`, `:html-text:imagecontent` | Standalone Compose library (`org.leviathan941.compose.htmltext`) that renders HTML as `AnnotatedString`, with pluggable inline content (e.g. `<img>`). Keep it app-agnostic. |
| `:network` | Ktor client for the WordPress REST API (`WpNetworkClient` / `WpKtorClient`). |
| `:network:cache` | Room database caching feed categories behind `FeedCacheProvider`. |
| `:notification` | Notification channels, builders, helpers. |
| `:permission` | Compose permission rationale UI (Accompanist permissions). |
| `:preferences` | DataStore-backed UI/promo preferences with migrations. |
| `:rss-reader` | RSS fetching/parsing into paged feed items. |

Versions live in `gradle/libs.versions.toml`; SDK/JVM/app-version constants live in
`buildSrc/src/main/kotlin/org/leviathan941/retrodromcompanion/` (bump `AppVersion` for releases).
Read them from there rather than restating them here — version numbers in this file go stale.

## Conventions

- Every library module enables `ExplicitApiMode.Strict` — public declarations need explicit
  `public` and explicit return types. `:app` does not.
- Package layout per module: public API at the top level (or `api/`), implementation under
  `internal/` or `impl/`; Hilt modules under `di/`.
- Source files carry the GPL v3 header (`RetroDrom Games Companion / Copyright (C) …`); Gradle
  build scripts and `buildSrc` carry the Apache 2.0 header. Copy the header of a neighbouring
  file when adding one.
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

There are currently **no tests** — the `src/test` and `src/androidTest` directories exist but
are empty, so `./gradlew test` is a no-op. Compilation plus Android lint is the practical gate.

Every module sets `lint { warningsAsErrors = true }`, so a lint *warning* in any module
fails the build — the exit code is the gate, and per-module reports under
`<module>/build/reports/` only matter when diagnosing a failure. **Lint is clean: there is
no baseline file.** Fix a new finding, or suppress it narrowly at the declaration with
`@SuppressLint("IssueId")` plus a comment saying why; adding a baseline to hide one is a
last resort.

`:app` also sets `checkDependencies = true`, so its lint analyses the library modules'
sources as well. That is what keeps `UnusedResources` quiet: `:notification`, `:permission`
and `:firebase` each declare an empty placeholder resource in their own `res/values/ids.xml`
so their code and manifests compile standalone, and `:app` supplies the real value and wins
the merge. Without `checkDependencies` lint cannot see the library-side reference and reports
every one of those `:app` values as unused. Never "fix" such a report by deleting the
resource — that ships blank strings and missing icons at runtime.

`local.properties` (SDK path) and `app/google-services.json` are git-ignored but present
locally; a build without the latter fails in the Google Services plugin.
