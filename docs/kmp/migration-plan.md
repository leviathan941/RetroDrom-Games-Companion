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
| A1 | Logging → Kermit | `android.util.Log` gone from all 15 call sites; a `Logger` facade available from `:common`. Cheapest stage — establishes the "replace a platform API with a multiplatform one" pattern. | — |
| A2 | Coil network layer | `coil-network-okhttp` → `coil-network-ktor3`, sharing the existing Ktor client. | — |
| A3 | Metro spike | Verify Metro's compiler plugin works with AGP-supplied Kotlin compilation on one small module (`:preferences` or `:common`). The single biggest unknown in the plan — see the risk note below. | — |
| A4 | Hilt → Metro | All 7 Hilt modules migrated, `hiltViewModel()` replaced. Metro's Hilt interop allows doing this module by module rather than in one commit. | A3, ADR-0001 |
| A5 | Ktor engine behind DI | `:network` no longer references OkHttp directly; the engine is injected. | A4 |
| A6 | Room 2.x → Room 3 | `androidx.room` → `androidx.room3` 3.0.3 across `:network:cache`, driver-based builder on `BundledSQLiteDriver`, remaining blocking DAO methods made suspend. Verify an upgrade over an install with real cached data. | A4, ADR-0004 |
| A7 | Platform APIs isolated | Custom Tabs, permissions, notifications, FCM and anything taking a `Context` sit behind interfaces declared in common-ready modules. | A4 |
| A8 | Navigation 2 → Navigation 3 | Back stack owned by the app as a `SnapshotStateList`; drawer and top bar reworked onto it. Type-safe `@Serializable` routes survive as back stack keys. Uses Google's `androidx.navigation3` 1.1.7 — the JetBrains coordinate swap happens in A10. Largest Phase A stage after A4. | A4, ADR-0002 |

### A9–A11 — toolchain and Compose

These cannot move earlier: the Compose Multiplatform plugin is precisely what requires the new
toolchain, and the resource migration depends on it.

| # | Stage | Outcome | Blocked by |
| --- | --- | --- | --- |
| A9 | Toolchain spike | `org.jetbrains.kotlin.multiplatform` + `com.android.kotlin.multiplatform.library` applied alongside AGP 9.4; convention plugins in `buildSrc` so 13 modules do not each hand-roll a KMP block. Success = one module compiles for `android` and `iosSimulatorArm64`. | A1–A8 |
| A10 | Compose BOM → Compose Multiplatform | Compose artifacts come from the CMP Gradle plugin's `compose.*` accessors. Includes swapping the Navigation 3 coordinates to the JetBrains ones — a version catalog change, no imports touched. Still Android-only at runtime. | A9 |
| A11 | Resources → `compose.resources` | `R.string` / `R.drawable` replaced at ~31 call sites; `values-ru` becomes `composeResources/values-ru`. | A10 |

**Risk carried by this ordering.** A3 now runs on the current toolchain rather than after the KMP
spike. If Metro turns out to need a Kotlin Gradle plugin applied explicitly, a piece of A9 has to
be pulled forward — apply `org.jetbrains.kotlin.android`, or go straight to the multiplatform
plugin for that module. That is a known and acceptable outcome of A3, not a reason to reorder
back; A3 exists to find it out cheaply.

**Exit criterion for Phase A:** no Android-only dependency remains except the ones listed as
deliberate in [`library-audit.md`](library-audit.md).

## Phase B — multiplatformise modules

Bottom-up along the real dependency graph.

| # | Module | Main work | Risk |
| --- | --- | --- | --- |
| B1 | `:common` | First real KMP module; validates the A8 convention plugins. | low |
| B2 | `:html-text:api`, `:html-text`, `:html-text:imagecontent` | Pure Compose + ksoup + Coil, no DI. The natural first CMP module. | low |
| B3 | `:preferences` | DataStore path via `expect`/`actual`. | low |
| B4 | `:network` | Darwin engine; existing JVM tests move to `commonTest`. | medium |
| B5 | `:network:cache` | `expect`/`actual` database builder and KSP running for the iOS targets. The driver and the Room 3 API surface are already done in A6, so this stage is build configuration rather than code. | medium |
| B6 | `:rss-reader` | Paging is already multiplatform; mostly follows B4/B5. | low |
| B7 | `:permission` | Accompanist has no iOS story — implement the A7 interface per platform. | medium |
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
| `:network` | no | no | |
| `:network:cache` | no | no | |
| `:notification` | no | no | Common shape only |
| `:permission` | no | no | |
| `:preferences` | no | no | |
| `:rss-reader` | no | no | |
| `:shared` | — | — | Does not exist yet (B9) |

## Open decisions

None. All four current ADRs are accepted; raise a new record if a stage turns up a decision that
constrains later work.
