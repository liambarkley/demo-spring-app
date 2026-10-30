rootProject.name = "demo-spring-app"

// Remote build cache points at the Develocity Build Cache Node (build-cache platform SRE
// portfolio project) - disabled by default so cloning this repo without that specific
// infrastructure still builds fine locally, using only the local cache. CI passes
// -PbuildCache.url (and, for the one job that's allowed to populate the cache,
// -PbuildCache.username/-PbuildCache.password from a GitHub Actions secret) to turn this on -
// see .github/workflows/ci.yml.
buildCache {
    local {
        isEnabled = true
    }
    remote<HttpBuildCache> {
        val url = providers.gradleProperty("buildCache.url")
        isEnabled = url.isPresent
        if (url.isPresent) {
            setUrl(url.get())
        }
        isPush = providers.gradleProperty("buildCache.push").map { it.toBoolean() }.getOrElse(false)
        // Gradle refuses a plain-HTTP remote cache URL by default, specifically because
        // credentials would travel in cleartext - exactly the trade-off already documented in
        // platform/apps/build-cache-node's README (no domain available for an ACM
        // certificate). Opting in explicitly, not silently working around it.
        isAllowInsecureProtocol = true
        credentials {
            username = providers.gradleProperty("buildCache.username").orNull
            password = providers.gradleProperty("buildCache.password").orNull
        }
    }
}
