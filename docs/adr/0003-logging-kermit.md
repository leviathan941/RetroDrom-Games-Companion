# 0003 — Logging: `android.util.Log` → Kermit

**Status:** Accepted (2026-09-13)

## Context

`android.util.Log` is called from 15 places across several modules. It is a platform API, so
every one of those call sites blocks the module from moving to common code.

## Decision

Use [Kermit](https://github.com/touchlab/Kermit) (`co.touchlab:kermit`, 2.2.0 at the time of
writing), exposed through a thin logging facade in `:common` so that call sites do not depend on
the library directly.

## Rationale

- Multiplatform, writes to Logcat on Android and `NSLog`/`os_log` on iOS with no call-site changes.
- Small and stable; no code generation.
- The facade in `:common` keeps the swap cheap if Kermit ever becomes a problem.

## Consequences

- This stage (A1) has no dependency on anything else in Phase A and is scheduled first, as a
  cheap way to establish the "replace a platform API with a multiplatform one" pattern.
- Tag conventions currently vary per call site; the facade should settle on one.

## Alternatives considered

- **`io.github.oshai:kotlin-logging`** (8.0.4) — also multiplatform, but oriented towards an
  SLF4J-style backend setup that buys nothing here.
- **An in-house `expect`/`actual` logger** — no dependency, but then we own the iOS side,
  log levels and the tagging for no real gain.
