package com.example.loganalyzer.autoconfigure;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.example.loganalyzer.config.LogAnalyzerProperties;
import com.example.loganalyzer.context.CodeContextResolver;
import com.example.loganalyzer.core.ErrorAnalyzerService;
import com.example.loganalyzer.core.PromptBuilder;
import com.example.loganalyzer.core.TerminalReporter;
import com.example.loganalyzer.logging.OllamaAnalysisAppender;
import com.example.loganalyzer.ollama.OllamaClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(LogAnalyzerProperties.class)
@ConditionalOnProperty(prefix = "log.analyzer", name = "enabled", havingValue = "true")
public class LogAnalyzerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CodeContextResolver codeContextResolver(LogAnalyzerProperties properties) {
        return new CodeContextResolver(properties);
    }

    @Bean
    public PromptBuilder promptBuilder(LogAnalyzerProperties properties) {
        return new PromptBuilder(properties);
    }

    @Bean
    public OllamaClient ollamaClient(ObjectMapper objectMapper, LogAnalyzerProperties properties) {
        return new OllamaClient(objectMapper, properties);
    }

    @Bean
    public TerminalReporter terminalReporter() {
        return new TerminalReporter();
    }

    @Bean
    public ErrorAnalyzerService errorAnalyzerService(
            CodeContextResolver resolver,
            PromptBuilder promptBuilder,
            OllamaClient ollamaClient,
            TerminalReporter reporter) {
        return new ErrorAnalyzerService(resolver, promptBuilder, ollamaClient, reporter);
    }

    @Bean
    public OllamaAnalysisAppender ollamaAnalysisAppender(
            ErrorAnalyzerService analyzerService,
            LogAnalyzerProperties properties) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        OllamaAnalysisAppender appender = new OllamaAnalysisAppender(analyzerService, properties);
        appender.setName("OLLAMA_ANALYZER");
        appender.setContext(context);
        appender.start();

        Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root.getAppender("OLLAMA_ANALYZER") == null) {
            root.addAppender(appender);
        }
        return appender;
    }
}
