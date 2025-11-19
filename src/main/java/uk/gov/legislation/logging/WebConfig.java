package uk.gov.legislation.logging;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiTimingInterceptor apiTimingInterceptor;

    public WebConfig(ApiTimingInterceptor apiTimingInterceptor) {
        this.apiTimingInterceptor = apiTimingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiTimingInterceptor)
            .addPathPatterns("/**"); // Apply to all endpoints
    }
}
