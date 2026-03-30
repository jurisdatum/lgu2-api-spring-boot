# ==========================================
# STAGE 1: Builder
# ==========================================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 1. Copy Maven wrapper + pom first (cache-friendly)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# 2. Download dependencies (warm Maven cache)
RUN ./mvnw -B -q -DskipTests dependency:resolve-plugins dependency:go-offline

# 3. Copy application source
COPY src src

# 4. Build
RUN ./mvnw -B -q -DskipTests package

# ==========================================
# STAGE 2: Final Runtime
# ==========================================
FROM eclipse-temurin:21-jre
WORKDIR /app

# 5. Non-root user (optional but recommended)
RUN useradd -m -u 10001 appuser

# 6. Copy the built jar
COPY --from=build /workspace/target/lgu2-api.jar /app/app.jar

# 7. Switch to non-root user
USER appuser

# 8. Expose port
EXPOSE 8080

# 9. A simple, overridable entrypoint
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
