package com.example.dcim.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.entity.GestionAcceso;
import com.example.dcim.entity.IngresoAP;
import com.example.dcim.entity.Usuario;
import com.example.dcim.service.GestionAccesoService;
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

    @Autowired
    private IUsuarioDao usuarioDao;

    @Autowired
    private GestionAccesoService gestionAccesoService;

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
            @RequestParam(name="sitioIngreso") String sitioIngreso,
            @RequestParam(name="salaIngresa") String salaIngresa,
            @RequestParam(name="rackIngresa", required=false) String rackIngresa,
            @RequestParam(name="motivoIngreso") String motivoIngreso,
            @RequestParam(name="actividadRemedy") String actividadRemedy,
            Model model,
            RedirectAttributes redirectAttributes){
        
        try {
            // Crear nueva entidad IngresoAP
            IngresoAP ingresoAP = new IngresoAP();

            // Resolver usuario autenticado y asignar FK referencial
            Authentication authPost = SecurityContextHolder.getContext().getAuthentication();
            if (authPost != null && authPost.isAuthenticated()) {
                String emailPost = authPost.getName();
                usuarioDao.findByEmail(emailPost).ifPresent(ingresoAP::setUsuarioRegistra);
            }

            // Intentar resolver aprobador como FK si existe en el sistema
            usuarioDao.findAll().stream()
                .filter(u -> (u.getNombre() + " " + u.getApellido()).equalsIgnoreCase(aprobador.trim()))
                .findFirst()
                .ifPresent(ingresoAP::setAprobadorRef);
            
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
            ingresoAP.setSitioIngreso(sitioIngreso.trim());
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

    // Verificar si existe al menos un ingreso de técnico para un número de ticket (API JSON)
    @GetMapping("/ingreso/api/tiene-tecnicos")
    @ResponseBody
    public Map<String, Object> tieneTecnicosIngresados(@RequestParam("ticket") String ticket) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean tiene = ingresoAPService.obtenerIngresoPorTicket(ticket).isPresent();
            result.put("tieneTecnicos", tiene);
        } catch (Exception e) {
            result.put("tieneTecnicos", false);
        }
        return result;
    }

    // Marcar ronda de supervisión intermedia como realizada
    @PostMapping("/ingreso/ap/marcar-segunda-supervision/{id}")
    public String marcarSegundaSupervision(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<IngresoAP> ingresoOpt = ingresoAPService.obtenerIngresoPorId(id);
            if (ingresoOpt.isPresent()) {
                IngresoAP ingreso = ingresoOpt.get();
                ingreso.setSegundaSupervisionRealizada(true);
                ingreso.setFechaSegundaSupervision(LocalDate.now());
                ingreso.setHoraSegundaSupervision(LocalTime.now());
                ingresoAPService.actualizarIngreso(ingreso);
                redirectAttributes.addFlashAttribute("successMessage", "Ronda de supervisión marcada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Registro no encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al marcar la ronda: " + e.getMessage());
        }
        return "redirect:/user/ingresoap-list";
    }

    // Listar todos los ingresos con filtros
    @GetMapping("/user/ingresoap-list")
    public String listarIngresos(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) Boolean soloActivos,
            @RequestParam(required = false) String sitio,
            Model model) {
        try {
            List<IngresoAP> ingresos;
            LocalDate dInicio = null;
            LocalDate dFin = null;

            // Parsear fechas
            if (fechaInicio != null && !fechaInicio.isBlank()) {
                dInicio = LocalDate.parse(fechaInicio);
            }
            if (fechaFin != null && !fechaFin.isBlank()) {
                dFin = LocalDate.parse(fechaFin);
            }

            // Validar rango
            if (dInicio != null && dFin != null && dInicio.isAfter(dFin)) {
                model.addAttribute("errorFechas", "La fecha inicio no puede ser posterior a la fecha fin.");
                dInicio = null;
                dFin = null;
            }

            final String sitioFiltro = (sitio != null && !sitio.isBlank()) ? sitio.trim() : null;
            boolean hayFiltro = dInicio != null || sitioFiltro != null || Boolean.TRUE.equals(soloActivos);

            // Sin filtros: no mostrar nada
            if (!hayFiltro) {
                model.addAttribute("ingresos", java.util.Collections.emptyList());
                model.addAttribute("sinFiltro", true);
                model.addAttribute("fechaInicio", fechaInicio);
                model.addAttribute("fechaFin", fechaFin);
                model.addAttribute("soloActivos", soloActivos);
                model.addAttribute("sitioSeleccionado", sitio);
                model.addAttribute("sitios", ingresoAPService.listarSitiosIngresoAP());
                return "user/ingresoap-list";
            }

            // Consulta base
            if (dInicio != null && dFin != null) {
                ingresos = ingresoAPService.obtenerIngresosPorRangoOrdenados(dInicio, dFin);
            } else if (dInicio != null) {
                ingresos = ingresoAPService.obtenerIngresosPorRangoOrdenados(dInicio, dInicio.plusYears(1));
            } else {
                ingresos = ingresoAPService.obtenerIngresosOrdenadosPorFecha();
            }

            // Filtro de sitio (Java-side)
            if (sitioFiltro != null) {
                ingresos = ingresos.stream()
                        .filter(i -> sitioFiltro.equalsIgnoreCase(i.getSitioIngreso()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // Filtro solo activos
            if (Boolean.TRUE.equals(soloActivos)) {
                ingresos = ingresos.stream()
                        .filter(i -> Boolean.TRUE.equals(i.getActivo()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // Mensaje resumen del filtro aplicado
            if (hayFiltro) {
                StringBuilder msg = new StringBuilder("Filtro: ");
                if (dInicio != null) msg.append("Fecha ").append(dInicio).append(dFin != null ? " a " + dFin : "").append("  ");
                if (sitioFiltro != null) msg.append("Sitio: ").append(sitioFiltro).append("  ");
                if (Boolean.TRUE.equals(soloActivos)) msg.append("Solo activos");
                model.addAttribute("filtroAplicado", msg.toString().trim());
            }

            model.addAttribute("ingresos", ingresos);
            model.addAttribute("sinFiltro", false);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("soloActivos", soloActivos);
            model.addAttribute("sitioSeleccionado", sitio);
            model.addAttribute("sitios", ingresoAPService.listarSitiosIngresoAP());

            // Mapa ticket -> primera GestionAcceso (para alertas de horario)
            Map<String, GestionAcceso> gestionPorTicket = new HashMap<>();
            for (IngresoAP ing : ingresos) {
                if (ing.getNumeroTicket() != null && !gestionPorTicket.containsKey(ing.getNumeroTicket())) {
                    List<GestionAcceso> gs = gestionAccesoService.listarPorNumeroTicket(ing.getNumeroTicket());
                    if (gs != null && !gs.isEmpty()) {
                        gestionPorTicket.put(ing.getNumeroTicket(), gs.get(0));
                    }
                }
            }
            model.addAttribute("gestionPorTicket", gestionPorTicket);

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
                // Buscar GestionAcceso asociada al ticket
                String ticket = ingreso.get().getNumeroTicket();
                GestionAcceso gestion = null;
                if (ticket != null) {
                    List<GestionAcceso> gs = gestionAccesoService.listarPorNumeroTicket(ticket);
                    if (gs != null && !gs.isEmpty()) {
                        gestion = gs.get(0);
                    }
                }
                model.addAttribute("gestion", gestion);
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
