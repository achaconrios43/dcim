package com.example.clases.controllers;

import com.example.clases.entity.Usuario;
import com.example.clases.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam(name = "username", required = false) String username,
                                @RequestParam(name = "password", required = false) String password,
                                HttpSession session,
                                Model model) {
        try {
            if (username == null || username.trim().isEmpty()) {
                model.addAttribute("error", "El nombre de usuario es obligatorio");
                return "login";
            }
            
            if (password == null || password.trim().isEmpty()) {
                model.addAttribute("error", "La contraseña es obligatoria");
                return "login";
            }

            // Autenticar contra la base de datos H2
            Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(username.trim(), password);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                
                // Guardar información del usuario en la sesión
                session.setAttribute("usuarioLogueado", usuario);
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("usuarioNombre", usuario.getNombre() + " " + usuario.getApellido());
                session.setAttribute("usuarioEmail", usuario.getEmail());
                session.setAttribute("usuarioRol", usuario.getRol());
                session.setAttribute("usuarioUbicacion", usuario.getUbicacion());
                
                System.out.println("[LoginController] Login exitoso para: " + usuario.getEmail());
                System.out.println("[LoginController] Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
                System.out.println("[LoginController] Rol: " + usuario.getRol());
                System.out.println("[LoginController] Ubicación: " + usuario.getUbicacion());
                
                // Redirigir a la página principal con opciones del menú
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Credenciales inválidas. Verifique su usuario y contraseña.");
                model.addAttribute("username", username); // Mantener el username para no perderlo
                return "login";
            }
        } catch (Exception e) {
            System.err.println("[LoginController] Error durante el login: " + e.getMessage());
            model.addAttribute("error", "Error interno del servidor. Intente nuevamente.");
            return "login";
        }
    }

    // Endpoint AJAX para validar si el usuario existe
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

    // Endpoint AJAX para validar credenciales
    @GetMapping("/user/validate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validarCredenciales(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String ubicacion) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(email, password);
            
            if (usuarioOpt.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "redirect", true,
                    "url", "/dashboard",
                    "valid", true
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "redirect", false,
                    "valid", false
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "redirect", false,
                "valid", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }
}
