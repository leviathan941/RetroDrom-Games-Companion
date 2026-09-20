# 0005 — HTTP engine selection: `ktor-client-engine-defaults` and a shared base client

**Status:** Accepted (2026-09-20)

## Context

Ktor's `HttpClient` is multiplatform; its engine is not. Android needs OkHttp, iOS needs Darwin,
and the app has two independent consumers of the transport: the WordPress/RSS client in
`:network` and Coil's image fetcher in `:app`. Both must share one engine, or the app opens two
connection pools.

Until this stage `:network` named the engine itself — `OkHttp.create()` in a Metro `@Provides` —
which is precisely the Android-only reference stage A5 exists to remove. Two shapes were on the
table:

1. **Per-platform engine binding.** `:network` keeps consuming an injected `HttpClientEngine`;
   each platform entry point provides its own (`OkHttp.create()` in `:app`, `Darwin.create()` in
   the future iOS app).
2. **`io.ktor:ktor-client-engine-defaults`.** A code-free aggregator whose Gradle Module Metadata
   maps each target to an engine: the `jvm` variant depends on `ktor-client-okhttp`, the Apple
   variants on `ktor-client-darwin`. It publishes no `androidJvm` variant, so an Android module
   resolves the `jvm` one — the same path `ktor-client-core` already takes here. Its engine is
   reachable only through the no-argument `HttpClient()`, which discovers the engine at runtime
   (`ServiceLoader` on JVM).

## Decision

Take option 2. `:network` depends on `ktor-client-engine-defaults` from what will become its
`commonMain`, and DI provides a **base `HttpClient`** instead of an `HttpClientEngine`
(`HttpClientModule` in `:network`). Consumers derive from that base client rather than building
their own:

- `WpHttpClientFactory` returns `baseClient.config { … }` — plugins and the site `defaultRequest`
  on top of the shared engine.
- Coil takes the base client as-is through the app graph.

No module names an engine, there is no `expect`/`actual` for the engine, and no engine dependency
per source set. Stage B4 is correspondingly reduced to source-set configuration, and C2 gets its
iOS engine without new wiring.

## Consequences

- **Engine-specific tuning is no longer reachable.** `HttpClient { engine { … } }` over a
  discovered engine exposes only the common `HttpClientEngineConfig` (proxy, `threadsCount`,
  `pipelining`) — not OkHttp interceptors or Darwin session settings. Nothing configures the
  engine today. If that changes, the escape hatch is option 1 for that platform, which this
  decision does not foreclose: the binding type stays `HttpClient`, so only the provider moves.
- **Runtime discovery on JVM/Android is an R8 exposure.** If R8 dropped
  `io.ktor.client.engine.okhttp.OkHttpEngineContainer` or its `META-INF/services` entry,
  `HttpClient()` would fail at the first request with *"Failed to find HTTP client engine
  implementation"*. Verified on the A5 release build: R8 keeps the container unrenamed and the
  service entry intact, and the minified app loads the feed and images. **No keep rule was
  needed.** If a future R8 or Ktor version regresses this, add the keep to
  `network/consumer-rules.pro` rather than to `:app`.
- The engine is decided by dependency resolution, so it is not visible in the source. The
  binding's KDoc and this record are where that is written down.
- `org.slf4j:slf4j-api` is *not* newly introduced: it already arrived with `ktor-client-okhttp`,
  which engine-defaults now pulls in transitively instead. The APK's resolved dependency set gains
  only the two `ktor-client-engine-defaults` modules.
