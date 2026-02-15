package com.example.loganalyzer.context;

import com.example.loganalyzer.config.LogAnalyzerProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CodeContextResolver {

    private final LogAnalyzerProperties properties;

    public CodeContextResolver(LogAnalyzerProperties properties) {
        this.properties = properties;
    }

    public String resolve(List<StackTraceElement> stackTrace) {
        if (stackTrace == null || stackTrace.isEmpty()) {
            return "No stack trace available.";
        }

        Path root = Path.of(System.getProperty("user.dir"));
        Path sourceRoot = root.resolve(properties.getSourceRoot());
        int window = Math.max(10, properties.getMaxSourceLines() / 4);

        Set<String> uniqueFrames = new LinkedHashSet<>();
        List<String> snippets = new ArrayList<>();

        for (StackTraceElement frame : stackTrace) {
            if (snippets.size() >= 4) {
                break;
            }
            if (frame.getLineNumber() <= 0) {
                continue;
            }

            String className = frame.getClassName();
            if (className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("sun.")) {
                continue;
            }
            if (!uniqueFrames.add(className)) {
                continue;
            }

            Path file = sourceRoot.resolve(className.replace('.', '/') + ".java");
            if (!Files.exists(file)) {
                continue;
            }

            try {
                List<String> lines = Files.readAllLines(file);
                int line = frame.getLineNumber();
                int start = Math.max(1, line - window / 2);
                int end = Math.min(lines.size(), line + window / 2);

                StringBuilder sb = new StringBuilder();
                sb.append("File: ").append(root.relativize(file)).append("\n");
                sb.append("Frame: ").append(frame).append("\n");
                for (int i = start; i <= end; i++) {
                    sb.append(i).append(": ").append(lines.get(i - 1)).append("\n");
                }
                snippets.add(sb.toString());
            } catch (IOException ignored) {
                // Ignore unreadable files for MVP.
            }
        }

        if (snippets.isEmpty()) {
            return "Could not resolve source snippets from stack trace in " + sourceRoot;
        }

        return String.join("\n---\n", snippets);
    }
}
