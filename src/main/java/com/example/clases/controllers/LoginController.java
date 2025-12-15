package com.example.clases.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.clases.entity.Usuario;
import com.example.clases.service.UsuarioService;

import org.springframework.security.core.Authentication;

/**
 * Controlador de Login
 * 
 * NOTA IMPORTANTE: Spring Security maneja automáticamente:
 * - GET /login → Muestra formulario de login
 * - POST /login → Procesa autenticación (CustomUserDetailsService + BCryptPasswordEncoder)
 * - Redirección tras login exitoso → /dashboard
 * - Redirección tras login fallido → /login?error=true
 * 
 * No es necesario código manual para login/logout, Spring Security lo gestiona todo.
 */
@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint AJAX para validar si el usuario existe
     * Usado en el formulario de registro para validación en tiempo real
     */
    @GetMapping("/user/exists")
    @ResponseBody
    public ResponseEntity<Boolean> verificarUsuarioExiste(@RequestParam String email) {
        try {
            boolean existe = usuarioService.existeEmail(email) || 
                           usuarioService.obtenerUsuarioPorEmail(email).isPresent();
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}

