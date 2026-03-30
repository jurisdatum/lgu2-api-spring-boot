package uk.gov.legislation.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static uk.gov.legislation.logging.LogDetails.createCustomLogMessage;

@Aspect
@Component
public class PerformanceLoggingAspect {


    private final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public PerformanceLoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Pointcut("execution(* uk.gov.legislation.transform.Transforms.*(..))")
    public void transforms() {}

    @Pointcut("execution(* uk.gov.legislation.data.virtuoso.Virtuoso.*(..))")
    public void virtuoso() {}

    @Pointcut("execution(* uk.gov.legislation.data.marklogic.MarkLogic.*(..))")
    public void marklogic() {}

    @Around("transforms() || virtuoso() || marklogic()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        var methodName  = joinPoint.getSignature().getName();
        var args        = joinPoint.getArgs();
        var endpoint    = (args.length > 0) ? String.valueOf(args[0]) : "N/A";

        ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (attrs != null) ? attrs.getRequest() : null;
        String apiEndpoint = "N/A";
        if (request != null) {
            apiEndpoint = request.getMethod() + " " + request.getRequestURI();
        }
        var startTimeISO = formatTimestamp(start);
        var endTimeISO   = formatTimestamp(end);

        var message = createCustomLogMessage(methodName, endpoint, end - start);

        var logDetails = new LogDetails(
            methodName,
            end - start,
            message,
            startTimeISO,
            endTimeISO,apiEndpoint
        );

        if (log.isDebugEnabled()) {
            log.debug(objectMapper.writeValueAsString(logDetails));
        }

        return result;
    }
    private String formatTimestamp(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .format(ISO_FORMATTER);
    }
}
