package com.example.CarSharing.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Dozwolone dla wszystkich ścieżek
                .allowedOrigins("http://localhost:3000") // Dozwolone zapytania z frontendu
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Dozwolone metody HTTP
                .allowedHeaders("*") // Dozwolone wszystkie nagłówki
                .allowCredentials(true); // Dozwolone poświadczenia (np. cookies, tokeny)
    }
}
