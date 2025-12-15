package com.example.clases.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security
 * 
 * Esta clase configura la seguridad de la aplicación:
 * - Protección de rutas (endpoints)
 * - Autenticación de usuarios
 * - Autorización por roles (ADMIN/USER)
 * - Encriptación de contraseñas con BCrypt
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configuración de la cadena de filtros de seguridad
     * Define qué URLs están protegidas y qué roles pueden acceder
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuración de autorización de peticiones
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos (CSS, JS, imágenes) - Acceso público
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                
                // Login y páginas públicas - Acceso sin autenticación
                .requestMatchers("/login", "/error").permitAll()
                
                // Permitir acceso público a registro de usuarios
                .requestMatchers("/user/create", "/user/exists").permitAll()
                
                // Acceso a base de datos (solo ADMIN - hardcoded por seguridad)
                .requestMatchers("/h2-console/**").hasRole("ADMIN")
                
                // TODAS LAS DEMÁS RUTAS: Permitir autenticados y validar en controlador
                // Esto permite mostrar mensajes personalizados en vez de 403 Forbidden
                .requestMatchers("/dashboard", "/").authenticated()
                .requestMatchers("/user/**").authenticated()
                .requestMatchers("/ingreso/**").authenticated()
                .requestMatchers("/gestion/**").authenticated()
                .requestMatchers("/dashboard/cliente/**").authenticated()
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configuración del formulario de login
            .formLogin(form -> form
                .loginPage("/login")  // URL de la página de login personalizada
                .loginProcessingUrl("/login")  // URL donde se procesa el login
                .defaultSuccessUrl("/dashboard", true)  // Redirige al dashboard tras login exitoso
                .failureUrl("/login?error=true")  // Redirige al login con error si falla
                .usernameParameter("email")  // Campo del formulario para username (usamos email)
                .passwordParameter("password")  // Campo del formulario para password
                .permitAll()
            )
            
            // Configuración del logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)  // Invalida la sesión HTTP
                .deleteCookies("JSESSIONID")  // Elimina la cookie de sesión
                .permitAll()
            )
            
            // Deshabilitar CSRF solo para H2 Console en desarrollo
            // NOTA: H2 Console debe estar deshabilitado en producción
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            
            // Permitir frames para H2 Console (solo desarrollo)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    /**
     * Bean de BCryptPasswordEncoder para encriptar contraseñas
     * 
     * BCrypt es un algoritmo de hash de una sola vía que:
     * - Genera un salt aleatorio por cada contraseña
     * - Es resistente a ataques de fuerza bruta
     * - Es el estándar recomendado por Spring Security
     * 
     * @return PasswordEncoder configurado con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
