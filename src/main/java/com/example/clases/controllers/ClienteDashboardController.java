package com.example.clases.controllers;

import com.example.clases.service.IngresoAPService;
import com.example.clases.service.GestionAccesoService;
import com.example.clases.entity.IngresoAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para el Dashboard de Cliente
 * Muestra estadísticas y registros recientes del mes actual
 */
@Controller
@RequestMapping("/dashboard/cliente")
public class ClienteDashboardController {
    
    @Autowired
    private IngresoAPService ingresoAPService;
    
    @Autowired
    private GestionAccesoService gestionAccesoService;
    
    @GetMapping
    public String mostrarDashboard(@org.springframework.web.bind.annotation.RequestParam(required = false) String sitio, Model model) {
        // Obtener fechas del mes actual
        LocalDate now = LocalDate.now();
        LocalDate primerDiaMes = now.withDayOfMonth(1);
        LocalDate ultimoDiaMes = now.withDayOfMonth(now.lengthOfMonth());
        
        // Agregar sitio seleccionado al modelo
        model.addAttribute("sitioSeleccionado", sitio);
        model.addAttribute("mesActual", now.getMonth().toString());
        model.addAttribute("anioActual", now.getYear());
        
        // Si no hay sitio seleccionado, solo mostrar el formulario
        if (sitio == null || sitio.trim().isEmpty()) {
            model.addAttribute("ingresosTotalesMes", 0L);
            model.addAttribute("ticketsUnicos", 0L);
            model.addAttribute("cantidadCRQ", 0L);
            model.addAttribute("cantidadINC", 0L);
            model.addAttribute("cantidadInspeccionRonda", 0L);
            model.addAttribute("salasTI", 0L);
            model.addAttribute("salasRED", 0L);
            model.addAttribute("registrosRecientes", List.of());
            return "dashboard-cliente";
        }
        
        // 1. Ingresos totales del mes en curso por sitio
        Long ingresosTotalesMes = ingresoAPService.contarIngresosPorSitio(sitio, primerDiaMes, ultimoDiaMes);
        
        // 2. Cantidad de tickets únicos (no repetidos) ingresados por sitio
        Long ticketsUnicos = ingresoAPService.contarTicketsUnicosPorSitio(sitio, primerDiaMes, ultimoDiaMes);
        
        // 3. Cantidad por tipo de ticket (tickets únicos sin repetir) por sitio
        Long cantidadCRQ = ingresoAPService.contarTicketsUnicosPorTipoYSitio("CRQ", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadINC = ingresoAPService.contarTicketsUnicosPorTipoYSitio("INC", sitio, primerDiaMes, ultimoDiaMes);
        
        // Visita Inspectiva y Ronda Rutinaria se suman (tickets únicos, se muestran como una sola)
        Long cantidadVisitaInspectiva = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Visita Inspectiva", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadRondaRutinaria = ingresoAPService.contarTicketsUnicosPorTipoYSitio("Ronda Rutinaria", sitio, primerDiaMes, ultimoDiaMes);
        Long cantidadInspeccionRonda = (cantidadVisitaInspectiva != null ? cantidadVisitaInspectiva : 0L) + 
                                        (cantidadRondaRutinaria != null ? cantidadRondaRutinaria : 0L);
        
        // 4. Cantidad por salas RED o TI por sitio
        Long salasTI = ingresoAPService.contarPorSalaRemedyYSitio("Salas TI", sitio, primerDiaMes, ultimoDiaMes);
        Long salasRED = ingresoAPService.contarPorSalaRemedyYSitio("Salas de RED", sitio, primerDiaMes, ultimoDiaMes);
        
        // Salas TI & RED se suma a ambas categorías
        Long salasTIyRED = ingresoAPService.contarPorSalaRemedyYSitio("Salas TI & RED", sitio, primerDiaMes, ultimoDiaMes);
        Long salasTITotal = (salasTI != null ? salasTI : 0L) + (salasTIyRED != null ? salasTIyRED : 0L);
        Long salasREDTotal = (salasRED != null ? salasRED : 0L) + (salasTIyRED != null ? salasTIyRED : 0L);
        
        // 5. Detalles de registros recientes (solo activos) por sitio
        List<IngresoAP> registrosActivosRecientes = ingresoAPService.obtenerRegistrosActivosRecientesPorSitio(sitio, 10);
        
        // Agregar atributos al modelo
        model.addAttribute("ingresosTotalesMes", ingresosTotalesMes != null ? ingresosTotalesMes : 0L);
        model.addAttribute("ticketsUnicos", ticketsUnicos != null ? ticketsUnicos : 0L);
        
        // Tipos de tickets
        model.addAttribute("cantidadCRQ", cantidadCRQ != null ? cantidadCRQ : 0L);
        model.addAttribute("cantidadINC", cantidadINC != null ? cantidadINC : 0L);
        model.addAttribute("cantidadInspeccionRonda", cantidadInspeccionRonda);
        
        // Salas
        model.addAttribute("salasTI", salasTITotal);
        model.addAttribute("salasRED", salasREDTotal);
        
        // Registros recientes
        model.addAttribute("registrosRecientes", registrosActivosRecientes);
        
        // ============================================
        // ESTADÍSTICAS DE GESTIÓN DE ACCESOS
        // ============================================
        LocalDate hoy = LocalDate.now();
        
        // Gestiones del día por sitio
        Long gestionesDelDia = gestionAccesoService.contarGestionesDelDia(hoy, sitio);
        
        // Gestiones que no llegaron en el día (fecha inicio > fecha registro)
        Long gestionesNoLlegaronEnDia = gestionAccesoService.contarGestionesNoLlegaronEnDia(hoy, sitio);
        
        // Gestiones vigentes (actividad en curso)
        Long gestionesVigentes = gestionAccesoService.contarGestionesVigentes(hoy, sitio);
        
        // Tickets únicos gestionados en el día
        Long ticketsGestionados = gestionAccesoService.contarTicketsUnicosDelDia(hoy, sitio);
        
        // Agregar estadísticas de gestión al modelo
        model.addAttribute("gestionesDelDia", gestionesDelDia != null ? gestionesDelDia : 0L);
        model.addAttribute("gestionesNoLlegaronEnDia", gestionesNoLlegaronEnDia != null ? gestionesNoLlegaronEnDia : 0L);
        model.addAttribute("gestionesVigentes", gestionesVigentes != null ? gestionesVigentes : 0L);
        model.addAttribute("ticketsGestionados", ticketsGestionados != null ? ticketsGestionados : 0L);
        
        return "dashboard-cliente";
    }
}
