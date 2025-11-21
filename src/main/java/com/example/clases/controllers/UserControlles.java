package com.example.clases.controllers;

import com.example.clases.entity.Usuario;
import com.example.clases.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/user")
public class UserControlles {

    @Autowired
    private UsuarioService usuarioService;

    // Verificar autenticación para todas las operaciones
    private boolean verificarAutenticacion(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return false;
        }
        model.addAttribute("usuarioLogueado", usuario);
        return true;
    }

    @GetMapping("/create")
    public String createUser(HttpSession session, Model model){
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        // Solo administradores pueden crear usuarios
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (!"ADMIN".equals(usuarioLogueado.getRol())) {
            model.addAttribute("error", "Solo los administradores pueden crear usuarios");
            return "redirect:/dashboard";
        }
        
        return "user/create";
    }

    @PostMapping("/create")
    public String handleCreate(@RequestParam(name="nombre", required=false) String nombre,
                               @RequestParam(name="apellido", required=false) String apellido,
                               @RequestParam(name="email", required=false) String email,
                               @RequestParam(name="password", required=false) String password,
                               @RequestParam(name="rut", required=false) String rut,
                               @RequestParam(name="ubicacion", required=false) String ubicacion,
                               @RequestParam(name="rol", required=false) String rol,
                               HttpSession session,
                               Model model){
        
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        // Solo administradores pueden crear usuarios
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (!"ADMIN".equals(usuarioLogueado.getRol())) {
            model.addAttribute("error", "Solo los administradores pueden crear usuarios");
            return "redirect:/dashboard";
        }
        
        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Nombre");
            return "user/create";
        }
        if (apellido == null || apellido.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Apellido");
            return "user/create";
        }
        if (email == null || email.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Email");
            return "user/create";
        }
        if (password == null || password.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Contraseña");
            return "user/create";
        }
        if (rut == null || rut.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: RUT");
            return "user/create";
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Ubicación");
            return "user/create";
        }
        if (rol == null || rol.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Rol");
            return "user/create";
        }

        try {
            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre.trim());
            nuevoUsuario.setApellido(apellido.trim());
            nuevoUsuario.setEmail(email.trim());
            nuevoUsuario.setPassword(password);
            nuevoUsuario.setRut(rut.trim());
            nuevoUsuario.setUbicacion(ubicacion.trim());
            nuevoUsuario.setRol(rol.trim());

            // Guardar en base de datos
            Usuario usuarioGuardado = usuarioService.crearUsuario(nuevoUsuario);
            
            System.out.println("[UserController] Usuario creado exitosamente: " + usuarioGuardado.getEmail());
            
            // Redirigir al dashboard después de crear el usuario
            System.out.println("[UserController] REDIRIGIENDO AL DASHBOARD CON SUCCESS=user-created");
            return "redirect:/dashboard?success=user-created";
        } catch (Exception e) {
            System.err.println("[UserController] Error al crear usuario: " + e.getMessage());
            model.addAttribute("errorMessage", "Error al crear usuario: " + e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellido", apellido);
            model.addAttribute("email", email);
            model.addAttribute("rut", rut);
            model.addAttribute("ubicacion", ubicacion);
            model.addAttribute("rol", rol);
            return "user/create";
        }
    }

    @GetMapping("/read/{id}")
    public String readUser(@PathVariable Long id, HttpSession session, Model model){
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
                return "user/read";
            } else {
                model.addAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
        } catch (Exception e) {
            System.err.println("[UserController] Error al leer usuario: " + e.getMessage());
            model.addAttribute("error", "Error al cargar usuario");
            return "redirect:/user/list";
        }
    }

    @GetMapping("/list")
    public String listUser(HttpSession session, Model model){
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
            model.addAttribute("usuarios", usuarios);
            
            // Estadísticas
            long totalUsuarios = usuarioService.contarUsuarios();
            long administradores = usuarioService.contarUsuariosPorRol("ADMIN");
            long tecnicos = usuarioService.contarUsuariosPorRol("USER");
            
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("administradores", administradores);
            model.addAttribute("tecnicos", tecnicos);
            
            return "user/list";
        } catch (Exception e) {
            System.err.println("[UserController] Error al listar usuarios: " + e.getMessage());
            model.addAttribute("error", "Error al cargar la lista de usuarios");
            return "dashboard";
        }
    }

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, HttpSession session, Model model){
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
                return "user/update";
            } else {
                model.addAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
        } catch (Exception e) {
            System.err.println("[UserController] Error al cargar usuario para editar: " + e.getMessage());
            model.addAttribute("error", "Error al cargar usuario");
            return "redirect:/user/list";
        }
    }

    @PostMapping("/update/{id}")
    public String handleUpdate(@PathVariable Long id,
                               @RequestParam(name="nombre", required=false) String nombre,
                               @RequestParam(name="apellido", required=false) String apellido,
                               @RequestParam(name="email", required=false) String email,
                               @RequestParam(name="password", required=false) String password,
                               @RequestParam(name="rut", required=false) String rut,
                               @RequestParam(name="ubicacion", required=false) String ubicacion,
                               @RequestParam(name="rol", required=false) String rol,
                               HttpSession session,
                               Model model){
        
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (!usuarioOpt.isPresent()) {
                model.addAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()){
                model.addAttribute("errorMessage", "Falta el campo: Nombre");
                model.addAttribute("usuario", usuario);
                return "user/update";
            }
            if (apellido == null || apellido.trim().isEmpty()){
                model.addAttribute("errorMessage", "Falta el campo: Apellido");
                model.addAttribute("usuario", usuario);
                return "user/update";
            }
            if (email == null || email.trim().isEmpty()){
                model.addAttribute("errorMessage", "Falta el campo: Email");
                model.addAttribute("usuario", usuario);
                return "user/update";
            }
            if (password == null || password.trim().isEmpty()){
                model.addAttribute("errorMessage", "Falta el campo: Contraseña");
                model.addAttribute("usuario", usuario);
                return "user/update";
            }
            if (rut == null || rut.trim().isEmpty()){
                model.addAttribute("errorMessage", "Falta el campo: RUT");
                model.addAttribute("usuario", usuario);
                return "user/update";
            }
            if (ubicacion == null || ubicacion.trim().isEmpty()){
                model.addAttribute("errorMessage", "Falta el campo: Ubicación");
                model.addAttribute("usuario", usuario);
                return "user/update";
            }
            if (rol == null || rol.trim().isEmpty()){
                model.addAttribute("errorMessage", "Falta el campo: Rol");
                model.addAttribute("usuario", usuario);
                return "user/update";
            }

            // Actualizar datos
            usuario.setNombre(nombre.trim());
            usuario.setApellido(apellido.trim());
            usuario.setEmail(email.trim());
            usuario.setPassword(password);
            usuario.setRut(rut.trim());
            usuario.setUbicacion(ubicacion.trim());
            usuario.setRol(rol.trim());
            
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            
            System.out.println("[UserController] Usuario actualizado: " + usuarioActualizado.getEmail());
            return "redirect:/user/list?success=updated";
            
        } catch (Exception e) {
            System.err.println("[UserController] Error al actualizar usuario: " + e.getMessage());
            model.addAttribute("errorMessage", "Error al actualizar usuario: " + e.getMessage());
            try {
                Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
                if (usuarioOpt.isPresent()) {
                    model.addAttribute("usuario", usuarioOpt.get());
                }
            } catch (Exception ex) {
                // Error al recargar usuario
            }
            return "user/update";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session, Model model){
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        // Solo administradores pueden eliminar usuarios
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (!"ADMIN".equals(usuarioLogueado.getRol())) {
            model.addAttribute("error", "Solo los administradores pueden eliminar usuarios");
            return "redirect:/user/list";
        }
        
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
                return "user/delete";
            } else {
                model.addAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
        } catch (Exception e) {
            System.err.println("[UserController] Error al cargar usuario para eliminar: " + e.getMessage());
            model.addAttribute("error", "Error al cargar usuario");
            return "redirect:/user/list";
        }
    }

    @PostMapping("/delete/{id}")
    public String handleDelete(@PathVariable Long id, HttpSession session, Model model){
        if (!verificarAutenticacion(session, model)) {
            return "redirect:/login";
        }
        
        // Solo administradores pueden eliminar usuarios
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (!"ADMIN".equals(usuarioLogueado.getRol())) {
            model.addAttribute("error", "Solo los administradores pueden eliminar usuarios");
            return "redirect:/user/list";
        }
        
        try {
            usuarioService.eliminarUsuario(id);
            System.out.println("[UserController] Usuario eliminado exitosamente con ID: " + id);
            return "redirect:/user/list?success=deleted";
        } catch (Exception e) {
            System.err.println("[UserController] Error al eliminar usuario: " + e.getMessage());
            model.addAttribute("error", "Error al eliminar usuario: " + e.getMessage());
            return "redirect:/user/list";
        }
    }

    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> emailExists(@RequestParam(name = "email", required = true) String email){
        try {
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.ok(false);
            }
            boolean exists = usuarioService.existeEmail(email.trim());
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/auth-check")
    @ResponseBody
    public Map<String, Object> validateUser(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "password", required = false) String password
            ) {

        Map<String, Object> resp = new HashMap<>();
        resp.put("redirect", false);
        resp.put("valid", false);

        if (email == null || password == null) {
            return resp;
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioService.autenticarUsuario(email, password);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                resp.put("redirect", true);
                resp.put("valid", true);
                resp.put("url", "/dashboard");
                System.out.println("[UserController] Validación exitosa para: " + usuario.getEmail());
            }
        } catch (Exception e) {
            System.err.println("[UserController] Error al validar usuario: " + e.getMessage());
        }

        return resp;
    }

}
