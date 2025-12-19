package com.example.clases.api;

import com.example.clases.entity.Usuario;
import com.example.clases.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioApiController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
            List<Map<String, Object>> usuariosSinPassword = new ArrayList<>();
            for (Usuario usuario : usuarios) {
                usuariosSinPassword.add(convertirUsuarioSinPassword(usuario));
            }
            return ResponseEntity.ok(usuariosSinPassword);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerUsuario(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioOpt.isPresent()) {
                return ResponseEntity.ok(convertirUsuarioSinPassword(usuarioOpt.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario creado exitosamente");
            response.put("data", convertirUsuarioSinPassword(usuarioCreado));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            usuario.setId(id);
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario actualizado exitosamente");
            response.put("data", convertirUsuarioSinPassword(usuarioActualizado));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<Map<String, Boolean>> verificarEmail(@RequestParam String email) {
        try {
            boolean existe = usuarioService.existeEmail(email);
            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/verificar-rut")
    public ResponseEntity<Map<String, Boolean>> verificarRut(@RequestParam String rut) {
        try {
            boolean existe = usuarioService.existeRut(rut);
            Map<String, Boolean> response = new HashMap<>();
            response.put("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private Map<String, Object> convertirUsuarioSinPassword(Usuario usuario) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", usuario.getId());
        map.put("rut", usuario.getRut());
        map.put("nombre", usuario.getNombre());
        map.put("apellido", usuario.getApellido());
        map.put("email", usuario.getEmail());
        map.put("rol", usuario.getRol());
        map.put("creatAt", usuario.getCreatAt());
        map.put("updateAt", usuario.getUpdateAt());
        return map;
    }
}
