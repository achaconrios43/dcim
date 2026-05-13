package com.example.dcim.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dcim.dao.InventarioRepository;
import com.example.dcim.dao.PlanoSalaRepository;
import com.example.dcim.dao.PuntoMedicionRepository;
import com.example.dcim.dao.SalaRepository;
import com.example.dcim.dao.SitioRepository;
import com.example.dcim.entity.Sala;
import com.example.dcim.entity.Sitio;

@Controller
@RequestMapping("/salas")
public class SalaController {

    private static final List<String> TIPOS_SALA = List.of(
            "Sala de Red", "Sala NOC", "Sala TI", "Sala Empresa");

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private SitioRepository sitioRepository;

    @Autowired
    private PlanoSalaRepository planoSalaRepository;

    @Autowired
    private PuntoMedicionRepository puntoMedicionRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @GetMapping
    public String listar(
            @RequestParam(required = false) Long sitioId,
            Model model) {

        List<Sala> salas = sitioId != null
                ? salaRepository.findByActivoTrueAndSitioIdOrderByNombreAsc(sitioId)
                : salaRepository.findByActivoTrueOrderByNombreAsc();

        // Estadísticas por sala
        List<Map<String, Object>> salasConStats = new ArrayList<>();
        for (Sala s : salas) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sala", s);
            row.put("planos", salaRepository.countPlanosBySalaId(s.getId()));
            row.put("puntos", salaRepository.countPuntosMedicionBySalaId(s.getId()));
            row.put("inventario", inventarioRepository.countBySala(s.getNombre()));
            salasConStats.add(row);
        }

        // Totales por tipo para el resumen
        Map<String, Long> totalPorTipo = new LinkedHashMap<>();
        for (String tipo : TIPOS_SALA) {
            totalPorTipo.put(tipo, salas.stream()
                    .filter(s -> tipo.equalsIgnoreCase(s.getTipo()))
                    .count());
        }

        model.addAttribute("salasConStats", salasConStats);
        model.addAttribute("totalPorTipo", totalPorTipo);
        model.addAttribute("sitios", sitioRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("sitioIdSeleccionado", sitioId);
        model.addAttribute("tiposSala", TIPOS_SALA);
        model.addAttribute("totalSalas", salas.size());
        return "salas";
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public String crear(
            @RequestParam Long sitioId,
            @RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam(required = false) String descripcion,
            RedirectAttributes ra) {

        if (!TIPOS_SALA.contains(tipo)) {
            ra.addFlashAttribute("error", "Tipo de sala no válido");
            return "redirect:/salas";
        }

        Sitio sitio = sitioRepository.findById(sitioId).orElse(null);
        if (sitio == null) {
            ra.addFlashAttribute("error", "Sitio no encontrado");
            return "redirect:/salas";
        }

        boolean existe = salaRepository.findBySitioIdAndNombreIgnoreCase(sitioId, nombre.trim()).isPresent();
        if (existe) {
            ra.addFlashAttribute("error", "Ya existe una sala con ese nombre en el sitio seleccionado");
            return "redirect:/salas?sitioId=" + sitioId;
        }

        Sala sala = new Sala();
        sala.setSitio(sitio);
        sala.setNombre(nombre.trim());
        sala.setTipo(tipo);
        sala.setDescripcion(descripcion != null ? descripcion.trim() : null);
        sala.setActivo(true);
        salaRepository.save(sala);

        ra.addFlashAttribute("success", "Sala \"" + sala.getNombre() + "\" creada correctamente");
        return "redirect:/salas?sitioId=" + sitioId;
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public String desactivar(@PathVariable Long id, RedirectAttributes ra) {
        Sala sala = salaRepository.findById(id).orElse(null);
        if (sala == null) {
            ra.addFlashAttribute("error", "Sala no encontrada");
            return "redirect:/salas";
        }
        sala.setActivo(false);
        salaRepository.save(sala);
        ra.addFlashAttribute("success", "Sala \"" + sala.getNombre() + "\" desactivada");
        return "redirect:/salas";
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public String activar(@PathVariable Long id, RedirectAttributes ra) {
        Sala sala = salaRepository.findById(id).orElse(null);
        if (sala == null) {
            ra.addFlashAttribute("error", "Sala no encontrada");
            return "redirect:/salas";
        }
        sala.setActivo(true);
        salaRepository.save(sala);
        ra.addFlashAttribute("success", "Sala \"" + sala.getNombre() + "\" activada");
        return "redirect:/salas";
    }
}
