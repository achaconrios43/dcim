package com.example.dcim.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.entity.GestionAcceso;
import com.example.dcim.entity.Usuario;
import com.example.dcim.service.GestionAccesoService;

@Controller
@RequestMapping("/gestion")
public class GestionAccesoController {

    @Autowired
    private GestionAccesoService gestionAccesoService;
    
    @Autowired
    private IUsuarioDao usuarioDao;

    @GetMapping("/list")
    public String listarGestiones(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder a Gestión de Accesos
        if ("USER".equals(usuario.getRol())) {
            model.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            model.addAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        }
        
        model.addAttribute("usuarioLogueado", usuario);

        List<GestionAcceso> gestiones = gestionAccesoService.listarTodas();
        model.addAttribute("gestiones", gestiones);
        model.addAttribute("titulo", "Gestión de Accesos - Listado Completo");
        return "gestion/gestion-list";
    }

    @GetMapping("/list/{sitio}")
    public String listarGestionesPorSitio(@PathVariable String sitio, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder
        if ("USER".equals(usuario.getRol())) {
            model.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            model.addAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        }
        
        model.addAttribute("usuarioLogueado", usuario);

        List<GestionAcceso> gestiones = gestionAccesoService.listarPorSitio(sitio);
        model.addAttribute("gestiones", gestiones);
        model.addAttribute("sitio", sitio);
        model.addAttribute("titulo", "Gestión de Accesos - " + sitio);
        return "gestion/gestion-list";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder
        if ("USER".equals(usuario.getRol())) {
            model.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            model.addAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        }
        
        model.addAttribute("usuarioLogueado", usuario);
        
        GestionAcceso gestion = new GestionAcceso();
        gestion.setFechaRegistro(LocalDate.now());
        gestion.setHoraRegistro(LocalTime.now());
        gestion.setUsuarioIngresa(usuario.getNombre() + " " + usuario.getApellido());
        gestion.setGestionRealizada(false);
        gestion.setEstadoAprobacion("Pendiente");
        
        model.addAttribute("gestion", gestion);
        model.addAttribute("titulo", "Nueva Gestión de Acceso");
        return "gestion/gestion-create";
    }

    @PostMapping("/save")
    public String guardarGestion(@ModelAttribute GestionAcceso gestion, 
                                 RedirectAttributes flash,
                                 Authentication authentication,
                                 Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder
        if ("USER".equals(usuario.getRol())) {
            flash.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            return "redirect:/dashboard";
        }

        try {
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

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder
        if ("USER".equals(usuario.getRol())) {
            model.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            model.addAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        }
        
        model.addAttribute("usuarioLogueado", usuario);

        GestionAcceso gestion = gestionAccesoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Gestión no encontrada: " + id));
        
        model.addAttribute("gestion", gestion);
        model.addAttribute("titulo", "Editar Gestión de Acceso");
        return "gestion/gestion-edit";
    }

    @PostMapping("/update/{id}")
    public String actualizarGestion(@PathVariable Long id,
                                    @ModelAttribute GestionAcceso gestion,
                                    RedirectAttributes flash,
                                    Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder
        if ("USER".equals(usuario.getRol())) {
            flash.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            return "redirect:/dashboard";
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

    @GetMapping("/delete/{id}")
    public String eliminarGestion(@PathVariable Long id, RedirectAttributes flash, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder
        if ("USER".equals(usuario.getRol())) {
            flash.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            return "redirect:/dashboard";
        }

        try {
            gestionAccesoService.eliminar(id);
            flash.addFlashAttribute("success", "Gestión eliminada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar la gestión: " + e.getMessage());
        }
        return "redirect:/gestion/list";
    }

    @GetMapping("/view/{id}")
    public String verDetalle(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder
        if ("USER".equals(usuario.getRol())) {
            model.addAttribute("error", "Acceso denegado. No tiene permisos para acceder a Gestión de Accesos.");
            model.addAttribute("usuarioLogueado", usuario);
            return "redirect:/dashboard";
        }
        
        model.addAttribute("usuarioLogueado", usuario);

        GestionAcceso gestion = gestionAccesoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Gestión no encontrada: " + id));
        
        model.addAttribute("gestion", gestion);
        model.addAttribute("titulo", "Detalle de Gestión");
        return "gestion/gestion-view";
    }
}
