package com.example.loganalyzer.core;

import com.example.loganalyzer.config.LogAnalyzerProperties;

import java.util.List;

public class PromptBuilder {

    private final LogAnalyzerProperties properties;

    public PromptBuilder(LogAnalyzerProperties properties) {
        this.properties = properties;
    }

    public String build(ErrorEvent event, String codeContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a Java/Spring production debugging assistant. Analyze the failure from logs and code context. ")
                .append("Return short sections: Root cause, Why likely, How to verify, Fix suggestion.\n\n");

        sb.append("Application log error:\n")
                .append("Logger: ").append(event.loggerName()).append("\n")
                .append("Thread: ").append(event.threadName()).append("\n")
                .append("Message: ").append(event.message()).append("\n\n");

        sb.append("Recent logs:\n")
                .append(join(event.recentLogLines(), properties.getMaxLogLines()))
                .append("\n\n");

        sb.append("Stack trace:\n")
                .append(join(event.stackTrace().stream().map(StackTraceElement::toString).toList(), 200))
                .append("\n\n");

        sb.append("Code snippets:\n")
                .append(codeContext)
                .append("\n\n");

        sb.append("Constraints: keep answer practical and avoid generic advice.");
        return sb.toString();
    }

    private String join(List<String> lines, int maxLines) {
        int from = Math.max(0, lines.size() - maxLines);
        return String.join("\n", lines.subList(from, lines.size()));
    }
}
