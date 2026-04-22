package com.example.dcim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración CORS para permitir peticiones desde aplicaciones móviles
 * 
 * CORS (Cross-Origin Resource Sharing) permite que aplicaciones móviles
 * accedan al backend desde diferentes dominios/orígenes
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir peticiones desde cualquier origen (ajustar en producción)
        // En producción, especifica los dominios permitidos: configuration.setAllowedOrigins(Arrays.asList("https://tu-app.com"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET",      // Leer datos
            "POST",     // Crear datos
            "PUT",      // Actualizar datos completos
            "PATCH",    // Actualizar datos parciales
            "DELETE",   // Eliminar datos
            "OPTIONS"   // Pre-flight request
        ));
        
        // Headers permitidos en las peticiones
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",    // Token de autenticación
            "Content-Type",     // Tipo de contenido (JSON, etc.)
            "Accept",          // Tipo de respuesta aceptada
            "X-Requested-With" // Identificador AJAX
        ));
        
        // Headers expuestos en las respuestas
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition"  // Para descargas de archivos
        ));
        
        // Permitir credenciales (cookies, headers de autenticación)
        configuration.setAllowCredentials(true);
        
        // Cachear la configuración CORS por 1 hora
        configuration.setMaxAge(3600L);
        
        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
