# Log Analyzer MVP (Spring Boot + Ollama)

This project provides a local-first MVP that analyzes Java/Spring runtime errors from logs and prints root-cause guidance in the terminal using Ollama.

## Modules

- `log-analyzer-spring-boot-starter`: reusable dependency for any Spring Boot app.
- `example-app`: sample app that throws an exception at `/boom`.

## Prerequisites

- Java 17+
- Maven 3.9+
- Ollama installed and running locally
- Model available locally (already done):
  - `ollama pull qwen2.5-coder:7b`

## Quick start

1. Start Ollama server (if not already running):
   - `ollama serve`
2. Build project:
   - `mvn clean install`
3. Run example app:
   - `mvn -pl example-app spring-boot:run`
4. Trigger an error:
   - `curl http://localhost:8080/boom`
5. Check terminal output for `LOG ANALYZER REPORT`.

## How to use the starter in another project

Add dependency:

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>log-analyzer-spring-boot-starter</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Enable in `application.yml`:

```yaml
log:
  analyzer:
    enabled: true
    model: qwen2.5-coder:7b
    ollama-url: http://localhost:11434
```

## Configuration

- `log.analyzer.enabled`: enable/disable analyzer.
- `log.analyzer.model`: local Ollama model name.
- `log.analyzer.ollama-url`: Ollama base URL.
- `log.analyzer.max-log-lines`: rolling log buffer size.
- `log.analyzer.max-source-lines`: max source lines for context extraction.
- `log.analyzer.timeout-ms`: Ollama request timeout.
- `log.analyzer.source-root`: source folder used for code context.

## MVP limitations

- No deduplication/rate limiting yet.
- No secrets redaction yet.
- Source resolution expects source tree available locally.
