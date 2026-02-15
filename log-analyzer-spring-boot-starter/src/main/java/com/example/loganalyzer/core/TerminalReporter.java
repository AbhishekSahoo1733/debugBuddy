package com.example.loganalyzer.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TerminalReporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void print(ErrorEvent event, String analysis) {
        String header = "\n================ LOG ANALYZER REPORT ================\n";
        String footer = "\n====================================================\n";

        StringBuilder sb = new StringBuilder();
        sb.append(header);
        sb.append("Time: ").append(LocalDateTime.now().format(FORMATTER)).append("\n");
        sb.append("Logger: ").append(event.loggerName()).append("\n");
        sb.append("Thread: ").append(event.threadName()).append("\n");
        sb.append("Message: ").append(event.message()).append("\n\n");
        sb.append("AI Analysis:\n");
        sb.append(analysis).append("\n");
        sb.append(footer);

        System.err.println(sb);
    }
}
