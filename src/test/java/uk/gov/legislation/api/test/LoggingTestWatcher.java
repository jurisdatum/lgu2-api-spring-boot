package uk.gov.legislation.api.test;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;


public class LoggingTestWatcher implements TestWatcher, BeforeEachCallback, AfterTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(LoggingTestWatcher.class);

    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";
    private static final String START_TIME_KEY = "start time";

    @Override
    public void beforeEach(ExtensionContext context) {
        getStore(context).put(START_TIME_KEY, Instant.now());
        logMessage("▶️  STARTING", CYAN, context);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        long duration = Duration.between(
            getStore(context).remove(START_TIME_KEY, Instant.class),
            Instant.now()
        ).toMillis();

        log.info("{}⏱️  DURATION: {}ms for {}.{}{}{}",
            CYAN, duration, getClassName(context), getMethodName(context), formatTags(context), RESET);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("{}✅  PASSED: {}{}{}", GREEN, context.getDisplayName(), formatTags(context), RESET);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        logFailureWithStackTrace(context.getDisplayName(), cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        log.warn("{}🚫 ABORTED  {}.{}{} | Reason: {}{}",
            YELLOW, getClassName(context), getMethodName(context), formatTags(context), cause.getMessage(), RESET);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        log.warn("{}⚠️ DISABLED {}.{}{} | Reason: {}{}",
            YELLOW, getClassName(context), getMethodName(context), formatTags(context), reason.orElse("Not specified"), RESET);
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

    private String getClassName(ExtensionContext context) {
        return context.getTestClass().map(Class::getSimpleName).orElse("UnknownClass");
    }

    private String getMethodName(ExtensionContext context) {
        return context.getTestMethod().map(Method::getName).orElse("unknownMethod");
    }

    private String formatTags(ExtensionContext context) {
        return context.getTags().isEmpty() ? "" : " [tags: " + String.join(", ", context.getTags()) + "]";
    }

    private void logMessage(String prefix, String color, ExtensionContext context) {
        log.info("{}{} {}.{}{} {}", color, prefix, getClassName(context), getMethodName(context), formatTags(context), RESET);
    }

    private void logFailureWithStackTrace(String testName, Throwable exception) {
        log.error(buildErrorMessage(testName, exception));
        Optional.ofNullable(exception.getCause()).ifPresent(cause -> log.error("Caused by:", cause));
    }

    private String buildErrorMessage(String testName, Throwable exception) {
        return new StringBuilder()
            .append(RED)
            .append("❌  TEST FAILED: ").append(testName).append("\n")
            .append("Reason: ").append(exception.getMessage()).append("\n")
            .append("Stack Trace:\n")
            .append(Arrays.stream(exception.getStackTrace())
                .map(e -> "\tat " + e + "\n")
                .collect(Collectors.joining()))
            .toString();
    }
}

