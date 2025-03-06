package ru.umd.intershop.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import ru.umd.intershop.web.config.interceptor.BackRedirectInterceptor;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final BackRedirectInterceptor backRedirectInterceptor;

    @Value("${app.image-file-base-path}")
    private String imageBasePath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(backRedirectInterceptor)
            .addPathPatterns("/main/items/**", "/items/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:" + imageBasePath + File.separator);
    }
}