# 0006 — Build logic: convention plugins in an included `build-logic` build

**Status:** Accepted (2026-11-02)

## Context

Phase B turns 13 library modules into Kotlin Multiplatform modules. Each one needs the same
block: `org.jetbrains.kotlin.multiplatform`, `com.android.kotlin.multiplatform.library`,
`com.android.lint`, `explicitApi()`, SDK levels, JVM target, lint options, host tests and the
iOS targets. The migration plan (A9) asked for convention plugins in `buildSrc`, which already
holds the project's SDK, JVM and version constants.

Spike A9 built and gated both places for the convention plugin:

1. **`buildSrc`.** The convention plugin is compiled against AGP and KGP, so both must be
   `implementation` dependencies of `buildSrc`. `compileOnly` cannot work, because `buildSrc`'s
   classloader is the parent of every build script's, so AGP types loaded below it are invisible.
   Every build script then sees those jars with no version, so any *versioned* request for a
   plugin they contain fails: "already on the classpath with an unknown version". That covers
   every `com.android.*` ID and KGP's `kotlin.multiplatform` and `plugin.parcelize`. Each such
   `alias(libs.plugins.…)` in the root script and all 14 module scripts had to become an
   unversioned `id(…)`, and the catalog's plugin versions for them stop being used.
2. **An included `build-logic` build** (`pluginManagement { includeBuild("build-logic") }`).
   AGP and KGP are `compileOnly`; at runtime the plugins come from the root script's
   `apply false` declarations, as today. No other script changed.

Gradle's best-practices guide recommends option 2 ("Favor `build-logic` Composite Builds for
Build Logic"). It cites the same classloader differences, and notes that any `buildSrc` change
invalidates the whole build.

## Decision

Put convention plugins in an included `build-logic` build, starting with
`retrodrom.kmp-library`. Move `buildSrc`'s constants (`AndroidSdk`, `JvmVersions`, `Application`,
`AppVersion`) into it and delete `buildSrc`, so the constants have one home.

## Consequences

- A class in `build-logic` is visible only to a build script that applies a plugin from
  `build-logic` (verified: `:preferences` could not resolve one). Moving the constants therefore
  needs a convention for every module: an Android-library one for modules not yet ported and an
  application one for `:app`. This also replaces the `android { }` block that 13 scripts now
  repeat. That move is its own stage, before B1, so B1 stays "first KMP module".
- Plugin versions stay in the catalog and in the root `apply false` list. Module scripts keep
  `alias(libs.plugins.…)`.
- `build-logic` is not linted by the root `allprojects` ktlint, the same as `buildSrc` today.
- The CLAUDE.md note that ktlint "cannot see AGP and KGP" from a `buildSrc` convention describes
  option 1's classloader. It should be re-checked rather than assumed for `build-logic`.
