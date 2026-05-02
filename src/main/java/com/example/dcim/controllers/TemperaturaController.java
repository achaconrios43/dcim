package com.example.dcim.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dcim.service.TemperaturaService;

@Controller
@RequestMapping("/temperaturas")
public class TemperaturaController {

    @Autowired
    private TemperaturaService temperaturaService;

    @GetMapping
    public String vistaPrincipal(@RequestParam(required = false) Long sitioId,
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) String sitioReporte,
            Model model) {

        LocalDate hoy = LocalDate.now();

        model.addAttribute("sitios", temperaturaService.listarSitiosActivos());
        model.addAttribute("salas", temperaturaService.listarSalasPorSitio(sitioId));
        model.addAttribute("puntos", temperaturaService.listarPuntosPorSala(salaId));
        model.addAttribute("sitioIdSeleccionado", sitioId);
        model.addAttribute("salaIdSeleccionada", salaId);
        model.addAttribute("resumenSalas", temperaturaService.obtenerResumenDiarioPorSitioId(sitioId, hoy));
        model.addAttribute("fechaResumenSalas", hoy);

        if (sitioReporte != null && !sitioReporte.trim().isEmpty()) {
            model.addAttribute("sitioReporte", sitioReporte);
            model.addAttribute("resumenDiarioTemp", temperaturaService.obtenerResumenDiario(sitioReporte, hoy));
            model.addAttribute("resumenMensualTemp", temperaturaService.obtenerResumenMensual(sitioReporte, hoy));
            model.addAttribute("ultimasMediciones", temperaturaService.ultimasMedicionesDiarias(sitioReporte, hoy));
        }

        return "temperaturas";
    }

    @PostMapping("/sitios")
    public String crearSitio(@RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            RedirectAttributes redirectAttributes) {
        try {
            temperaturaService.crearSitio(nombre, descripcion);
            redirectAttributes.addFlashAttribute("success", "Sitio creado correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas";
    }

    @PostMapping("/salas")
    public String crearSala(@RequestParam Long sitioId,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            RedirectAttributes redirectAttributes) {
        try {
            temperaturaService.crearSala(sitioId, nombre, descripcion);
            redirectAttributes.addFlashAttribute("success", "Sala creada correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas?sitioId=" + sitioId;
    }

    @PostMapping("/puntos")
    public String crearPunto(@RequestParam Long sitioId,
            @RequestParam Long salaId,
            @RequestParam String codigo,
            @RequestParam String nombre,
            @RequestParam BigDecimal temperaturaMinima,
            @RequestParam BigDecimal temperaturaMaxima,
            RedirectAttributes redirectAttributes) {
        try {
            temperaturaService.crearPunto(salaId, codigo, nombre, temperaturaMinima, temperaturaMaxima);
            redirectAttributes.addFlashAttribute("success", "Punto de medicion creado correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas?sitioId=" + sitioId + "&salaId=" + salaId;
    }

    @PostMapping("/mediciones")
    public String registrarMedicion(@RequestParam Long sitioId,
            @RequestParam Long salaId,
            @RequestParam Long puntoId,
            @RequestParam BigDecimal temperatura,
            @RequestParam(required = false) String fechaMedicion,
            RedirectAttributes redirectAttributes) {
        try {
            LocalDate fecha = (fechaMedicion != null && !fechaMedicion.isBlank()) ? LocalDate.parse(fechaMedicion) : LocalDate.now();
            temperaturaService.registrarMedicion(puntoId, fecha, temperatura);
            redirectAttributes.addFlashAttribute("success", "Medicion guardada correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas?sitioId=" + sitioId + "&salaId=" + salaId;
    }

    @GetMapping("/registro")
    public String vistaRegistro(@RequestParam(required = false) Long sitioId,
            @RequestParam(required = false) Long salaId,
            Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("sitios", temperaturaService.listarSitiosActivos());
        model.addAttribute("salas", temperaturaService.listarSalasPorSitio(sitioId));
            model.addAttribute("puntos", temperaturaService.listarPuntosPendientesPorSala(salaId, hoy));
            model.addAttribute("totalPuntosSala", temperaturaService.contarPuntosTotalesPorSala(salaId));
            model.addAttribute("puntosRegistradosHoy", temperaturaService.contarPuntosRegistradosHoy(salaId, hoy));
        model.addAttribute("sitioIdSeleccionado", sitioId);
        model.addAttribute("salaIdSeleccionada", salaId);
        model.addAttribute("fechaSeleccionada", hoy);
        model.addAttribute("promedioSalaDiario", temperaturaService.obtenerPromedioDiarioSala(salaId, hoy));
        model.addAttribute("medicionesSalaHoy", temperaturaService.ultimasMedicionesSala(salaId, hoy));
        return "temperaturas-registro";
    }

    @PostMapping("/registro-lote")
    public String registrarLoteDesdePagina(@RequestParam Long sitioId,
            @RequestParam Long salaId,
            @RequestParam(required = false) String fechaMedicion,
            @RequestParam(name = "puntoIds", required = false) List<Long> puntoIds,
            @RequestParam(name = "temperaturas", required = false) List<BigDecimal> temperaturas,
            RedirectAttributes redirectAttributes) {
        try {
            if (puntoIds == null || temperaturas == null || puntoIds.isEmpty()) {
                throw new Exception("No hay puntos para registrar");
            }

            Map<Long, BigDecimal> temperaturasPorPunto = new LinkedHashMap<>();
            int limite = Math.min(puntoIds.size(), temperaturas.size());
            for (int i = 0; i < limite; i++) {
                Long puntoId = puntoIds.get(i);
                BigDecimal temperatura = temperaturas.get(i);
                if (puntoId != null && temperatura != null) {
                    temperaturasPorPunto.put(puntoId, temperatura);
                }
            }

            LocalDate fecha = (fechaMedicion != null && !fechaMedicion.isBlank()) ? LocalDate.parse(fechaMedicion) : LocalDate.now();
            TemperaturaService.RegistroLoteResultado resultado = temperaturaService.registrarMedicionesPorSala(salaId, fecha,
                    temperaturasPorPunto);

            redirectAttributes.addFlashAttribute("success",
                    "Se guardaron " + resultado.getTotalGuardadas() + " mediciones. Promedio de sala: "
                            + resultado.getPromedioSala() + " C");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas/registro?sitioId=" + sitioId + "&salaId=" + salaId;
    }

    @PostMapping("/registro")
    public String registrarMedicionDesdePagina(@RequestParam Long sitioId,
            @RequestParam Long salaId,
            @RequestParam Long puntoId,
            @RequestParam BigDecimal temperatura,
            @RequestParam(required = false) String fechaMedicion,
            RedirectAttributes redirectAttributes) {
        try {
            LocalDate fecha = (fechaMedicion != null && !fechaMedicion.isBlank()) ? LocalDate.parse(fechaMedicion) : LocalDate.now();
            temperaturaService.registrarMedicion(puntoId, fecha, temperatura);
            redirectAttributes.addFlashAttribute("success", "Temperatura guardada correctamente y disponible en estadisticas");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas/registro?sitioId=" + sitioId + "&salaId=" + salaId;
    }

    @PostMapping("/mediciones/eliminar")
    public String eliminarMedicion(@RequestParam Long medicionId,
            @RequestParam Long sitioId,
            @RequestParam Long salaId,
            RedirectAttributes redirectAttributes) {
        try {
            temperaturaService.eliminarMedicion(medicionId);
            redirectAttributes.addFlashAttribute("success", "Medicion eliminada correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas/registro?sitioId=" + sitioId + "&salaId=" + salaId;
    }
}
