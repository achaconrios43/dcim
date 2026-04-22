package com.example.dcim.api;

import com.example.dcim.entity.Usuario;
import com.example.dcim.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = loginRequest.get("username"); // RUT o EMAIL
            String password = loginRequest.get("password");

            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El usuario es requerido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "La contraseÃ±a es requerida");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Buscar usuario por RUT o EMAIL
            Optional<Usuario> usuarioOpt;
            if (username.contains("@")) {
                // Si contiene @, buscar por email
                usuarioOpt = usuarioService.obtenerUsuarioPorEmail(username);
            } else {
                // Si no, buscar por RUT
                usuarioOpt = usuarioService.obtenerUsuarioPorRut(username);
            }

            if (usuarioOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Usuario o contraseÃ±a incorrectos");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Usuario usuario = usuarioOpt.get();

            // Verificar contraseÃ±a con BCrypt
            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                response.put("success", false);
                response.put("message", "Usuario o contraseÃ±a incorrectos");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Login exitoso
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", usuario.getId());
            userData.put("rut", usuario.getRut());
            userData.put("nombre", usuario.getNombre());
            userData.put("apellido", usuario.getApellido());
            userData.put("email", usuario.getEmail());
            userData.put("rol", usuario.getRol());

            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("usuario", userData);
            response.put("user", userData);
            response.put("token", "jwt-token-" + usuario.getId() + "-" + System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();

        if (token != null && token.startsWith("jwt-token-")) {
            response.put("valid", true);
            response.put("message", "Token vÃ¡lido");
            return ResponseEntity.ok(response);
        }

        response.put("valid", false);
        response.put("message", "Token invÃ¡lido");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "SesiÃ³n cerrada");
        return ResponseEntity.ok(response);
    }
}

