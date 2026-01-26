package com.pinheiro.musica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Para todos endpoints
                .allowedOrigins("https://player.gpinheiro.cloud")  // Origem do frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Inclua POST para uploads
                .allowedHeaders("*")  // Permite headers como Content-Type para multipart
                .exposedHeaders("*")  // Se precisar expor headers custom
                .allowCredentials(true)  // Se usar auth com cookies
                .maxAge(3600);  // Cache do preflight
    }
}
