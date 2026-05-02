package com.example.dcim.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.entity.Usuario;
import com.example.dcim.service.GestionAccesoService;
import com.example.dcim.service.IngresoAPService;
import com.example.dcim.service.TemperaturaService;

/**
 * Controlador para el Dashboard de Cliente - Vista Mensual
 * Muestra estadísticas mensuales con gráficos y resumen ejecutivo
 * ACCESO RESTRINGIDO: Solo usuarios con rol ADMIN
 */
@Controller
@RequestMapping("/dashboard/cliente/mensual")
public class DashboardClienteMensualController {
    
    @Autowired
    private IngresoAPService ingresoAPService;
    
    @Autowired
    private GestionAccesoService gestionAccesoService;
    
    @Autowired
    private IUsuarioDao usuarioDao;

    @Autowired
    private TemperaturaService temperaturaService;
    
    @GetMapping
    public String mostrarDashboardMensual(@RequestParam(required = false) String sitio, Authentication authentication, Model model) {
        // Verificar autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        Usuario usuarioLogueado = usuarioDao.findByEmail(email).orElse(null);
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        
        // Solo ADMIN puede acceder al dashboard mensual
        // Solo USER sin privilegios de visualización no puede acceder
        // ADMIN y VIEWER pueden ver estadísticas
        String rol = usuarioLogueado.getRol();
        if (!"ADMIN".equals(rol) && !"VIEWER".equals(rol)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        LocalDate now = LocalDate.now();
        LocalDate primerDiaMes = now.withDayOfMonth(1);
        LocalDate ultimoDiaMes = now.withDayOfMonth(now.lengthOfMonth());
        model.addAttribute("sitiosDisponibles", temperaturaService.listarSitiosActivos());
        
        model.addAttribute("sitioSeleccionado", sitio);
        model.addAttribute("mesActual", now.getMonth().toString());
        model.addAttribute("anioActual", now.getYear());
        
        if (sitio == null || sitio.trim().isEmpty()) {
            model.addAttribute("ingresosTotalesMes", 0L);
            model.addAttribute("ticketsUnicos", 0L);
            model.addAttribute("cantidadCRQ", 0L);
            model.addAttribute("cantidadINC", 0L);
            model.addAttribute("cantidadInspeccionRonda", 0L);
            model.addAttribute("salasTI", 0L);
            model.addAttribute("salasRED", 0L);
            model.addAttribute("gestionesTotalesMes", 0L);
            model.addAttribute("ticketsAprobadosMes", 0L);
            model.addAttribute("ticketsRechazadosMes", 0L);
            model.addAttribute("ticketsDevueltosMes", 0L);
            return "dashboard-cliente-mensual";
        }
        
        // === ESTADÍSTICAS DE INGRESOS TÉCNICOS ===
        Long ingresosTotalesMes = ingresoAPService.contarIngresosPorSitio(sitio, primerDiaMes, ultimoDiaMes);
        Long ticketsUnicos = ingresoAPService.contarTicketsUnicosPorSitio(sitio, primerDiaMes, ultimoDiaMes);
        
        // Tipos de tickets
        Long cantidadCRQ = ingresoAPService.contarTicketsUnicosPorTipoYSitio("CRQ", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadINC = ingresoAPService.contarTicketsUnicosPorTipoYSitio("INC", sitio, primerDiaMes, ultimoDiaMes);
        
        Long cantidadVisitaInspectiva = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Visita Inspectiva", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadRondaRutinaria = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Ronda Rutinaria", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadInspeccionRonda = (cantidadVisitaInspectiva != null ? cantidadVisitaInspectiva : 0L) + 
                                        (cantidadRondaRutinaria != null ? cantidadRondaRutinaria : 0L);
        
        // Salas
        Long salasTI = ingresoAPService.contarPorSalaRemedyYSitio("Salas TI", sitio, primerDiaMes, ultimoDiaMes);
        Long salasRED = ingresoAPService.contarPorSalaRemedyYSitio("Salas de RED", sitio, primerDiaMes, ultimoDiaMes);
        Long salasTIyRED = ingresoAPService.contarPorSalaRemedyYSitio("Salas TI & RED", sitio, primerDiaMes, ultimoDiaMes);
        
        Long salasTITotal = (salasTI != null ? salasTI : 0L) + (salasTIyRED != null ? salasTIyRED : 0L);
        Long salasREDTotal = (salasRED != null ? salasRED : 0L) + (salasTIyRED != null ? salasTIyRED : 0L);
        
        model.addAttribute("ingresosTotalesMes", ingresosTotalesMes != null ? ingresosTotalesMes : 0L);
        model.addAttribute("ticketsUnicos", ticketsUnicos != null ? ticketsUnicos : 0L);
        model.addAttribute("cantidadCRQ", cantidadCRQ != null ? cantidadCRQ : 0L);
        model.addAttribute("cantidadINC", cantidadINC != null ? cantidadINC : 0L);
        model.addAttribute("cantidadInspeccionRonda", cantidadInspeccionRonda);
        model.addAttribute("salasTI", salasTITotal);
        model.addAttribute("salasRED", salasREDTotal);
        
        // === ESTADÍSTICAS DE GESTIÓN DE ACCESOS ===
        // Contar todas las gestiones del mes
        Long gestionesTotalesMes = 0L;
        Long ticketsAprobadosMes = 0L;
        Long ticketsRechazadosMes = 0L;
        Long ticketsDevueltosMes = 0L;
        
        // Iterar por cada día del mes
        LocalDate fecha = primerDiaMes;
        while (!fecha.isAfter(ultimoDiaMes)) {
            gestionesTotalesMes += gestionAccesoService.contarGestionesDelDia(fecha, sitio);
            ticketsAprobadosMes += gestionAccesoService.contarTicketsPorEstadoYFecha("Aprobada", fecha, sitio);
            ticketsRechazadosMes += gestionAccesoService.contarTicketsRechazadosHoy(fecha, sitio);
            ticketsDevueltosMes += gestionAccesoService.contarTicketsDevueltosHoy(fecha, sitio);
            fecha = fecha.plusDays(1);
        }
        
        Long ticketsPendientesAprobacion = gestionAccesoService.contarTicketsPorEstado("Pendiente", sitio);
        Long ticketsPendientesCierre = gestionAccesoService.contarTicketsPendientesCierre(sitio);
        
        model.addAttribute("gestionesTotalesMes", gestionesTotalesMes);
        model.addAttribute("ticketsAprobadosMes", ticketsAprobadosMes);
        model.addAttribute("ticketsRechazadosMes", ticketsRechazadosMes);
        model.addAttribute("ticketsDevueltosMes", ticketsDevueltosMes);
        model.addAttribute("ticketsPendientesAprobacion", ticketsPendientesAprobacion != null ? ticketsPendientesAprobacion : 0L);
        model.addAttribute("ticketsPendientesCierre", ticketsPendientesCierre != null ? ticketsPendientesCierre : 0L);

        TemperaturaService.TemperaturaResumen resumenMensualTemp = temperaturaService.obtenerResumenMensual(sitio, now);
        model.addAttribute("resumenTempMensual", resumenMensualTemp);
        
        return "dashboard-cliente-mensual";
    }
}
