package com.example.dcim.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dcim.entity.IngresoAP;
import com.example.dcim.entity.Usuario;
import com.example.dcim.service.IngresoAPService;
import com.example.dcim.service.TemperaturaService;
import com.example.dcim.service.UsuarioService;

@Controller
public class IngresoController {

    @Autowired
    private IngresoAPService ingresoAPService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TemperaturaService temperaturaService;

    @GetMapping("/ingresoap")
    public String ingresoForm(Model model){
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName(); // El nombre de usuario es el email
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(email);
            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuarioActual", usuarioOpt.get());
            }
        }
        model.addAttribute("sitiosCatalogo", temperaturaService.listarSitiosActivos());
        model.addAttribute("salasPorSitio", temperaturaService.obtenerMapaSalasPorSitio());
        return "ingresoap";
    }

    @PostMapping("/ingresoap")
    public String handleIngreso(
            @RequestParam(name="turno") String turno,
            @RequestParam(name="nombreUsuario") String nombreUsuario,
            @RequestParam(name="fechaInicio") String fechaInicio,
            @RequestParam(name="horaInicio") String horaInicio,
            @RequestParam(name="fechaTermino", required=false) String fechaTermino,
            @RequestParam(name="horaTermino", required=false) String horaTermino,
            @RequestParam(name="nombreTecnico") String nombreTecnico,
            @RequestParam(name="rutTecnico") String rutTecnico,
            @RequestParam(name="cargoTecnico") String cargoTecnico,
            @RequestParam(name="empresaDemandante") String empresaDemandante,
            @RequestParam(name="empresaContratista") String empresaContratista,
            @RequestParam(name="salaRemedy") String salaRemedy,
            @RequestParam(name="tipoTicket") String tipoTicket,
            @RequestParam(name="numeroTicket") String numeroTicket,
            @RequestParam(name="aprobador") String aprobador,
            @RequestParam(name="escolta") String escolta,
            @RequestParam(name="guiaDespacho", required=false) String guiaDespacho,
            @RequestParam(name="sitioIngreso", required=false) String sitioIngreso,
            @RequestParam(name="salaIngresa") String salaIngresa,
            @RequestParam(name="rackIngresa", required=false) String rackIngresa,
            @RequestParam(name="motivoIngreso") String motivoIngreso,
            @RequestParam(name="actividadRemedy") String actividadRemedy,
            Model model,
            RedirectAttributes redirectAttributes){
        
        try {
            // Crear nueva entidad IngresoAP
            IngresoAP ingresoAP = new IngresoAP();
            
            // Asignar valores de formulario
            ingresoAP.setTurno(turno);
            ingresoAP.setNombreUsuario(nombreUsuario);
            ingresoAP.setFechaInicio(LocalDate.parse(fechaInicio));
            ingresoAP.setHoraInicio(LocalTime.parse(horaInicio));
            
            // Campos opcionales de fecha término
            if (fechaTermino != null && !fechaTermino.trim().isEmpty()) {
                ingresoAP.setFechaTermino(LocalDate.parse(fechaTermino));
            }
            if (horaTermino != null && !horaTermino.trim().isEmpty()) {
                ingresoAP.setHoraTermino(LocalTime.parse(horaTermino));
            }
            
            // Información del técnico
            ingresoAP.setNombreTecnico(nombreTecnico);
            ingresoAP.setRutTecnico(rutTecnico);
            ingresoAP.setCargoTecnico(cargoTecnico);
            
            // Empresas
            ingresoAP.setEmpresaDemandante(empresaDemandante);
            ingresoAP.setEmpresaContratista(empresaContratista);
            
            // Ticket y autorización
            ingresoAP.setSalaRemedy(salaRemedy);
            ingresoAP.setTipoTicket(tipoTicket);
            ingresoAP.setNumeroTicket(numeroTicket);
            ingresoAP.setAprobador(aprobador);
            ingresoAP.setEscolta(escolta);
            
            // Campos opcionales
            if (guiaDespacho != null && !guiaDespacho.trim().isEmpty()) {
                ingresoAP.setGuiaDespacho(guiaDespacho);
            }
            if (sitioIngreso != null && !sitioIngreso.trim().isEmpty()) {
                ingresoAP.setSitioIngreso(sitioIngreso);
            }
            if (rackIngresa != null && !rackIngresa.trim().isEmpty()) {
                ingresoAP.setRackIngresa(rackIngresa);
            }
            
            // Ubicación y actividad
            ingresoAP.setSalaIngresa(salaIngresa);
            ingresoAP.setMotivoIngreso(motivoIngreso);
            ingresoAP.setActividadRemedy(actividadRemedy);
            
            // Guardar en base de datos usando el servicio
            ingresoAPService.registrarIngreso(ingresoAP);
            
            redirectAttributes.addFlashAttribute("successMessage", "Ingreso registrado correctamente con ticket: " + numeroTicket);
            return "redirect:/ingresoap";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al registrar ingreso: " + e.getMessage());
            return "ingresoap";
        }
    }

    // Listar todos los ingresos
    @GetMapping("/user/ingresoap-list")
    public String listarIngresos(Model model) {
        try {
            List<IngresoAP> ingresos = ingresoAPService.obtenerTodosLosIngresos();
            model.addAttribute("ingresos", ingresos);
            return "user/ingresoap-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar la lista de ingresos: " + e.getMessage());
            return "user/ingresoap-list";
        }
    }

    // Ver detalles de un ingreso
    @GetMapping("/user/ingresoap-read/{id}")
    public String verIngreso(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.obtenerIngresoPorId(id);
            if (ingreso.isPresent()) {
                model.addAttribute("ingreso", ingreso.get());
                return "user/ingresoap-read";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Ingreso no encontrado");
                return "redirect:/user/ingresoap-list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el ingreso: " + e.getMessage());
            return "redirect:/user/ingresoap-list";
        }
    }

    // Mostrar formulario de actualización
    @GetMapping("/user/ingresoap-update/{id}")
    public String mostrarFormularioActualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.obtenerIngresoPorId(id);
            if (ingreso.isPresent()) {
                model.addAttribute("ingreso", ingreso.get());
                return "user/ingresoap-update";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Ingreso no encontrado");
                return "redirect:/user/ingresoap-list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el ingreso: " + e.getMessage());
            return "redirect:/user/ingresoap-list";
        }
    }

    // Procesar actualización de ingreso
    @PostMapping("/user/ingresoap-update/{id}")
    public String actualizarIngreso(@PathVariable Long id, @ModelAttribute IngresoAP ingresoAP, 
                                    RedirectAttributes redirectAttributes) {
        try {
            ingresoAP.setId(id);
            ingresoAPService.actualizarIngreso(ingresoAP);
            redirectAttributes.addFlashAttribute("successMessage", "Ingreso actualizado correctamente");
            return "redirect:/user/ingresoap-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el ingreso: " + e.getMessage());
            return "redirect:/user/ingresoap-update/" + id;
        }
    }

    // Mostrar confirmación de eliminación
    @GetMapping("/user/ingresoap-delete/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.obtenerIngresoPorId(id);
            if (ingreso.isPresent()) {
                model.addAttribute("ingreso", ingreso.get());
                return "user/ingresoap-delete";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Ingreso no encontrado");
                return "redirect:/user/ingresoap-list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el ingreso: " + e.getMessage());
            return "redirect:/user/ingresoap-list";
        }
    }

    // Procesar eliminación
    @PostMapping("/user/ingresoap-delete/{id}")
    public String eliminarIngreso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ingresoAPService.obtenerIngresoPorId(id)
                .orElseThrow(() -> new Exception("Ingreso no encontrado"));
            // Aquí deberías implementar el método eliminar en el servicio
            redirectAttributes.addFlashAttribute("successMessage", "Ingreso eliminado correctamente");
            return "redirect:/user/ingresoap-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el ingreso: " + e.getMessage());
            return "redirect:/user/ingresoap-list";
        }
    }
}
