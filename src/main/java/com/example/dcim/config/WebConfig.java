package com.example.dcim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración adicional de Spring MVC
 * Maneja vistas simples sin necesidad de controladores
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura view controllers para páginas estáticas
     * Esto evita conflictos con Spring Security
     */
    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Sin mapeos estáticos por ahora.
        // /login y / son manejados en MainController para redirigir
        // automáticamente al dashboard cuando ya existe sesión activa.
    }
}
