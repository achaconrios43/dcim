package com.example.clases.controllers;

import com.example.clases.entity.IngresoAP;
import com.example.clases.entity.Usuario;
import com.example.clases.service.IngresoAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador para gestión de ingresos al Centro de Datos
 */
@Controller
@RequestMapping("/ingreso")
public class IngresoController {

    @Autowired
    private IngresoAPService ingresoAPService;

    @GetMapping(value = {"/", ""})
    public String redirigirAForm() {
        return "redirect:/ingreso/ap";
    }

    @GetMapping("/ap")
    public String mostrarFormularioIngreso(@RequestParam(name="nombre", required=false) String nombre,
                                          @RequestParam(name="rut", required=false) String rut,
                                          HttpSession session,
                                          Model model) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        
        // Auto-completar con el nombre del usuario logueado por defecto
        model.addAttribute("nombreUsuario", usuario.getNombre());
        
        if (nombre != null) model.addAttribute("nombre", nombre);
        if (rut != null) model.addAttribute("rut", rut);
        
        return "ingresoap";
    }

    @PostMapping("/ap")
    public String handleIngreso(@RequestParam(name="turno", required=false) String turno,
                                @RequestParam(name="nombreUsuario", required=false) String nombreUsuario,
                                @RequestParam(name="fechaInicio", required=false) String fechaInicio,
                                @RequestParam(name="horaInicio", required=false) String horaInicio,
                                @RequestParam(name="fechaTermino", required=false) String fechaTermino,
                                @RequestParam(name="horaTermino", required=false) String horaTermino,
                                @RequestParam(name="nombreTecnico", required=false) String nombreTecnico,
                                @RequestParam(name="rutTecnico", required=false) String rutTecnico,
                                @RequestParam(name="empresaDemandante", required=false) String empresaDemandante,
                                @RequestParam(name="empresaContratista", required=false) String empresaContratista,
                                @RequestParam(name="cargoTecnico", required=false) String cargoTecnico,
                                @RequestParam(name="salaRemedy", required=false) String salaRemedy,
                                @RequestParam(name="tipoTicket", required=false) String tipoTicket,
                                @RequestParam(name="numeroTicket", required=false) String numeroTicket,
                                @RequestParam(name="aprobador", required=false) String aprobador,
                                @RequestParam(name="escolta", required=false) String escolta,
                                @RequestParam(name="motivoIngreso", required=false) String motivoIngreso,
                                @RequestParam(name="guiaDespacho", required=false) String guiaDespacho,
                                @RequestParam(name="salaIngresa", required=false) String salaIngresa,
                                @RequestParam(name="rackIngresa", required=false) String rackIngresa,
                                @RequestParam(name="actividadRemedy", required=false) String actividadRemedy,
                                HttpSession session,
                                Model model){
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        
        // Si nombreUsuario está vacío, usar el nombre del usuario logueado
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            nombreUsuario = usuario.getNombre();
        }
        
        // Validar campos requeridos
        String[] campos = {turno, nombreUsuario, fechaInicio, horaInicio, nombreTecnico, rutTecnico,
                          empresaDemandante, empresaContratista, cargoTecnico, salaRemedy, tipoTicket,
                          numeroTicket, aprobador, escolta, motivoIngreso, salaIngresa, rackIngresa, actividadRemedy};
        String[] nombres = {"Turno", "Nombre Usuario", "Fecha Inicio", "Hora Inicio", "Nombre Técnico", "RUT Técnico",
                           "Empresa Demandante", "Empresa Contratista", "Cargo Técnico", "Sala REMEDY", "Tipo Ticket",
                           "Número Ticket", "Aprobador", "Escolta", "Motivo Ingreso", "Sala Ingresa", "Rack Ingresa", "Actividad Remedy"};
        
        for (int i = 0; i < campos.length; i++) {
            if (campos[i] == null || campos[i].trim().isEmpty()) {
                model.addAttribute("errorMessage", "Falta el campo: " + nombres[i]);
                addAllValuesToModel(model, turno, nombreUsuario, fechaInicio, horaInicio, fechaTermino, horaTermino,
                                  nombreTecnico, rutTecnico, empresaDemandante, empresaContratista, cargoTecnico,
                                  salaRemedy, tipoTicket, numeroTicket, aprobador, escolta, motivoIngreso,
                                  guiaDespacho, salaIngresa, rackIngresa, actividadRemedy);
                return "ingresoap";
            }
        }
        
        // Validar formato RUT (misma validación que formulario de usuario)
        String rutLimpio = rutTecnico.replaceAll("\\s+","");
        if (!rutLimpio.matches("^[\\d]{1,2}\\.?[\\d]{3}\\.?[\\d]{3}-?[\\dkK]$")){
            model.addAttribute("errorMessage", "Por favor ingrese un RUT válido (formato: 12.345.678-9)");
            addAllValuesToModel(model, turno, nombreUsuario, fechaInicio, horaInicio, fechaTermino, horaTermino,
                              nombreTecnico, rutTecnico, empresaDemandante, empresaContratista, cargoTecnico,
                              salaRemedy, tipoTicket, numeroTicket, aprobador, escolta, motivoIngreso,
                              guiaDespacho, salaIngresa, rackIngresa, actividadRemedy);
            return "ingresoap";
        }

        try {
            IngresoAP ingreso = new IngresoAP();
            
            ingreso.setTurno(turno.trim());
            ingreso.setNombreUsuario(nombreUsuario.trim());
            ingreso.setFechaInicio(LocalDate.parse(fechaInicio));
            ingreso.setHoraInicio(LocalTime.parse(horaInicio));
            
            if (fechaTermino != null && !fechaTermino.trim().isEmpty()) {
                ingreso.setFechaTermino(LocalDate.parse(fechaTermino));
            }
            if (horaTermino != null && !horaTermino.trim().isEmpty()) {
                ingreso.setHoraTermino(LocalTime.parse(horaTermino));
            }
            
            ingreso.setNombreTecnico(nombreTecnico.trim());
            ingreso.setRutTecnico(rutLimpio);
            ingreso.setCargoTecnico(cargoTecnico.trim());
            ingreso.setEmpresaDemandante(empresaDemandante.trim());
            ingreso.setEmpresaContratista(empresaContratista.trim());
            ingreso.setSalaRemedy(salaRemedy.trim());
            ingreso.setTipoTicket(tipoTicket.trim());
            ingreso.setNumeroTicket(numeroTicket.trim());
            ingreso.setAprobador(aprobador.trim());
            ingreso.setEscolta(escolta.trim());
            ingreso.setMotivoIngreso(motivoIngreso.trim());
            
            if (guiaDespacho != null && !guiaDespacho.trim().isEmpty()) {
                ingreso.setGuiaDespacho(guiaDespacho.trim());
            }
            ingreso.setSalaIngresa(salaIngresa.trim());
            ingreso.setRackIngresa(rackIngresa.trim());
            ingreso.setActividadRemedy(actividadRemedy.trim());

            IngresoAP ingresoGuardado = ingresoAPService.registrarIngreso(ingreso);
            
            model.addAttribute("successMessage", 
                "Ingreso registrado exitosamente. ID: " + ingresoGuardado.getId() + 
                " - Técnico: " + ingresoGuardado.getNombreTecnico() + 
                " - Ticket: " + ingresoGuardado.getNumeroTicket());
            
            return "ingresoap";
        
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al procesar el registro: " + e.getMessage());
            addAllValuesToModel(model, turno, nombreUsuario, fechaInicio, horaInicio, fechaTermino, horaTermino,
                              nombreTecnico, rutTecnico, empresaDemandante, empresaContratista, cargoTecnico,
                              salaRemedy, tipoTicket, numeroTicket, aprobador, escolta, motivoIngreso,
                              guiaDespacho, salaIngresa, rackIngresa, actividadRemedy);
            return "ingresoap";
        }
    }

    @GetMapping("/ap/list")
    public String listarIngresos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            List<IngresoAP> ingresos = ingresoAPService.obtenerTodosLosIngresos();
            model.addAttribute("ingresos", ingresos);
            model.addAttribute("usuario", usuario);
            return "user/ingresoap-list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los ingresos");
            return "dashboard";
        }
    }

    // READ - Ver detalle de un ingreso específico
    @GetMapping("/ap/read/{id}")
    public String verIngreso(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.buscarPorId(id);
            if (ingreso.isPresent()) {
                model.addAttribute("ingreso", ingreso.get());
                model.addAttribute("usuario", usuario);
                return "user/ingresoap-read";
            } else {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
                return "redirect:/ingreso/ap/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el ingreso: " + e.getMessage());
            return "redirect:/ingreso/ap/list";
        }
    }

    // UPDATE - Mostrar formulario de edición
    @GetMapping("/ap/update/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.buscarPorId(id);
            if (ingreso.isPresent()) {
                model.addAttribute("ingreso", ingreso.get());
                model.addAttribute("usuario", usuario);
                return "user/ingresoap-update";
            } else {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
                return "redirect:/ingreso/ap/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el ingreso: " + e.getMessage());
            return "redirect:/ingreso/ap/list";
        }
    }

    // UPDATE - Procesar formulario de edición
    @PostMapping("/ap/update/{id}")
    public String actualizarIngreso(@PathVariable Long id, 
                                  @RequestParam String turno,
                                  @RequestParam String nombreUsuario,
                                  @RequestParam String fechaInicio,
                                  @RequestParam String horaInicio,
                                  @RequestParam String nombreTecnico,
                                  @RequestParam String rutTecnico,
                                  @RequestParam String empresaDemandante,
                                  @RequestParam String empresaContratista,
                                  @RequestParam String cargoTecnico,
                                  @RequestParam String salaRemedy,
                                  @RequestParam String tipoTicket,
                                  @RequestParam String numeroTicket,
                                  @RequestParam String aprobador,
                                  @RequestParam String escolta,
                                  @RequestParam String motivoIngreso,
                                  @RequestParam String guiaDespacho,
                                  @RequestParam String salaIngresa,
                                  @RequestParam String rackIngresa,
                                  @RequestParam String actividadRemedy,
                                  @RequestParam(required = false) String fechaTermino,
                                  @RequestParam(required = false) String horaTermino,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingresoExistente = ingresoAPService.buscarPorId(id);
            if (!ingresoExistente.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
                return "redirect:/ingreso/ap/list";
            }

            // Validaciones
            if (nombreTecnico.trim().isEmpty() || rutTecnico.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Nombre y RUT del técnico son obligatorios");
                return "redirect:/ingreso/ap/update/" + id;
            }

            // Validar formato RUT
            String rutLimpio = rutTecnico.replaceAll("\\s+","");
            if (!rutLimpio.matches("^[\\d]{1,2}\\.?[\\d]{3}\\.?[\\d]{3}-?[\\dkK]$")){
                redirectAttributes.addFlashAttribute("error", "Por favor ingrese un RUT válido (formato: 12.345.678-9)");
                return "redirect:/ingreso/ap/update/" + id;
            }

            // Actualizar el ingreso existente
            IngresoAP ingreso = ingresoExistente.get();
            ingreso.setTurno(turno.trim());
            ingreso.setNombreUsuario(nombreUsuario.trim());
            ingreso.setFechaInicio(LocalDate.parse(fechaInicio));
            ingreso.setHoraInicio(LocalTime.parse(horaInicio));
            ingreso.setNombreTecnico(nombreTecnico.trim());
            ingreso.setRutTecnico(rutLimpio);
            ingreso.setEmpresaDemandante(empresaDemandante.trim());
            ingreso.setEmpresaContratista(empresaContratista.trim());
            ingreso.setCargoTecnico(cargoTecnico.trim());
            ingreso.setSalaRemedy(salaRemedy.trim());
            ingreso.setTipoTicket(tipoTicket.trim());
            ingreso.setNumeroTicket(numeroTicket.trim());
            ingreso.setAprobador(aprobador.trim());
            ingreso.setEscolta(escolta.trim());
            ingreso.setMotivoIngreso(motivoIngreso.trim());
            ingreso.setGuiaDespacho(guiaDespacho.trim());
            ingreso.setSalaIngresa(salaIngresa.trim());
            ingreso.setRackIngresa(rackIngresa.trim());
            ingreso.setActividadRemedy(actividadRemedy.trim());

            // Actualizar fechas de término si se proporcionan
            if (fechaTermino != null && !fechaTermino.trim().isEmpty()) {
                ingreso.setFechaTermino(LocalDate.parse(fechaTermino));
                ingreso.setActivo(false);
            }
            if (horaTermino != null && !horaTermino.trim().isEmpty()) {
                ingreso.setHoraTermino(LocalTime.parse(horaTermino));
            }

            ingresoAPService.guardar(ingreso);
            redirectAttributes.addFlashAttribute("success", "Ingreso actualizado exitosamente");
            return "redirect:/ingreso/ap/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el ingreso: " + e.getMessage());
            return "redirect:/ingreso/ap/update/" + id;
        }
    }

    // DELETE - Mostrar confirmación de eliminación
    @GetMapping("/ap/delete/{id}")
    public String mostrarConfirmacionEliminacion(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.buscarPorId(id);
            if (ingreso.isPresent()) {
                model.addAttribute("ingreso", ingreso.get());
                model.addAttribute("usuario", usuario);
                return "user/ingresoap-delete";
            } else {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
                return "redirect:/ingreso/ap/list";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el ingreso: " + e.getMessage());
            return "redirect:/ingreso/ap/list";
        }
    }

    // DELETE - Eliminar ingreso
    @PostMapping("/ap/delete/{id}")
    public String eliminarIngreso(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.buscarPorId(id);
            if (ingreso.isPresent()) {
                ingresoAPService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Ingreso eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el ingreso: " + e.getMessage());
        }
        return "redirect:/ingreso/ap/list";
    }

    // Buscar técnico por RUT para reutilizar información
    @GetMapping("/ap/buscar-tecnico")
    @ResponseBody
    public Map<String, Object> buscarTecnicoPorRut(@RequestParam String rut, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            response.put("error", "Sesión expirada");
            return response;
        }
        
        try {
            List<IngresoAP> ingresosPrevios = ingresoAPService.buscarPorRutTecnico(rut);
            if (!ingresosPrevios.isEmpty()) {
                // Obtener el ingreso más reciente
                IngresoAP ultimoIngreso = ingresosPrevios.get(0);
                
                response.put("existe", true);
                response.put("nombreTecnico", ultimoIngreso.getNombreTecnico());
                response.put("cargoTecnico", ultimoIngreso.getCargoTecnico());
                response.put("empresaContratista", ultimoIngreso.getEmpresaContratista());
                response.put("empresaDemandante", ultimoIngreso.getEmpresaDemandante());
                response.put("totalIngresos", ingresosPrevios.size());
                response.put("ultimoIngreso", ultimoIngreso.getFechaInicio().toString());
            } else {
                response.put("existe", false);
            }
        } catch (Exception e) {
            response.put("error", "Error al buscar técnico: " + e.getMessage());
        }
        return response;
    }

    private void addAllValuesToModel(Model model, String turno, String nombreUsuario, 
                                     String fechaInicio, String horaInicio, String fechaTermino, String horaTermino,
                                     String nombreTecnico, String rutTecnico, String empresaDemandante, 
                                     String empresaContratista, String cargoTecnico, String salaRemedy,
                                     String tipoTicket, String numeroTicket, String aprobador, String escolta,
                                     String motivoIngreso, String guiaDespacho, String salaIngresa, 
                                     String rackIngresa, String actividadRemedy){
        model.addAttribute("turno", turno);
        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("horaInicio", horaInicio);
        model.addAttribute("fechaTermino", fechaTermino);
        model.addAttribute("horaTermino", horaTermino);
        model.addAttribute("nombreTecnico", nombreTecnico);
        model.addAttribute("rutTecnico", rutTecnico);
        model.addAttribute("empresaDemandante", empresaDemandante);
        model.addAttribute("empresaContratista", empresaContratista);
        model.addAttribute("cargoTecnico", cargoTecnico);
        model.addAttribute("salaRemedy", salaRemedy);
        model.addAttribute("tipoTicket", tipoTicket);
        model.addAttribute("numeroTicket", numeroTicket);
        model.addAttribute("aprobador", aprobador);
        model.addAttribute("escolta", escolta);
        model.addAttribute("motivoIngreso", motivoIngreso);
        model.addAttribute("guiaDespacho", guiaDespacho);
        model.addAttribute("salaIngresa", salaIngresa);
        model.addAttribute("rackIngresa", rackIngresa);
        model.addAttribute("actividadRemedy", actividadRemedy);
    }
}
