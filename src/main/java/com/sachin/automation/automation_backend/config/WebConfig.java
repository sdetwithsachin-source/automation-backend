package com.sachin.automation.automation_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        // ✅ SCREENSHOTS
        registry.addResourceHandler(
                "/screenshots/**"
        ).addResourceLocations(
                "file:screenshots/"
        );

        // ✅ VIDEOS
        registry.addResourceHandler(
                "/videos/**"
        ).addResourceLocations(
                "file:videos/"
        );

        // ✅ REPORTS
        registry.addResourceHandler(
                "/reports/**"
        ).addResourceLocations(
                "file:reports/"
        );
    }
}
