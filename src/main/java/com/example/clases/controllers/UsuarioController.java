package com.example.clases.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.clases.entity.Usuario;
import com.example.clases.service.UsuarioService;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Listar todos los usuarios
    @GetMapping({"/list", "/"})
    public String listarUsuarios(Model model) {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("totalUsuarios", usuarios.size());
            model.addAttribute("administradores", usuarioService.contarUsuariosPorRol("ADMIN"));
            model.addAttribute("tecnicos", usuarioService.contarUsuariosPorRol("USER"));
            return "user/list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la lista de usuarios: " + e.getMessage());
            return "user/list";
        }
    }

    // Mostrar formulario para crear usuario
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "user/create";
    }

    // Procesar la creación de usuario
    @PostMapping("/create")
    public String crearUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.crearUsuario(usuario);
            redirectAttributes.addAttribute("success", "created");
            return "redirect:/user/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear usuario: " + e.getMessage());
            return "redirect:/user/create";
        }
    }

    // Mostrar detalles de un usuario (read)
    @GetMapping("/read/{id}")
    public String leerUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
                return "user/read";
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al obtener el usuario: " + e.getMessage());
            return "redirect:/user/list";
        }
    }

    // Mostrar formulario para actualizar usuario
    @GetMapping("/update/{id}")
    public String mostrarFormularioActualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
                return "user/update";
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el usuario: " + e.getMessage());
            return "redirect:/user/list";
        }
    }

    // Procesar la actualización de usuario
    @PostMapping("/update/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuario.setId(id);
            usuarioService.actualizarUsuario(usuario);
            redirectAttributes.addAttribute("success", "updated");
            return "redirect:/user/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar usuario: " + e.getMessage());
            return "redirect:/user/update/" + id;
        }
    }

    // Mostrar confirmación de eliminación
    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
                model.addAttribute("idx", id);
                return "user/delete";
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el usuario: " + e.getMessage());
            return "redirect:/user/list";
        }
    }

    // Procesar la eliminación de usuario
    @PostMapping("/delete")
    public String eliminarUsuario(@RequestParam("idx") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addAttribute("success", "deleted");
            return "redirect:/user/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
            return "redirect:/user/list";
        }
    }
}
