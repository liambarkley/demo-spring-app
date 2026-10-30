# demo-spring-app

A small Spring Boot app, built with Gradle, that exists to exercise the Develocity Build Cache
Node - the SRE portfolio project's actual subject. Phase 3.

Not a deployment repo - `platform/apps/demo-spring-app` (in the `platform` repo) deploys
whatever image this repo's CI publishes to `ghcr.io/liambarkley/demo-spring-app`.

## What it is

A `Widget` entity, a Spring Data JPA repository, and a `GET`/`POST /widgets` REST controller -
deliberately simple. The point isn't the app; it's giving Gradle something real to compile,
test, and cache.

- Java 17, Spring Boot 4.1.2, Spring Framework 7.
- `spring-boot-starter-webmvc` (not the deprecated `-web` name - Spring Boot 4 renamed it).
- `@DataJpaTest` (`spring-boot-starter-data-jpa-test`, also a Boot 4 package/module move) runs
  the one test against an embedded H2 database - no real Postgres needed just to build and
  test. The real database (`infra/modules/rds`) is only needed at actual runtime.

## The remote build cache

`settings.gradle.kts` configures an `HttpBuildCache`, disabled unless `-PbuildCache.url` is
passed - cloning this repo without that specific infrastructure still builds fine locally,
local cache only. `.github/workflows/ci.yml` is where it actually gets exercised:

1. **`first-build`**: a fresh runner, full build, pushes to the cache - needs the `ci` user's
   credentials (`BUILD_CACHE_PASSWORD` secret), since writing requires auth
   (`anonymousLevel: read` on the Build Cache Node, not `readwrite`).
2. **`second-build`**: a *different* fresh runner - deliberately, so Gradle's own local cache
   (unaffected by `clean`) can't explain a cache hit the way it could if this ran as a second
   step in the same job. No credentials at all: anonymous reads already work, so this also
   proves a PR from a fork would get cache hits with zero secrets. Fails the job if no task
   shows `FROM-CACHE` in the output.
3. **`image`**: builds the deployable image from whatever the previous jobs already compiled
   (no third Gradle invocation inside Docker) and pushes it to GHCR - `main` only.

`BUILD_CACHE_URL` is a GitHub Actions repository **variable**, not a secret - it's just the
Build Cache Node's ALB DNS name, which changes every time `infra/envs/dev` is destroyed and
recreated. **Needs updating by hand after that happens** (`gh variable set BUILD_CACHE_URL
--repo liambarkley/demo-spring-app --body "http://<new-alb-dns>/cache/"`) - same category of
operational note as `infra/ci-oidc`'s `oidc_sub_claim_prefix`.

## Database credentials

Never in this repo. `platform/apps/demo-spring-app`'s init container fetches the RDS master
password (AWS Secrets Manager) and address (SSM Parameter Store) at pod startup via its own
Pod Identity role, writing the password to a file this image's `entrypoint.sh` reads into
`DB_PASSWORD` before starting the JVM - see that app's README for the full mechanism.

## Git hooks

Same as `infra`/`platform` - this repo is public too.

```
git config core.hooksPath scripts/git-hooks
```

Requires `gitleaks` (`brew install gitleaks`).
