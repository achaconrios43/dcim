package com.example.clases.controllers;

import com.example.clases.entity.GestionAcceso;
import com.example.clases.service.GestionAccesoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/gestion")
public class GestionAccesoController {

    @Autowired
    private GestionAccesoService gestionAccesoService;

    // Listar todas las gestiones
    @GetMapping("/list")
    public String listarGestiones(Model model, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        List<GestionAcceso> gestiones = gestionAccesoService.listarTodas();
        model.addAttribute("gestiones", gestiones);
        model.addAttribute("titulo", "Gestión de Accesos - Listado Completo");
        return "gestion/list";
    }

    // Listar gestiones por sitio
    @GetMapping("/list/{sitio}")
    public String listarGestionesPorSitio(@PathVariable String sitio, Model model, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        List<GestionAcceso> gestiones = gestionAccesoService.listarPorSitio(sitio);
        model.addAttribute("gestiones", gestiones);
        model.addAttribute("sitio", sitio);
        model.addAttribute("titulo", "Gestión de Accesos - " + sitio);
        return "gestion/list";
    }

    // Mostrar formulario para crear nueva gestión
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        String nombreUsuario = (String) session.getAttribute("usuarioNombre");
        
        GestionAcceso gestion = new GestionAcceso();
        gestion.setFechaRegistro(LocalDate.now());
        gestion.setHoraRegistro(LocalTime.now());
        gestion.setUsuarioIngresa(nombreUsuario);
        gestion.setGestionRealizada(false);
        gestion.setEstadoAprobacion("Pendiente");
        
        model.addAttribute("gestion", gestion);
        model.addAttribute("titulo", "Nueva Gestión de Acceso");
        return "gestion/create";
    }

    // Procesar formulario de creación
    @PostMapping("/save")
    public String guardarGestion(@ModelAttribute GestionAcceso gestion, 
                                 RedirectAttributes flash,
                                 HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        try {
            // Establecer fecha y hora de registro si no vienen del formulario
            if (gestion.getFechaRegistro() == null) {
                gestion.setFechaRegistro(LocalDate.now());
            }
            if (gestion.getHoraRegistro() == null) {
                gestion.setHoraRegistro(LocalTime.now());
            }
            
            gestionAccesoService.guardar(gestion);
            flash.addFlashAttribute("success", "Gestión de acceso creada exitosamente");
            return "redirect:/gestion/list";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al crear la gestión: " + e.getMessage());
            return "redirect:/gestion/create";
        }
    }

    // Mostrar formulario para editar
    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        GestionAcceso gestion = gestionAccesoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Gestión no encontrada: " + id));
        
        model.addAttribute("gestion", gestion);
        model.addAttribute("titulo", "Editar Gestión de Acceso");
        return "gestion/edit";
    }

    // Procesar actualización
    @PostMapping("/update/{id}")
    public String actualizarGestion(@PathVariable Long id,
                                    @ModelAttribute GestionAcceso gestion,
                                    RedirectAttributes flash,
                                    HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        try {
            gestion.setId(id);
            gestionAccesoService.guardar(gestion);
            flash.addFlashAttribute("success", "Gestión actualizada exitosamente");
            return "redirect:/gestion/list";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar la gestión: " + e.getMessage());
            return "redirect:/gestion/edit/" + id;
        }
    }

    // Eliminar gestión
    @GetMapping("/delete/{id}")
    public String eliminarGestion(@PathVariable Long id, RedirectAttributes flash, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        try {
            gestionAccesoService.eliminar(id);
            flash.addFlashAttribute("success", "Gestión eliminada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar la gestión: " + e.getMessage());
        }
        return "redirect:/gestion/list";
    }

    // Ver detalle de gestión
    @GetMapping("/view/{id}")
    public String verDetalle(@PathVariable Long id, Model model, HttpSession session) {
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        if (usuarioEmail == null) {
            return "redirect:/login";
        }

        GestionAcceso gestion = gestionAccesoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Gestión no encontrada: " + id));
        
        model.addAttribute("gestion", gestion);
        model.addAttribute("titulo", "Detalle de Gestión");
        return "gestion/view";
    }
}
