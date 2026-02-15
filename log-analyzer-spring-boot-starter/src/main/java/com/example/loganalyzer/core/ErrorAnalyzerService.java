package com.example.loganalyzer.core;

import com.example.loganalyzer.context.CodeContextResolver;
import com.example.loganalyzer.ollama.OllamaClient;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ErrorAnalyzerService {

    private final CodeContextResolver codeContextResolver;
    private final PromptBuilder promptBuilder;
    private final OllamaClient ollamaClient;
    private final TerminalReporter reporter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "log-analyzer-worker");
        t.setDaemon(true);
        return t;
    });

    public ErrorAnalyzerService(
            CodeContextResolver codeContextResolver,
            PromptBuilder promptBuilder,
            OllamaClient ollamaClient,
            TerminalReporter reporter) {
        this.codeContextResolver = codeContextResolver;
        this.promptBuilder = promptBuilder;
        this.ollamaClient = ollamaClient;
        this.reporter = reporter;
    }

    public void submit(ErrorEvent event) {
        executor.submit(() -> {
            String codeContext = codeContextResolver.resolve(event.stackTrace());
            String prompt = promptBuilder.build(event, codeContext);
            String analysis = ollamaClient.analyze(prompt);
            reporter.print(event, analysis);
        });
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}
