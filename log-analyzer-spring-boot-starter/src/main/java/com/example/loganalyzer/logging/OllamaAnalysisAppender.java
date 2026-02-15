package com.example.loganalyzer.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import com.example.loganalyzer.config.LogAnalyzerProperties;
import com.example.loganalyzer.core.ErrorAnalyzerService;
import com.example.loganalyzer.core.ErrorEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class OllamaAnalysisAppender extends AppenderBase<ILoggingEvent> {

    private final ErrorAnalyzerService analyzerService;
    private final LogAnalyzerProperties properties;
    private final Deque<String> recentLogs;

    public OllamaAnalysisAppender(ErrorAnalyzerService analyzerService, LogAnalyzerProperties properties) {
        this.analyzerService = analyzerService;
        this.properties = properties;
        this.recentLogs = new ArrayDeque<>(Math.max(20, properties.getMaxLogLines()));
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        synchronized (recentLogs) {
            if (recentLogs.size() >= properties.getMaxLogLines()) {
                recentLogs.removeFirst();
            }
            recentLogs.addLast("[" + eventObject.getLevel() + "] " + eventObject.getFormattedMessage());
        }

        if (!Level.ERROR.equals(eventObject.getLevel())) {
            return;
        }

        IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
        if (throwableProxy == null) {
            return;
        }

        List<String> logSnapshot;
        synchronized (recentLogs) {
            logSnapshot = new ArrayList<>(recentLogs);
        }

        List<StackTraceElement> stackTrace = extractStackTrace(throwableProxy);
        ErrorEvent errorEvent = new ErrorEvent(
                eventObject.getLoggerName(),
                eventObject.getThreadName(),
                eventObject.getFormattedMessage(),
                logSnapshot,
                stackTrace
        );
        analyzerService.submit(errorEvent);
    }

    private List<StackTraceElement> extractStackTrace(IThrowableProxy proxy) {
        List<StackTraceElement> result = new ArrayList<>();
        IThrowableProxy current = proxy;
        while (current != null && result.size() < 200) {
            StackTraceElementProxy[] entries = current.getStackTraceElementProxyArray();
            if (entries != null) {
                for (StackTraceElementProxy entry : entries) {
                    result.add(entry.getStackTraceElement());
                    if (result.size() >= 200) {
                        break;
                    }
                }
            }
            current = current.getCause();
        }
        return result;
    }
}
