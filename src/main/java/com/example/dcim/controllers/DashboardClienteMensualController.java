package com.example.dcim.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.dao.SitioRepository;
import com.example.dcim.entity.Usuario;
import com.example.dcim.service.GestionAccesoService;
import com.example.dcim.service.IngresoAPService;
import com.example.dcim.service.TemperaturaService;

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

    @Autowired
    private SitioRepository sitioRepository;
    
    @GetMapping
    public String mostrarDashboardMensual(@RequestParam(required = false) String sitio, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        Usuario usuarioLogueado = usuarioDao.findByEmail(email).orElse(null);
        if (usuarioLogueado == null) return "redirect:/login";

        String rol = usuarioLogueado.getRol();
        if (!"ADMIN".equals(rol) && !"VIEWER".equals(rol) && !"CLIENTE".equals(rol)) return "redirect:/dashboard";
        
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        LocalDate now = LocalDate.now();
        LocalDate primerDiaMes = now.withDayOfMonth(1);
        LocalDate ultimoDiaMes = now.withDayOfMonth(now.lengthOfMonth());

        // Dropdown desde tabla Sitio (igual que dashboard diario)
        model.addAttribute("sitiosDisponibles", sitioRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("sitioSeleccionado", sitio);
        model.addAttribute("mesActual", now.getMonth().toString());
        model.addAttribute("anioActual", now.getYear());

        boolean filtrarPorSitio = sitio != null && !sitio.trim().isEmpty();

        // Sin sitio seleccionado: no calcular estadísticas
        if (!filtrarPorSitio) {
            return "dashboard-cliente-mensual";
        }

        // === ESTADÍSTICAS DE INGRESOS TÉCNICOS ===
        Long ingresosTotalesMes = ingresoAPService.contarIngresosPorSitio(sitio, primerDiaMes, ultimoDiaMes);
        Long ticketsUnicos = ingresoAPService.contarTicketsUnicosPorSitio(sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadCRQ = ingresoAPService.contarTicketsUnicosPorTipoYSitio("CRQ", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadINC = ingresoAPService.contarTicketsUnicosPorTipoYSitio("INC", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadVisitaInspectiva = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Visita Inspectiva", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadRondaRutinaria = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Ronda Rutinaria", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadInspeccionRonda = (cantidadVisitaInspectiva != null ? cantidadVisitaInspectiva : 0L)
                                     + (cantidadRondaRutinaria != null ? cantidadRondaRutinaria : 0L);
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
        Long gestionesTotalesMes = 0L;
        Long ticketsAprobadosMes = 0L;
        Long ticketsRechazadosMes = 0L;
        Long ticketsDevueltosMes = 0L;
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

        // === TEMPERATURA ===
        TemperaturaService.TemperaturaResumen resumenMensualTemp = temperaturaService.obtenerResumenMensual(sitio, now);
        model.addAttribute("resumenTempMensual", resumenMensualTemp);

        // Promedios diarios para gráfico lineal
        Map<String, Double> promediosDiarios = temperaturaService.promediosDiariosDelMes(sitio, primerDiaMes, ultimoDiaMes);
        List<String> tempLabels = new ArrayList<>(promediosDiarios.keySet());
        List<String> tempValores = new ArrayList<>();
        for (Double v : promediosDiarios.values()) {
            tempValores.add(v != null ? String.format("%.1f", v).replace(',', '.') : "null");
        }
        model.addAttribute("tempLabels", tempLabels);
        model.addAttribute("tempValores", tempValores);
        
        return "dashboard-cliente-mensual";
    }
}