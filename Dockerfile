# Takes an already-built jar (build/libs/*.jar) rather than running Gradle inside Docker -
# the CI workflow's whole point is demonstrating remote build caching with two plain
# `./gradlew build` runs on the runner itself, where the cache hit/miss output is directly
# visible in the Actions log. Re-running Gradle a third time inside a Docker build stage would
# just be a confusing, slower duplicate of that, not a clearer story.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]
