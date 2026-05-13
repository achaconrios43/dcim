package com.example.dcim.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.entity.IngresoAP;
import com.example.dcim.entity.MedicionTemperatura;
import com.example.dcim.entity.Usuario;
import com.example.dcim.service.GestionAccesoService;
import com.example.dcim.service.IngresoAPService;
import com.example.dcim.service.TemperaturaService;

/**
 * Controlador para el Dashboard de Cliente - Vista Diaria
 * Muestra estadísticas del día actual con cantidades específicas
 * ACCESO RESTRINGIDO: Solo usuarios con rol ADMIN
 */
@Controller
@RequestMapping("/dashboard/cliente/diario")
public class DashboardClienteDiarioController {
    
    @Autowired
    private IngresoAPService ingresoAPService;
    
    @Autowired
    private GestionAccesoService gestionAccesoService;
    
    @Autowired
    private IUsuarioDao usuarioDao;

    @Autowired
    private TemperaturaService temperaturaService;
    
    @GetMapping
    public String mostrarDashboardDiario(@RequestParam(required = false) String sitio, Authentication authentication, Model model) {
        // Verificar autenticación con Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        // Obtener usuario autenticado desde la base de datos
        String email = authentication.getName();
        Usuario usuarioLogueado = usuarioDao.findByEmail(email).orElse(null);
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        
        // Permitir acceso a USER y ADMIN
        // Ambos roles pueden ver el dashboard diario
        
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        LocalDate hoy = LocalDate.now();

        // Dropdown pobla desde los sitios reales de IngresoAP
        model.addAttribute("sitiosDisponibles", ingresoAPService.listarSitiosIngresoAP());
        
        model.addAttribute("sitioSeleccionado", sitio);
        model.addAttribute("fechaActual", hoy);
        model.addAttribute("diaSemana", hoy.getDayOfWeek().toString());
        
        boolean filtrarPorSitio = sitio != null && !sitio.trim().isEmpty() && !sitio.equals("TODOS");

        // === ESTADÍSTICAS DE INGRESOS TÉCNICOS DEL DÍA ===
        Long ingresosHoy = filtrarPorSitio
                ? ingresoAPService.contarIngresosPorSitio(sitio, hoy, hoy)
                : ingresoAPService.contarIngresosPorFecha(hoy);

        Long ticketsUnicosHoy = filtrarPorSitio
                ? ingresoAPService.contarTicketsUnicosPorSitio(sitio, hoy, hoy)
                : ingresoAPService.contarTicketsUnicos(hoy, hoy);

        // Tipos de tickets del día
        Long cantidadCRQHoy = filtrarPorSitio
                ? ingresoAPService.contarTicketsUnicosPorTipoYSitio("CRQ", sitio, hoy, hoy)
                : ingresoAPService.contarTicketsUnicosPorTipo("CRQ", hoy, hoy);
        Long cantidadINCHoy = filtrarPorSitio
                ? ingresoAPService.contarTicketsUnicosPorTipoYSitio("INC", sitio, hoy, hoy)
                : ingresoAPService.contarTicketsUnicosPorTipo("INC", hoy, hoy);

        Long cantidadVisitaInspectivaHoy = filtrarPorSitio
                ? ingresoAPService.contarTicketsUnicosPorTipoYSitio("Visita Inspectiva", sitio, hoy, hoy)
                : ingresoAPService.contarTicketsUnicosPorTipo("Visita Inspectiva", hoy, hoy);
        Long cantidadRondaRutinariaHoy = filtrarPorSitio
                ? ingresoAPService.contarTicketsUnicosPorTipoYSitio("Ronda Rutinaria", sitio, hoy, hoy)
                : ingresoAPService.contarTicketsUnicosPorTipo("Ronda Rutinaria", hoy, hoy);
        Long cantidadInspeccionHoy = (cantidadVisitaInspectivaHoy != null ? cantidadVisitaInspectivaHoy : 0L) +
                                      (cantidadRondaRutinariaHoy != null ? cantidadRondaRutinariaHoy : 0L);

        // Salas del día
        Long salasTIHoy = filtrarPorSitio
                ? ingresoAPService.contarPorSalaRemedyYSitio("Salas TI", sitio, hoy, hoy)
                : ingresoAPService.contarPorSalaRemedy("Salas TI", hoy, hoy);
        Long salasREDHoy = filtrarPorSitio
                ? ingresoAPService.contarPorSalaRemedyYSitio("Salas de RED", sitio, hoy, hoy)
                : ingresoAPService.contarPorSalaRemedy("Salas de RED", hoy, hoy);
        Long salasTIyREDHoy = filtrarPorSitio
                ? ingresoAPService.contarPorSalaRemedyYSitio("Salas TI & RED", sitio, hoy, hoy)
                : ingresoAPService.contarPorSalaRemedy("Salas TI & RED", hoy, hoy);

        Long salasTITotalHoy = (salasTIHoy != null ? salasTIHoy : 0L) + (salasTIyREDHoy != null ? salasTIyREDHoy : 0L);
        Long salasREDTotalHoy = (salasREDHoy != null ? salasREDHoy : 0L) + (salasTIyREDHoy != null ? salasTIyREDHoy : 0L);

        // Registros activos del día
        List<IngresoAP> registrosActivosHoy = filtrarPorSitio
                ? ingresoAPService.obtenerRegistrosActivosRecientesPorSitio(sitio, 10)
                : ingresoAPService.obtenerRegistrosActivosRecientes(10);
        
        model.addAttribute("ingresosHoy", ingresosHoy != null ? ingresosHoy : 0L);
        model.addAttribute("ticketsUnicosHoy", ticketsUnicosHoy != null ? ticketsUnicosHoy : 0L);
        model.addAttribute("cantidadCRQHoy", cantidadCRQHoy != null ? cantidadCRQHoy : 0L);
        model.addAttribute("cantidadINCHoy", cantidadINCHoy != null ? cantidadINCHoy : 0L);
        model.addAttribute("cantidadInspeccionHoy", cantidadInspeccionHoy);
        model.addAttribute("salasTIHoy", salasTITotalHoy);
        model.addAttribute("salasREDHoy", salasREDTotalHoy);
        model.addAttribute("registrosActivosHoy", registrosActivosHoy);
        
        // === ESTADÍSTICAS DE GESTIÓN DE ACCESOS DEL DÍA ===
        Long gestionesDelDia = gestionAccesoService.contarGestionesDelDia(hoy, sitio);
        Long ticketsAprobadosHoy = gestionAccesoService.contarTicketsPorEstadoYFecha("Aprobada", hoy, sitio);
        Long ticketsPendientesAprobacion = gestionAccesoService.contarTicketsPorEstado("Pendiente", sitio);
        Long ticketsRechazadosHoy = gestionAccesoService.contarTicketsRechazadosHoy(hoy, sitio);
        Long ticketsDevueltosHoy = gestionAccesoService.contarTicketsDevueltosHoy(hoy, sitio);
        Long ticketsPendientesCierre = gestionAccesoService.contarTicketsPendientesCierre(sitio);
        
        model.addAttribute("gestionesDelDia", gestionesDelDia != null ? gestionesDelDia : 0L);
        model.addAttribute("ticketsAprobadosHoy", ticketsAprobadosHoy != null ? ticketsAprobadosHoy : 0L);
        model.addAttribute("ticketsPendientesAprobacion", ticketsPendientesAprobacion != null ? ticketsPendientesAprobacion : 0L);
        model.addAttribute("ticketsRechazadosHoy", ticketsRechazadosHoy != null ? ticketsRechazadosHoy : 0L);
        model.addAttribute("ticketsDevueltosHoy", ticketsDevueltosHoy != null ? ticketsDevueltosHoy : 0L);
        model.addAttribute("ticketsPendientesCierre", ticketsPendientesCierre != null ? ticketsPendientesCierre : 0L);

        TemperaturaService.TemperaturaResumen resumenTemperatura = temperaturaService.obtenerResumenDiario(sitio, hoy);
        List<MedicionTemperatura> ultimasMediciones = temperaturaService.ultimasMedicionesDiarias(sitio, hoy);
        model.addAttribute("resumenTempDiario", resumenTemperatura);
        model.addAttribute("ultimasMedicionesTemp", ultimasMediciones);
        
        return "dashboard-cliente-diario";
    }
}
