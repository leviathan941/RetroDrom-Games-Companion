# 0002 — Navigation library: Navigation 2 → Navigation 3

**Status:** Accepted (2026-09-13)

## Context

The app uses `androidx.navigation:navigation-compose` 2.10.1 with type-safe routes
(`@Serializable` route classes and `toRoute()`) across roughly six destinations, plus a drawer and
a top bar that both read the current back stack entry.

Google's `androidx.navigation:navigation-compose` 2.10.1 publishes Gradle Module Metadata for
`android`, `jvmStubs` and `linux_x64` only — **there are no iOS variants**, so it cannot be used
from common code. The coordinate has to change regardless of which target library is chosen.

Two candidates were available:

1. **JetBrains' Navigation 2 fork**, `org.jetbrains.androidx.navigation:navigation-compose`
   (2.9.2 stable). Effectively a coordinate swap — same `NavHost` / `composable<Route>` API.
2. **Navigation 3**, `org.jetbrains.androidx.navigation3:navigation3-ui` (1.1.1), requiring
   Compose Multiplatform ≥ 1.10. A different library: the library-owned back stack is replaced
   by a `SnapshotStateList` of keys owned by the app, which the UI observes directly.

## Decision

Migrate to **Navigation 3**, in two steps, both while the project is still Android-only.

**A8 — the rewrite, on Google's artifacts.** `androidx.navigation3:navigation3-ui` 1.1.7 and
`androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0. These need no Compose Multiplatform,
so the navigation rewrite lands before the toolchain change.

**A10 — the coordinate swap**, as part of the move to Compose Multiplatform:
`org.jetbrains.androidx.navigation3:navigation3-ui` 1.1.1 and
`org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0. `navigation3-common`
arrives transitively; only `navigation3-ui` has a separate multiplatform implementation.

The two-step route costs one extra line change in the version catalog and nothing else: the
JetBrains artifacts ship the same `androidx.navigation3.*` package names as Google's — verified by
inspecting `navigation3-ui-desktop-1.1.1.jar`, which contains `androidx/navigation3/`,
`androidx/navigation3/scene/` and `androidx/navigation3/ui/`. No import in the codebase changes at
A10.

## Rationale

Navigation 3 is worth adopting on its own merits, independently of the iOS goal — app-owned back
stack state, and no graph to keep in sync with the UI. Since navigation has to be touched anyway,
doing it once is better than doing the Nav2 coordinate swap first and rewriting on top of it later.

Splitting it across A8 and A10 keeps the largest UI rewrite of the project away from the toolchain
change, so a navigation regression is attributable to navigation. It also delivers the Navigation 3
benefit to the Android app well before the port reaches iOS.

## Consequences

- A8 is a real stage with its own plan: the `NavHost` graph becomes an app-owned back stack, and
  the drawer and top bar have to be reworked with it. It is the largest single stage of Phase A
  after the DI migration.
- **A8 comes after the Hilt → Metro migration (A4).** There is no `androidx.hilt:hilt-navigation3`
  artifact — Hilt has no Navigation 3 integration — so doing A8 first would mean hand-wiring view
  model retrieval for Navigation 3 under Hilt and then again under Metro.
- A10 keeps the two libraries' versions close but not identical: Google is at 1.1.7 while the
  JetBrains mirror is at 1.1.1. Check the gap before the swap; pin A8 to 1.1.1 if it has grown
  into anything behavioural.
- Type-safe routes survive: the `@Serializable` route classes stay, they just become back stack
  keys instead of graph destinations.
- The project depends on a JetBrains-published artifact whose cadence trails androidx.

## Amended 2026-10-25 (A8)

A8 uses **1.2.0** rather than 1.1.7, and A10 swaps to the JetBrains **1.2.0**: Google's
`navigation3-runtime` / `navigation3-ui` and JetBrains' `navigation3-ui` are all stable at 1.2.0,
so the version gap the Consequences warned about has closed. `lifecycle-viewmodel-navigation3`
stays at 2.11.0 on both coordinates. 1.2.0 is also what brings the multiplatform
`androidx.navigation3.runtime.deeplink` API, which replaced Nav2's `navDeepLink`.

The decision is unchanged. One detail of the shape it describes: the back stack is a single
`NavBackStack<NavKey>` saved through an explicit `SavedStateConfiguration`, not a bare
`SnapshotStateList`, so it survives configuration changes and process death on every target.

## Amended 2026-11-07 (A10)

A10 swaps **only `navigation3-ui`** to `org.jetbrains.androidx.navigation3:navigation3-ui` 1.2.0.
`androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0 stays on Google's coordinate, and so
does `navigation3-runtime`. Both publish `ios_arm64` and `ios_simulator_arm64`.
`org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0 is an empty facade onto
Google's artifact on every platform: its iOS klib is ~8 KB and depends on
`androidx.lifecycle:lifecycle-viewmodel-navigation3` 2.11.0. So that swap would add a hop and
nothing else. Google's `navigation3-ui` still has no iOS variant (1.2.1, 1.3.0-alpha04), so the
JetBrains one remains necessary. On Android it is a facade onto Google's 1.2.0, so the shipped
code is unchanged.

The decision is unchanged.
