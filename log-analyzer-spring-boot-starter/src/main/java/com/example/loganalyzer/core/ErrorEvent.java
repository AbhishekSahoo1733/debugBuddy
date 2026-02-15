package com.example.loganalyzer.core;

import java.util.List;

public record ErrorEvent(
        String loggerName,
        String threadName,
        String message,
        List<String> recentLogLines,
        List<StackTraceElement> stackTrace) {
}
