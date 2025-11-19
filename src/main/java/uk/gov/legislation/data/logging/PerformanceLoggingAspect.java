package uk.gov.legislation.data.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);
    private final ObjectMapper objectMapper;

    public PerformanceLoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Define pointcuts for different classes
    @Pointcut("execution(* uk.gov.legislation.transform.Transforms.*(..))")
    public void transforms() {}

    @Pointcut("execution(* uk.gov.legislation.data.marklogic.legislation.Legislation.*(..))")
    public void legislation() {}

    @Pointcut("execution(* uk.gov.legislation.data.virtuoso.Virtuoso.*(..))")
    public void virtuoso() {}

    @Pointcut("execution(* uk.gov.legislation.data.marklogic.changes.Changes.*(..))")
    public void changes() {}

    @Pointcut("execution(* uk.gov.legislation.data.marklogic.search.Search.*(..))")
    public void search() {}

    // Apply the logging advice to each class
    @Around("transforms() || legislation() || virtuoso() || changes() || search()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();

        long end = System.currentTimeMillis();
        long duration = end - start;
        String methodName = joinPoint.getSignature().getName();
        String customMessage = createCustomLogMessage(methodName, duration);

        LogDetails logDetails = new LogDetails(methodName, duration, customMessage, start, end);

        // Convert log details to JSON and log it
        String jsonLogMessage = objectMapper.writeValueAsString(logDetails);
        log.debug(jsonLogMessage);

        return proceed;
    }

    private String createCustomLogMessage(String methodName, long duration) {
        return switch(methodName) {

            // Category 1: CLML → Other Formats
            case "clml2document", "clml2akn", "clml2html", "clml2docx", "clml2toc" ->
                "Transformation from "
                    + methodName
                    + " completed in "
                    + duration
                    + " ms.";

            // Category 2: Mark Logic (legislation.xq), (Search.xq), (Changes.xq) Database-related methods
            case "getDocument", "getAndFollowRedirect", "getTableOfContents",
                 "getDocumentSection", "getMetadata", "getAtom", "fetch" ->
                "MarkLogic Database operation for method ["
                    + methodName
                    + "] completed in "
                    + duration
                    + " ms.";

            // Category 3: Virtuoso Database-related methods
            case "query" ->
                " Virtuoso Database operation for method ["
                    + methodName
                    + "] completed in "
                    + duration
                    + " ms.";

            default -> methodName + " took " + duration + "ms";
        };
    }

    // Custom record to structure the log details in JSON format
    public record LogDetails(
        String methodName,
        long durationMillis,
        String message,
        long startTime,
        long endTime
    ) { }
}
