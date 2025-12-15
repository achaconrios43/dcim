package com.example.clases.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.clases.dao.IUsuarioDao;
import com.example.clases.entity.IngresoAP;
import com.example.clases.entity.Usuario;
import com.example.clases.service.GestionAccesoService;
import com.example.clases.service.IngresoAPService;

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
        
        model.addAttribute("sitioSeleccionado", sitio);
        model.addAttribute("fechaActual", hoy);
        model.addAttribute("diaSemana", hoy.getDayOfWeek().toString());
        
        if (sitio == null || sitio.trim().isEmpty()) {
            model.addAttribute("ingresosHoy", 0L);
            model.addAttribute("ticketsUnicosHoy", 0L);
            model.addAttribute("cantidadCRQHoy", 0L);
            model.addAttribute("cantidadINCHoy", 0L);
            model.addAttribute("cantidadInspeccionHoy", 0L);
            model.addAttribute("salasTIHoy", 0L);
            model.addAttribute("salasREDHoy", 0L);
            model.addAttribute("gestionesDelDia", 0L);
            model.addAttribute("ticketsAprobadosHoy", 0L);
            model.addAttribute("ticketsPendientesAprobacion", 0L);
            model.addAttribute("ticketsRechazadosHoy", 0L);
            model.addAttribute("ticketsDevueltosHoy", 0L);
            model.addAttribute("ticketsPendientesCierre", 0L);
            model.addAttribute("registrosActivosHoy", List.of());
            return "dashboard-cliente-diario";
        }
        
        // === ESTADÍSTICAS DE INGRESOS TÉCNICOS DEL DÍA ===
        Long ingresosHoy = ingresoAPService.contarIngresosPorSitio(sitio, hoy, hoy);
        Long ticketsUnicosHoy = ingresoAPService.contarTicketsUnicosPorSitio(sitio, hoy, hoy);
        
        // Tipos de tickets del día
        Long cantidadCRQHoy = ingresoAPService.contarTicketsUnicosPorTipoYSitio("CRQ", sitio, hoy, hoy);
        Long cantidadINCHoy = ingresoAPService.contarTicketsUnicosPorTipoYSitio("INC", sitio, hoy, hoy);
        
        Long cantidadVisitaInspectivaHoy = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Visita Inspectiva", sitio, hoy, hoy);
        Long cantidadRondaRutinariaHoy = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Ronda Rutinaria", sitio, hoy, hoy);
        Long cantidadInspeccionHoy = (cantidadVisitaInspectivaHoy != null ? cantidadVisitaInspectivaHoy : 0L) + 
                                      (cantidadRondaRutinariaHoy != null ? cantidadRondaRutinariaHoy : 0L);
        
        // Salas del día
        Long salasTIHoy = ingresoAPService.contarPorSalaRemedyYSitio("Salas TI", sitio, hoy, hoy);
        Long salasREDHoy = ingresoAPService.contarPorSalaRemedyYSitio("Salas de RED", sitio, hoy, hoy);
        Long salasTIyREDHoy = ingresoAPService.contarPorSalaRemedyYSitio("Salas TI & RED", sitio, hoy, hoy);
        
        Long salasTITotalHoy = (salasTIHoy != null ? salasTIHoy : 0L) + (salasTIyREDHoy != null ? salasTIyREDHoy : 0L);
        Long salasREDTotalHoy = (salasREDHoy != null ? salasREDHoy : 0L) + (salasTIyREDHoy != null ? salasTIyREDHoy : 0L);
        
        // Registros activos del día
        List<IngresoAP> registrosActivosHoy = ingresoAPService.obtenerRegistrosActivosRecientesPorSitio(sitio, 10);
        
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
        
        return "dashboard-cliente-diario";
    }
}
