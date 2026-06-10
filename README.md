# lgu2-api

Spring Boot API for legislation.gov.uk v2. It exposes REST endpoints used by the
front-end Django app and serves the OpenAPI spec at `/spec`.

## Requirements

- Java 21
- Access to MarkLogic/Virtuoso for real data, or local/test configuration for development

## Getting Started

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Local secrets may be placed in `src/main/resources/application-secrets.properties`
(ignored by Git) or supplied via environment variables. The `dev` profile enables
debug logging and disables AWS Parameter Store; tests use placeholder service settings
from `application-test.properties`.

## Formatting

Java sources are formatted with google-java-format via Spotless.

Format locally:

```bash
./mvnw spotless:apply
```

Check formatting:

```bash
./mvnw spotless:check
```

`./mvnw verify` runs the formatting check, and PR CI runs `clean verify`.

## Testing

```bash
./mvnw test
```

For the full build and verification path:

```bash
./mvnw verify
```
