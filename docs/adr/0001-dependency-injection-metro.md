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

## Amended 2026-09-20 (A3 spike)

The A3 spike verified Metro **1.4.4** (not 1.4.3) on `:preferences`. Three corrections to the
above, none of which change the decision to use Metro.

**1. The toolchain question is settled — Metro works with AGP-supplied Kotlin compilation.**
AGP 9's built-in Kotlin applies KGP's `KotlinBaseApiPlugin` and runs every
`KotlinCompilerPluginSupportPlugin` exactly as KGP does. Metro is one, so it needs no Kotlin
Gradle plugin of its own. Verified end to end: the compiler plugin reaches the kotlinc command
line, FIR/IR codegen and graph validation run, and `javax.inject` qualifiers resolve. R8,
`explicitApi = Strict`, Android lint and the configuration cache are all unaffected.

**2. The fallback named in the Consequences never existed.** AGP 9 hard-fails when
`org.jetbrains.kotlin.android`, `kotlin-kapt` or `org.jetbrains.kotlin.multiplatform` is applied
alongside built-in Kotlin, unless `android.builtInKotlin=false` *and* `android.newDsl=false` are
set project-wide. "Apply the Kotlin plugin explicitly" was not a per-module escape hatch. Moot,
since Metro worked, but the text was wrong.

**3. The Dagger/Hilt interop will not be used.** A4 converts every Hilt annotation directly to its
native Metro equivalent in one commit, rather than flipping `:app`'s graph first and leaving the
library modules on Hilt behind interop. Four reasons:

- Metro's interop is **one-directional**. A Metro `@DependencyGraph` merges Hilt `@Module`s and
  `@EntryPoint`s, but Hilt's generated component cannot see Metro bindings, and Metro does not
  consume `@HiltAndroidApp`. A staged migration would have to run root-first, and that first
  commit is already irreducible — the graph, both `@AndroidEntryPoint`s, 6 view models and all 8
  `hiltViewModel()` call sites hang off the single root component. Staging would only have split
  off five modules of mechanical annotation swaps.
- Keeping Hilt on the classpath is what creates the problems. A `@DependencyGraph(Singleton::class)`
  cannot compile while `hilt-android` is present without `excludes` for the three public
  `@InstallIn(SingletonComponent::class) @EntryPoint` interfaces Hilt ships for its own Android
  machinery — one of which is not even nameable from Kotlin. Those excludes would be added and
  then deleted again.
- **Interop is the riskier option, not the safer one.** Metro ships native `@Assisted`,
  `@AssistedInject`, `@AssistedFactory`, `@IntoSet`, `@ElementsIntoSet`, `@Multibinds`, `@IntoMap`,
  `@ClassKey` and `@ContributesIntoMap` — every mechanism this project uses. Interop would mean
  relying on Metro reading *Dagger's* versions of those instead.
- The surface is small: 27 files carrying DI annotations across 9 modules, plus the build files.

**This spends one of the three reasons this ADR gave for choosing Metro.** The Rationale above
lists Dagger/Hilt interop as letting the migration "proceed one module at a time"; we are not
using it. The decision stands on the other two — compile-time graph validation and multiplatform
support — which Koin does not offer and which were always the load-bearing ones. Recorded here
rather than quietly dropped.

The practical consequence: A4 is one branch, one commit, one green gate, plus a manual smoke run.
Runtime semantics (scope mapping, the view model factory, eager-vs-lazy initialisation) are the
real risk and are not covered by the compiler; a staged route would not have surfaced them
earlier either. Convert in dependency order on the branch (`:common` → leaves → `:app`) as a
debugging tactic, even though only the final state has to compile.

Full spike findings, including the `excludes` snippet, are in `.local/reports/a3-metro-spike.md`.

## Alternatives considered

- **Koin** — multiplatform and mature, but runtime resolution loses compile-time graph checking.
- **kotlin-inject / kotlin-inject-anvil** — multiplatform and compile-time, but KSP-based and
  with a heavier migration from Hilt's module model.
