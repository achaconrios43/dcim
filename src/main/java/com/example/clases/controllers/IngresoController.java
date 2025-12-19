package com.example.clases.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.clases.dao.IUsuarioDao;
import com.example.clases.entity.IngresoAP;
import com.example.clases.entity.Usuario;
import com.example.clases.service.IngresoAPService;

/**
 * Controlador para gestiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de ingresos al Centro de Datos
 */
@Controller
@RequestMapping("/ingreso")
public class IngresoController {

    @Autowired
    private IngresoAPService ingresoAPService;
    
    @Autowired
    private IUsuarioDao usuarioDao;

    @GetMapping(value = {"/", ""})
    public String redirigirAForm() {
        return "redirect:/ingreso/ap";
    }

    @GetMapping("/ap")
    public String mostrarFormularioIngreso(@RequestParam(name="nombre", required=false) String nombre,
                                          @RequestParam(name="rut", required=false) String rut,
                                          Authentication authentication,
                                          Model model) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
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
                                @RequestParam(name="fechaFinFicticia", required=false) String fechaFinFicticia,
                                @RequestParam(name="horaFinFicticia", required=false) String horaFinFicticia,
                                @RequestParam(name="fechaTermino", required=false) String fechaTermino,
                                @RequestParam(name="horaTermino", required=false) String horaTermino,
                                @RequestParam(name="nombreTecnico", required=false) String nombreTecnico,
                                @RequestParam(name="rutTecnico", required=false) String rutTecnico,
                                @RequestParam(name="empresaDemandante", required=false) String empresaDemandante,
                                @RequestParam(name="empresaContratista", required=false) String empresaContratista,
                                @RequestParam(name="cargoTecnico", required=false) String cargoTecnico,
                                @RequestParam(name="sitioIngreso", required=false) String sitioIngreso,
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
                                Authentication authentication,
                                Model model){        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        
        // Si nombreUsuario estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ vacÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­o, usar el nombre del usuario logueado
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            nombreUsuario = usuario.getNombre();
        }
        
        // Validar campos requeridos
        String[] campos = {turno, nombreUsuario, fechaInicio, horaInicio, fechaFinFicticia, horaFinFicticia, nombreTecnico, rutTecnico,
                          empresaDemandante, empresaContratista, cargoTecnico, sitioIngreso, salaRemedy, tipoTicket,
                          numeroTicket, aprobador, escolta, motivoIngreso, salaIngresa, rackIngresa, actividadRemedy};
        String[] nombres = {"Turno", "Nombre Usuario", "Fecha Inicio", "Hora Inicio", "Fecha Final Estimada", "Hora Final Estimada", "Nombre TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico", "RUT TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico",
                           "Empresa Demandante", "Empresa Contratista", "Cargo TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico", "Sitio de Ingreso", "Tipo de Sala REMEDY", "Tipo Ticket",
                           "NÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Âºmero Ticket", "Aprobador", "Escolta", "Motivo Ingreso", "Sala Ingresa", "Rack Ingresa", "Actividad Remedy"};
        
        for (int i = 0; i < campos.length; i++) {
            if (campos[i] == null || campos[i].trim().isEmpty()) {
                model.addAttribute("errorMessage", "Falta el campo: " + nombres[i]);
                addAllValuesToModel(model, turno, nombreUsuario, fechaInicio, horaInicio, fechaTermino, horaTermino,
                                  nombreTecnico, rutTecnico, empresaDemandante, empresaContratista, cargoTecnico, sitioIngreso,
                                  salaRemedy, tipoTicket, numeroTicket, aprobador, escolta, motivoIngreso,
                                  guiaDespacho, salaIngresa, rackIngresa, actividadRemedy);
                return "ingresoap";
            }
        }
        
        // Validar formato RUT (misma validaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n que formulario de usuario)
        String rutLimpio = rutTecnico.replaceAll("\\s+","");
        if (!rutLimpio.matches("^[\\d]{1,2}\\.?[\\d]{3}\\.?[\\d]{3}-?[\\dkK]$")){
            model.addAttribute("errorMessage", "Por favor ingrese un RUT vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido (formato: 12.345.678-9)");
            addAllValuesToModel(model, turno, nombreUsuario, fechaInicio, horaInicio, fechaTermino, horaTermino,
                              nombreTecnico, rutTecnico, empresaDemandante, empresaContratista, cargoTecnico, sitioIngreso,
                              salaRemedy, tipoTicket, numeroTicket, aprobador, escolta, motivoIngreso,
                              guiaDespacho, salaIngresa, rackIngresa, actividadRemedy);
            return "ingresoap";
        }
        
        // VALIDACIÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œN CRÃƒÆ’Ã†â€™Ãƒâ€šÃ‚ÂTICA: Verificar si el tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico ya tiene un ingreso activo
        if (ingresoAPService.tieneIngresoActivo(rutLimpio)) {
            Optional<IngresoAP> ingresoActivo = ingresoAPService.obtenerIngresoActivoPorRut(rutLimpio);
            if (ingresoActivo.isPresent()) {
                IngresoAP ingreso = ingresoActivo.get();
                model.addAttribute("errorMessage", 
                    "ATENCIÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œN: El tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico " + ingreso.getNombreTecnico() + " (RUT: " + ingreso.getRutTecnico() + 
                    ") ya tiene un ingreso activo desde el " + ingreso.getFechaInicio() + " a las " + ingreso.getHoraInicio() + 
                    ". Debe cerrar el ingreso anterior para poder volver a ingresar al tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico. " +
                    "Vaya a la secciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de 'Actualizar Ingreso' para registrar la salida.");
            } else {
                model.addAttribute("errorMessage", 
                    "ATENCIÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œN: El tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico con RUT " + rutLimpio + " ya tiene un ingreso activo. " +
                    "Debe cerrar el ingreso anterior para poder volver a ingresar al tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico.");
            }
            addAllValuesToModel(model, turno, nombreUsuario, fechaInicio, horaInicio, fechaTermino, horaTermino,
                              nombreTecnico, rutTecnico, empresaDemandante, empresaContratista, cargoTecnico, sitioIngreso,
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
            
            // Procesar fecha y hora final ficticia (estimada)
            ingreso.setFechaFinFicticia(LocalDate.parse(fechaFinFicticia));
            ingreso.setHoraFinFicticia(LocalTime.parse(horaFinFicticia));
            
            // Calcular hora media de supervisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n
            LocalDateTime inicioDateTime = LocalDateTime.of(ingreso.getFechaInicio(), ingreso.getHoraInicio());
            LocalDateTime finFicticioDateTime = LocalDateTime.of(ingreso.getFechaFinFicticia(), ingreso.getHoraFinFicticia());
            
            long minutosTotal = java.time.Duration.between(inicioDateTime, finFicticioDateTime).toMinutes();
            LocalDateTime supervisionMediaDateTime = inicioDateTime.plusMinutes(minutosTotal / 2);
            
            ingreso.setFechaSupervisionMedia(supervisionMediaDateTime.toLocalDate());
            ingreso.setHoraSupervisionMedia(supervisionMediaDateTime.toLocalTime());
            ingreso.setSegundaSupervisionRealizada(false);
            
            // Manejo de fecha/hora de tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino y auto-inactivaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n
            if (fechaTermino != null && !fechaTermino.trim().isEmpty()) {
                ingreso.setFechaTermino(LocalDate.parse(fechaTermino));
                // Auto-inactivar cuando se registra fecha de tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino
                ingreso.setActivo(false);
            }
            if (horaTermino != null && !horaTermino.trim().isEmpty()) {
                ingreso.setHoraTermino(LocalTime.parse(horaTermino));
                // Auto-inactivar cuando se registra hora de tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino
                ingreso.setActivo(false);
            }
            
            // Si no se especifica fecha/hora de tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino, el registro permanece activo
            if (ingreso.getFechaTermino() == null && ingreso.getHoraTermino() == null) {
                ingreso.setActivo(true);
            }
            
            ingreso.setNombreTecnico(nombreTecnico.trim());
            ingreso.setRutTecnico(rutLimpio);
            ingreso.setCargoTecnico(cargoTecnico.trim());
            ingreso.setEmpresaDemandante(empresaDemandante.trim());
            ingreso.setEmpresaContratista(empresaContratista.trim());
            ingreso.setSitioIngreso(sitioIngreso.trim());
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
                " - TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico: " + ingresoGuardado.getNombreTecnico() + 
                " - Ticket: " + ingresoGuardado.getNumeroTicket());
            
            return "ingresoap";
        
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al procesar el registro: " + e.getMessage());
            addAllValuesToModel(model, turno, nombreUsuario, fechaInicio, horaInicio, fechaTermino, horaTermino,
                              nombreTecnico, rutTecnico, empresaDemandante, empresaContratista, cargoTecnico, sitioIngreso,
                              salaRemedy, tipoTicket, numeroTicket, aprobador, escolta, motivoIngreso,
                              guiaDespacho, salaIngresa, rackIngresa, actividadRemedy);
            return "ingresoap";
        }
    }

    @GetMapping("/ap/list")
    public String listarIngresos(@RequestParam(required = false) String fechaInicio,
                                 @RequestParam(required = false) String fechaFin,
                                 @RequestParam(required = false, defaultValue = "false") boolean soloActivos,
                                 Authentication authentication, Model model) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            List<IngresoAP> ingresos;
            
            // Si se especifica filtro de solo activos
            if (soloActivos) {
                ingresos = ingresoAPService.obtenerIngresosActivosOrdenados();
                model.addAttribute("filtroAplicado", "Mostrando solo ingresos ACTIVOS");
            }
            // Si se especifican fechas, filtrar por rango
            else if (fechaInicio != null && !fechaInicio.trim().isEmpty() && 
                     fechaFin != null && !fechaFin.trim().isEmpty()) {
                try {
                    LocalDate inicio = LocalDate.parse(fechaInicio);
                    LocalDate fin = LocalDate.parse(fechaFin);
                    ingresos = ingresoAPService.obtenerIngresosPorRangoOrdenados(inicio, fin);
                    model.addAttribute("filtroAplicado", "Ingresos del " + inicio + " al " + fin);
                } catch (Exception e) {
                    ingresos = ingresoAPService.obtenerIngresosOrdenadosPorFecha();
                    model.addAttribute("errorFechas", "Formato de fecha invÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido. Mostrando todos los ingresos.");
                }
            }
            // Sin filtros, mostrar todos ordenados
            else {
                ingresos = ingresoAPService.obtenerIngresosOrdenadosPorFecha();
            }
            
            model.addAttribute("ingresos", ingresos);
            model.addAttribute("usuario", usuario);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("soloActivos", soloActivos);
            model.addAttribute("totalIngresos", ingresos.size());
            
            return "user/ingresoap-list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los ingresos: " + e.getMessage());
            return "dashboard";
        }
    }

    // READ - Ver detalle de un ingreso especÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­fico
    @GetMapping("/ap/read/{id}")
    public String verIngreso(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
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

    // UPDATE - Mostrar formulario de ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n
    @GetMapping("/ap/update/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
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

    // UPDATE - Procesar formulario de ediciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n
    @PostMapping("/ap/update/{id}")
    public String actualizarIngreso(@PathVariable Long id, 
                                  @ModelAttribute IngresoAP ingresoForm,
                                  BindingResult bindingResult,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        System.out.println("=== DEBUG: Actualizando ingreso ID: " + id + " ===");
        System.out.println("Turno: " + ingresoForm.getTurno());
        System.out.println("Nombre TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico: " + ingresoForm.getNombreTecnico());
        System.out.println("RUT TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico: " + ingresoForm.getRutTecnico());
        System.out.println("Fecha TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino: " + ingresoForm.getFechaTermino());
        System.out.println("Hora TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino: " + ingresoForm.getHoraTermino());
        
        try {
            Optional<IngresoAP> ingresoExistente = ingresoAPService.buscarPorId(id);
            if (!ingresoExistente.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
                return "redirect:/ingreso/ap/list";
            }

            // Validaciones
            if (ingresoForm.getNombreTecnico() == null || ingresoForm.getNombreTecnico().trim().isEmpty() || 
                ingresoForm.getRutTecnico() == null || ingresoForm.getRutTecnico().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Nombre y RUT del tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico son obligatorios");
                return "redirect:/ingreso/ap/update/" + id;
            }

            // Validar formato RUT
            String rutLimpio = ingresoForm.getRutTecnico().replaceAll("\\s+","");
            if (!rutLimpio.matches("^[\\d]{1,2}\\.?[\\d]{3}\\.?[\\d]{3}-?[\\dkK]$")){
                redirectAttributes.addFlashAttribute("error", "Por favor ingrese un RUT vÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡lido (formato: 12.345.678-9)");
                return "redirect:/ingreso/ap/update/" + id;
            }

            // Actualizar el ingreso existente manteniendo la fecha/hora original de ingreso
            IngresoAP ingreso = ingresoExistente.get();
            ingreso.setTurno(ingresoForm.getTurno() != null ? ingresoForm.getTurno().trim() : ingreso.getTurno());
            ingreso.setNombreUsuario(ingresoForm.getNombreUsuario() != null ? ingresoForm.getNombreUsuario().trim() : ingreso.getNombreUsuario());
            // MANTENER fecha y hora de ingreso originales - NO las actualizamos desde el form
            // ingreso.setFechaInicio() y ingreso.setHoraInicio() permanecen sin cambios
            ingreso.setNombreTecnico(ingresoForm.getNombreTecnico().trim());
            ingreso.setRutTecnico(rutLimpio);
            ingreso.setEmpresaDemandante(ingresoForm.getEmpresaDemandante() != null ? ingresoForm.getEmpresaDemandante().trim() : ingreso.getEmpresaDemandante());
            ingreso.setEmpresaContratista(ingresoForm.getEmpresaContratista() != null ? ingresoForm.getEmpresaContratista().trim() : ingreso.getEmpresaContratista());
            ingreso.setCargoTecnico(ingresoForm.getCargoTecnico() != null ? ingresoForm.getCargoTecnico().trim() : ingreso.getCargoTecnico());
            ingreso.setSalaRemedy(ingresoForm.getSalaRemedy() != null ? ingresoForm.getSalaRemedy().trim() : ingreso.getSalaRemedy());
            ingreso.setTipoTicket(ingresoForm.getTipoTicket() != null ? ingresoForm.getTipoTicket().trim() : ingreso.getTipoTicket());
            ingreso.setNumeroTicket(ingresoForm.getNumeroTicket() != null ? ingresoForm.getNumeroTicket().trim() : ingreso.getNumeroTicket());
            ingreso.setAprobador(ingresoForm.getAprobador() != null ? ingresoForm.getAprobador().trim() : ingreso.getAprobador());
            ingreso.setEscolta(ingresoForm.getEscolta() != null ? ingresoForm.getEscolta().trim() : ingreso.getEscolta());
            ingreso.setMotivoIngreso(ingresoForm.getMotivoIngreso() != null ? ingresoForm.getMotivoIngreso().trim() : ingreso.getMotivoIngreso());
            ingreso.setGuiaDespacho(ingresoForm.getGuiaDespacho() != null ? ingresoForm.getGuiaDespacho().trim() : "");
            ingreso.setSalaIngresa(ingresoForm.getSalaIngresa() != null ? ingresoForm.getSalaIngresa().trim() : ingreso.getSalaIngresa());
            ingreso.setRackIngresa(ingresoForm.getRackIngresa() != null ? ingresoForm.getRackIngresa().trim() : ingreso.getRackIngresa());
            ingreso.setActividadRemedy(ingresoForm.getActividadRemedy() != null ? ingresoForm.getActividadRemedy().trim() : ingreso.getActividadRemedy());

            // LÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³gica de salida: cuando se completan fecha Y hora de tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino, el tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico sale
            if (ingresoForm.getFechaTermino() != null) {
                ingreso.setFechaTermino(ingresoForm.getFechaTermino());
            }
            
            if (ingresoForm.getHoraTermino() != null) {
                ingreso.setHoraTermino(ingresoForm.getHoraTermino());
            }
            
            // Auto-inactivaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n: solo cuando AMBOS campos de tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©rmino estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡n completos
            if (ingreso.getFechaTermino() != null && ingreso.getHoraTermino() != null) {
                ingreso.setActivo(false); // TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico ha salido
            } else {
                ingreso.setActivo(true); // TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico sigue activo
            }

            ingresoAPService.guardar(ingreso);
            System.out.println("=== DEBUG: Ingreso guardado exitosamente ===");
            redirectAttributes.addFlashAttribute("success", "Ingreso actualizado exitosamente");
            return "redirect:/ingreso/ap/read/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el ingreso: " + e.getMessage());
            return "redirect:/ingreso/ap/update/" + id;
        }
    }

    // ACCIÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œN - Marcar segunda supervisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n realizada
    @PostMapping("/ap/marcar-segunda-supervision/{id}")
    public String marcarSegundaSupervision(@PathVariable Long id, 
                                          Authentication authentication, 
                                          RedirectAttributes redirectAttributes) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingresoOpt = ingresoAPService.buscarPorId(id);
            if (!ingresoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
                return "redirect:/ingreso/ap/list";
            }
            
            IngresoAP ingreso = ingresoOpt.get();
            
            // Verificar que el ingreso estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â© activo
            if (!ingreso.getActivo()) {
                redirectAttributes.addFlashAttribute("error", "No se puede marcar supervisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n en un registro cerrado");
                return "redirect:/ingreso/ap/read/" + id;
            }
            
            // Marcar como realizada
            ingreso.setSegundaSupervisionRealizada(true);
            ingreso.setFechaSegundaSupervision(LocalDate.now());
            ingreso.setHoraSegundaSupervision(LocalTime.now());
            
            ingresoAPService.guardar(ingreso);
            
            redirectAttributes.addFlashAttribute("success", 
                "ÃƒÆ’Ã‚Â¢Ãƒâ€¦Ã¢â‚¬Å“ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â¦ Segunda supervisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n marcada como realizada exitosamente a las " + 
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            return "redirect:/ingreso/ap/read/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al marcar supervisiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n: " + e.getMessage());
            return "redirect:/ingreso/ap/read/" + id;
        }
    }
    
    // ACCIÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œN - Cerrar registro (finalizar ingreso)
    @PostMapping("/ap/cerrar/{id}")
    public String cerrarIngreso(@PathVariable Long id, 
                               Authentication authentication, 
                               RedirectAttributes redirectAttributes) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingresoOpt = ingresoAPService.buscarPorId(id);
            if (!ingresoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ingreso no encontrado");
                return "redirect:/ingreso/ap/list";
            }
            
            IngresoAP ingreso = ingresoOpt.get();
            
            // Verificar que el ingreso estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â© activo
            if (!ingreso.getActivo()) {
                redirectAttributes.addFlashAttribute("error", "Este registro ya estÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡ cerrado");
                return "redirect:/ingreso/ap/read/" + id;
            }
            
            // Cerrar el ingreso con fecha y hora actual
            ingreso.setFechaTermino(LocalDate.now());
            ingreso.setHoraTermino(LocalTime.now());
            ingreso.setActivo(false);
            
            ingresoAPService.guardar(ingreso);
            
            redirectAttributes.addFlashAttribute("success", 
                "ÃƒÆ’Ã‚Â°Ãƒâ€¦Ã‚Â¸ÃƒÂ¢Ã¢â€šÂ¬Ã‚ÂÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ Registro cerrado exitosamente. TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico " + ingreso.getNombreTecnico() + 
                " marcado como inactivo a las " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            return "redirect:/ingreso/ap/read/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cerrar el ingreso: " + e.getMessage());
            return "redirect:/ingreso/ap/read/" + id;
        }
    }

    // DELETE - Mostrar confirmaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n de eliminaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n
    @GetMapping("/ap/delete/{id}")
    public String mostrarConfirmacionEliminacion(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // TODOS los usuarios autenticados pueden eliminar registros (para corregir errores)
        // No hay restricciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n por rol - tanto ADMIN como USER pueden eliminar
        
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.buscarPorId(id);
            if (ingreso.isPresent()) {
                model.addAttribute("ingreso", ingreso.get());
                model.addAttribute("usuario", usuario);
                model.addAttribute("puedeEliminar", true); // Todos los usuarios pueden eliminar
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
    public String eliminarIngreso(@PathVariable Long id, 
                                @RequestParam(required = false) String razonEliminacion,
                                Authentication authentication, 
                                RedirectAttributes redirectAttributes) {        
        // Verificar autenticaciÃƒÆ’Ã‚Â¯Ãƒâ€šÃ‚Â¿Ãƒâ€šÃ‚Â½n con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.buscarPorId(id);
            if (ingreso.isPresent()) {
                IngresoAP registroEliminado = ingreso.get();
                
                // Logging para auditorÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â­a
                System.out.printf("[AUDIT] ELIMINACIÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…â€œN - Usuario: %s | Registro ID: %d | TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico: %s (%s) | Fecha: %s | RazÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n: %s%n",
                    usuario.getNombre(),
                    registroEliminado.getId(),
                    registroEliminado.getNombreTecnico(),
                    registroEliminado.getRutTecnico(),
                    registroEliminado.getFechaInicio(),
                    razonEliminacion != null ? razonEliminacion : "No especificada"
                );
                
                ingresoAPService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", 
                    "Registro eliminado exitosamente. TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico: " + registroEliminado.getNombreTecnico() + 
                    " | ID: " + registroEliminado.getId());
            } else {
                redirectAttributes.addFlashAttribute("error", "Registro no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el registro: " + e.getMessage());
        }
        return "redirect:/ingreso/ap/list";
    }

    // Buscar tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico por RUT para reutilizar informaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n
    @GetMapping("/ap/buscar-tecnico")
    @ResponseBody
    public Map<String, Object> buscarTecnicoPorRut(@RequestParam String rut, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("error", "SesiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n expirada");
            return response;
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            response.put("error", "SesiÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n expirada");
            return response;
        }
        
        try {
            List<IngresoAP> ingresosPrevios = ingresoAPService.buscarPorRutTecnico(rut);
            if (!ingresosPrevios.isEmpty()) {
                // Obtener el ingreso mÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¡s reciente
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
            response.put("error", "Error al buscar tÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico: " + e.getMessage());
        }
        return response;
    }
    
    // Exportar ingresos a Excel
    @GetMapping("/ap/export")
    public ResponseEntity<byte[]> exportarIngresos(@RequestParam(required = false) String fechaInicio,
                                                  @RequestParam(required = false) String fechaFin,
                                                  @RequestParam(required = false, defaultValue = "false") boolean soloActivos,
                                                  Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        String email = authentication.getName();
        Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            List<IngresoAP> ingresos;
            String nombreArchivo;
            
            // Aplicar los mismos filtros que en la lista
            if (soloActivos) {
                ingresos = ingresoAPService.obtenerIngresosActivosOrdenados();
                nombreArchivo = "Ingresos_Activos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if (fechaInicio != null && !fechaInicio.trim().isEmpty() && 
                       fechaFin != null && !fechaFin.trim().isEmpty()) {
                LocalDate inicio = LocalDate.parse(fechaInicio);
                LocalDate fin = LocalDate.parse(fechaFin);
                ingresos = ingresoAPService.obtenerIngresosPorRangoOrdenados(inicio, fin);
                nombreArchivo = "Ingresos_" + fechaInicio + "_a_" + fechaFin;
            } else {
                ingresos = ingresoAPService.obtenerIngresosOrdenadosPorFecha();
                nombreArchivo = "Todos_los_Ingresos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            // Crear el archivo Excel
            byte[] excelContent = generarExcel(ingresos, usuario);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", nombreArchivo + ".xlsx");
            headers.setContentLength(excelContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelContent);
                    
        } catch (Exception e) {
            System.err.println("Error al exportar ingresos: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Genera un archivo Excel con los ingresos
     */
    private byte[] generarExcel(List<IngresoAP> ingresos, Usuario usuario) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Registro de Ingresos");
            
            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Estilo para datos
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            
            // Estilo para fechas
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "ID", "Fecha Ingreso", "Hora Ingreso", "Fecha Salida", "Hora Salida",
                "TÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â©cnico", "RUT", "Empresa Demandante", "Empresa Contratista",
                "Cargo", "Ticket", "Tipo Ticket", "Sala", "Motivo", "Estado",
                "Turno", "Aprobador", "Escolta", "Actividad"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (IngresoAP ingreso : ingresos) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(ingreso.getId());
                row.createCell(1).setCellValue(ingreso.getFechaInicio() != null ? ingreso.getFechaInicio().toString() : "");
                row.createCell(2).setCellValue(ingreso.getHoraInicio() != null ? ingreso.getHoraInicio().toString() : "");
                row.createCell(3).setCellValue(ingreso.getFechaTermino() != null ? ingreso.getFechaTermino().toString() : "");
                row.createCell(4).setCellValue(ingreso.getHoraTermino() != null ? ingreso.getHoraTermino().toString() : "");
                row.createCell(5).setCellValue(ingreso.getNombreTecnico() != null ? ingreso.getNombreTecnico() : "");
                row.createCell(6).setCellValue(ingreso.getRutTecnico() != null ? ingreso.getRutTecnico() : "");
                row.createCell(7).setCellValue(ingreso.getEmpresaDemandante() != null ? ingreso.getEmpresaDemandante() : "");
                row.createCell(8).setCellValue(ingreso.getEmpresaContratista() != null ? ingreso.getEmpresaContratista() : "");
                row.createCell(9).setCellValue(ingreso.getCargoTecnico() != null ? ingreso.getCargoTecnico() : "");
                row.createCell(10).setCellValue(ingreso.getNumeroTicket() != null ? ingreso.getNumeroTicket() : "");
                row.createCell(11).setCellValue(ingreso.getTipoTicket() != null ? ingreso.getTipoTicket() : "");
                row.createCell(12).setCellValue(ingreso.getSalaIngresa() != null ? ingreso.getSalaIngresa() : "");
                row.createCell(13).setCellValue(ingreso.getMotivoIngreso() != null ? ingreso.getMotivoIngreso() : "");
                row.createCell(14).setCellValue(ingreso.getActivo() != null && ingreso.getActivo() ? "ACTIVO" : "INACTIVO");
                row.createCell(15).setCellValue(ingreso.getTurno() != null ? ingreso.getTurno() : "");
                row.createCell(16).setCellValue(ingreso.getAprobador() != null ? ingreso.getAprobador() : "");
                row.createCell(17).setCellValue(ingreso.getEscolta() != null ? ingreso.getEscolta() : "");
                row.createCell(18).setCellValue(ingreso.getActividadRemedy() != null ? ingreso.getActividadRemedy() : "");
                
                // Aplicar estilos
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }
            
            // Agregar informaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n del reporte
            Row infoRow1 = sheet.createRow(rowNum + 2);
            infoRow1.createCell(0).setCellValue("Reporte generado por:");
            infoRow1.createCell(1).setCellValue(usuario.getNombre() + " " + usuario.getApellido());
            
            Row infoRow2 = sheet.createRow(rowNum + 3);
            infoRow2.createCell(0).setCellValue("Fecha de generaciÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â³n:");
            infoRow2.createCell(1).setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                              " " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            
            Row infoRow3 = sheet.createRow(rowNum + 4);
            infoRow3.createCell(0).setCellValue("Total de registros:");
            infoRow3.createCell(1).setCellValue(ingresos.size());
            
            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void addAllValuesToModel(Model model, String turno, String nombreUsuario, 
                                     String fechaInicio, String horaInicio, String fechaTermino, String horaTermino,
                                     String nombreTecnico, String rutTecnico, String empresaDemandante, 
                                     String empresaContratista, String cargoTecnico, String sitioIngreso, String salaRemedy,
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
        model.addAttribute("sitioIngreso", sitioIngreso);
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
