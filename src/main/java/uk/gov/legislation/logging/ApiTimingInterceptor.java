package uk.gov.legislation.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

@Component
public class ApiTimingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiTimingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", Instant.now());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        var startTime = (Instant) request.getAttribute("startTime");
        var totalTime = Duration.between(startTime, Instant.now()).toMillis();

        var method = request.getMethod();
        var uri = request.getRequestURI();
        var status = response.getStatus();

        if (log.isDebugEnabled()) {
            log.debug("API {} {} completed with status {} in {} ms", method, uri, status, totalTime);
        }    }
}

