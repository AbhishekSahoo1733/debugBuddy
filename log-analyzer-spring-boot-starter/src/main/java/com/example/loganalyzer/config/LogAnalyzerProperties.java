package com.example.loganalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "log.analyzer")
public class LogAnalyzerProperties {

    private boolean enabled = false;
    private String model = "qwen2.5-coder:7b";
    private String ollamaUrl = "http://localhost:11434";
    private int maxLogLines = 200;
    private int maxSourceLines = 120;
    private int timeoutMs = 30000;
    private String sourceRoot = "src/main/java";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOllamaUrl() {
        return ollamaUrl;
    }

    public void setOllamaUrl(String ollamaUrl) {
        this.ollamaUrl = ollamaUrl;
    }

    public int getMaxLogLines() {
        return maxLogLines;
    }

    public void setMaxLogLines(int maxLogLines) {
        this.maxLogLines = maxLogLines;
    }

    public int getMaxSourceLines() {
        return maxSourceLines;
    }

    public void setMaxSourceLines(int maxSourceLines) {
        this.maxSourceLines = maxSourceLines;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public String getSourceRoot() {
        return sourceRoot;
    }

    public void setSourceRoot(String sourceRoot) {
        this.sourceRoot = sourceRoot;
    }
}
