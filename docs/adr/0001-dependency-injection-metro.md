# 0001 — Dependency injection: Hilt → Metro

**Status:** Accepted (2026-09-13)

## Context

Hilt is used in 7 of 13 modules (`:app`, `:app:migration`, `:common`, `:firebase`, `:network`,
`:network:cache`, `:rss-reader`) and is Android-only. It is the single largest blocker to moving
any module to common code, and it touches every module, so it cannot be deferred behind the
module-by-module port.

## Decision

Replace Hilt with [Metro](https://github.com/ZacSweers/metro) (`dev.zacsweers.metro`, 1.4.3 at
the time of writing), done entirely on Android before any module gains multiplatform source sets.

## Rationale

- Multiplatform and compile-time — it keeps the compile-time graph validation Hilt gives us,
  unlike a runtime container such as Koin.
- It is a Kotlin compiler plugin, so it removes KSP/kapt from the DI path rather than adding to it.
- It ships Dagger/Hilt interop, so a `@DependencyGraph` can consume existing Hilt modules and
  entry points. The migration can therefore proceed one module at a time with a green build after
  each, instead of as one large commit.

## Consequences

- `hiltViewModel()` has no direct equivalent; view model construction has to be wired explicitly.
  This is the main code-level cost and it lands in `:app`.
- Metro hooks into Kotlin compilation, and this project has no Kotlin Gradle plugin applied —
  AGP 9.4 supplies Kotlin itself. Whether Metro's Gradle plugin works in that setup is unverified.
  **Stage A3 is a spike on one small module specifically to answer this.** If it does not work,
  the fallback is to apply the Kotlin plugin explicitly; if that also fails, reopen this ADR with
  Koin and kotlin-inject as the alternatives.
- Metro is younger than Hilt and less widely deployed. Accepted deliberately: the project is
  ~9k lines, and the DI surface is small enough that a reversal would be days, not weeks.

## Alternatives considered

- **Koin** — multiplatform and mature, but runtime resolution loses compile-time graph checking.
- **kotlin-inject / kotlin-inject-anvil** — multiplatform and compile-time, but KSP-based and
  with a heavier migration from Hilt's module model.
