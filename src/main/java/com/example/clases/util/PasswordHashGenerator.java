package com.example.clases.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar hashes BCrypt de contraseñas
 * 
 * Uso:
 * 1. Ejecutar este archivo como aplicación Java standalone
 * 2. Copiar el hash generado
 * 3. Usar en database-init.sql o para crear usuarios
 * 
 * IMPORTANTE: No usar en producción, solo para generar hashes iniciales
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Contraseñas para hashear
        String[] passwords = {
            "Admin123!",
            "Ayj05102017",
            "User123!"
        };
        
        System.out.println("========================================");
        System.out.println("GENERADOR DE HASHES BCRYPT");
        System.out.println("========================================\n");
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("Password: " + password);
            System.out.println("BCrypt:   " + hash);
            System.out.println();
        }
        
        System.out.println("========================================");
        System.out.println("NOTA: Cada ejecución genera hashes diferentes");
        System.out.println("(BCrypt usa salt aleatorio)");
        System.out.println("========================================");
    }
}
