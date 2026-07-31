# Project Status Report — CareerPilot Backend

## Summary
- Type: Spring Boot 3 backend (Java 21)
- Purpose: AI-powered resume analysis, improvements, and learning roadmaps
- Current state: Buildable, runtime config present; unit tests ran but one failure due to Testcontainers/Docker environment missing

## Key Findings
- Build config: `pom.xml` uses Spring Boot 3.3.0, Java 21, MapStruct, LangChain4j, Flyway, PDFBox, Hypersistence. Compiler target set to Java 21.
- Runtime config: `src/main/resources/application.yml` configured for PostgreSQL, Redis, Flyway, and Gemini AI settings with sensible defaults and placeholders.
- Tests: `target/surefire-reports` shows:
  - `CareerpilotBackendApplicationTests` failed with an error: Testcontainers cannot find a valid Docker environment (Docker not accessible).
  - `ResumeServiceTest` passed (2 tests, no failures).
- Secrets: API keys and JWT secret appear in local config/example files (`.continue/config.yaml` / `.env.example` attachments). These should not be committed.

## Risks & Issues
- CI / local test failure: tests using Testcontainers require Docker; CI must provide Docker or configure Testcontainers to use a remote Docker daemon or a mock strategy.
- Hard-coded or example secrets in config files risk accidental leakage.
- LangChain4j + Gemini dependency requires valid API keys and may need networking/quotas for integration tests.
- `mapstruct-processor` is declared with `scope=provided` but annotationProcessorPaths also reference Lombok — ensure `lombok.version` property exists if compilation uses it (pom references `${lombok.version}` for the compiler plugin).

## Recommendations (short-term)
- Fix tests locally: install Docker Desktop or configure Testcontainers to use a remote Docker host. See https://www.testcontainers.org/ for options.
- Remove real API keys from repository and move credentials to environment variables or a secrets manager; add `.env` or `application-*.yml` to `.gitignore`.
- Add a CI job that runs `mvn -B -DskipTests=false clean verify` with Docker available, and a quick `mvn -DskipTests package` job for fast builds.
- Validate `pom.xml` for a defined `lombok.version` property (or remove `${lombok.version}` reference) to avoid build-time resolution issues.
- Add a README section about required environment variables (DB, REDIS, GEMINI API KEY, JWT_SECRET).

## Recommended Next Steps (actionable)
1. Ensure Docker is installed and running locally; re-run tests:

```bash
# from repo root
mvn clean test
```

2. Move secrets into environment variables and remove them from tracked files. Example env vars to set:
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- `GEMINI_API_KEY`, `JWT_SECRET`

3. Add a CI config (GitHub Actions) that: installs Java 21, starts Docker, runs `mvn -B -DskipTests=false verify`.

4. (Optional) Add a lightweight integration test profile that disables Testcontainers for quick unit test runs in environments without Docker.

## Status Conclusion
The project is in a healthy, near-production shape: dependencies and configuration are modern and appropriate for the stated goals. The primary blocker for full verification is Docker/Testcontainers availability for integration tests and removal of embedded secrets.

---
Report generated on 2026-07-21.

## Detailed Code Audit (answers to your checklist)

- **Which classes are placeholders?**
  - `com.careerpilot.ai.GeminiAiService` — defined as an L4J service interface with no concrete implementation in the repo. This is the main runtime placeholder: without an adapter that wires LangChain4j/Gemini client, AI features are non-functional.
  - No other explicit "placeholder" classes were found (no TODO/FIXME markers); however several repository query usages use in-memory filtering (e.g., `findAll().stream()` in services) instead of dedicated repository methods — these are implementation gaps rather than placeholders.

- **Which services are incomplete?**
  - `GeminiAiService` (no implementation) — blocks: resume parsing, JD parsing, semantic matching, improvements, and roadmap generation.
  - `ResumeParserService` and `JobDescriptionParserService` rely on the above interface (they are implemented but depend on the missing AI adapter).
  - `ImprovementService` and `RoadmapService` are implemented but use `findAll()` + in-memory filtering to locate records (inefficient and brittle). Repositories should expose targeted query methods (e.g., `findByAnalysisIdAndUserId`).

- **Which TODOs remain?**
  - Implement a concrete `GeminiAiService` adapter that calls LangChain4j / Gemini with retries, timeouts, and error handling.
  - Add `lombok.version` property to `pom.xml` or remove the `${lombok.version}` reference in the compiler plugin config.
  - Remove committed API keys / secrets from repo and add them to `.gitignore` / environment variables.
  - Replace `findAll().stream().filter(...)` usages in `ImprovementService`, `RoadmapService` with repository query methods.
  - Add comprehensive unit and integration tests for `AnalysisService`, `MatchingEngineService`, `ImprovementService`, `RoadmapService`, and controllers.

- **Which endpoints are only partially implemented?**
  - API endpoints exist for Analysis, Improvement, Roadmap, Resumes, Auth and Users and are functionally wired to services.
  - They are effectively "partially implemented" because the AI integration is missing (see `GeminiAiService`), so endpoints that depend on AI (analysis, improvement, roadmap, matching) cannot produce usable outputs until the adapter is implemented.
  - Some endpoints rely on inefficient data lookups (e.g., improvement/roadmap GET use `findAll()`), which should be replaced with precise repository methods.

- **Which entities need additional fields?**
  - `User`:
    - Add roles/authorities collection, emailVerified flag, lastLogin, and optional profile metadata (headline, location).
  - `Resume`:
    - Add fileSize, contentType, parsingStatus (enum), parsingErrors, source (uploaded/via-api) and checksum.
  - `JobDescription`:
    - Add sourceUrl, externalId (for scraped/imported JDs), and metadata (location, seniority).
  - `Analysis`:
    - Add aiModelName, aiConfidence (optional), status, and tags/labels for filtering.
  - `ImprovedResume` / `Roadmap`:
    - Optionally add exportedFileId or persistedDocument reference, status, and TTL/archival metadata.

- **Which DTOs are missing?**
  - Core response/request DTOs are present for current flows (`Responses`, `AnalysisRequest`, `AuthRequests`, `UpdateProfileRequest`). Missing or advisable additions:
    - `ResumeUploadResponse` (already covered by `ResumeResponse`) — optional: signed-upload URL DTO if using object storage.
    - `AiErrorResponse` or more granular `AnalysisProgress` DTO for long-running AI operations (job id, status, progress).
    - Explicit request/response DTOs for pagination and filtering (resume list, analysis history params).

- **Which tests are absent?**
  - Unit tests are missing for: `AnalysisService`, `MatchingEngineService`, `ImprovementService`, `RoadmapService`, `AuthService`, `UserController`, `AuthController`, and `AnalysisController`.
  - Integration tests covering the full AI flow are absent (and require a working `GeminiAiService` implementation or a mocked strategy).
  - Security tests (JWT flows, authorization checks) are not present.

- **What prevents production readiness?**
  - Missing AI adapter (`GeminiAiService` implementation) — core feature blocker.
  - Secrets committed in example/config files — security risk.
  - Tests requiring Docker/Testcontainers fail when Docker isn't available; CI must provide Docker or tests must support an alternative test profile.
  - Missing repository query methods and some inefficient data access patterns (in-memory filtering) — risks correctness and performance at scale.
  - No CI workflow or release pipeline present in the repo (add GitHub Actions / other CI to validate builds, tests, and containerization).
  - Production hardening gaps: monitoring/metrics, structured logging, rate limiting, input validation boundaries, and documentation of operational runbook.

If you want, I can (pick one):
- implement a `GeminiAiService` adapter that calls LangChain4j with a mockable client (safe default),
- add repository query methods and replace `findAll()` uses,
- remove embedded secrets and update `.gitignore`, or
- scaffold a CI (GitHub Actions) workflow that runs tests with Docker.

