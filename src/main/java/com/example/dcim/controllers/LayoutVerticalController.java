package com.example.dcim.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.dcim.dao.InventarioRepository;
import com.example.dcim.entity.Inventario;

@Controller
@RequestMapping("/layout-vertical")
public class LayoutVerticalController {

    @Autowired
    private InventarioRepository inventarioRepository;

    @GetMapping
    public String vista(
            @RequestParam(required = false) String sitio,
            @RequestParam(required = false) String sala,
            @RequestParam(required = false) String rack,
            Model model) {

        List<String> sitios = inventarioRepository.findDistinctSitios();
        List<String> salas  = inventarioRepository.findDistinctSalasBySitio(sitio);
        List<String> racks  = inventarioRepository.findDistinctRacksBySitioAndSala(sitio, sala);

        model.addAttribute("sitios", sitios);
        model.addAttribute("salas", salas);
        model.addAttribute("racks", racks);
        model.addAttribute("sitioSel", sitio != null ? sitio : "");
        model.addAttribute("salaSel",  sala  != null ? sala  : "");
        model.addAttribute("rackSel",  rack  != null ? rack  : "");

        Map<String, RackViewModel> racksData = new LinkedHashMap<>();

        if ((sitio != null && !sitio.isBlank()) || (sala != null && !sala.isBlank())) {
            List<Inventario> equipos = inventarioRepository.findByFiltroLayout(sitio, sala, rack);

            for (Inventario inv : equipos) {
                String rackNombre = (inv.getNombreRack() != null && !inv.getNombreRack().isBlank()
                        && !inv.getNombreRack().equals("N/A")) ? inv.getNombreRack() : "Sin Rack";

                RackViewModel rv = racksData.computeIfAbsent(rackNombre, RackViewModel::new);
                if (rv.sitio.isBlank()) rv.sitio = inv.getSitio() != null ? inv.getSitio() : "";
                if (rv.sala.isBlank())  rv.sala  = inv.getSala()  != null ? inv.getSala()  : "";

                String tipo = inv.getTipo() != null ? inv.getTipo().trim().toLowerCase() : "";
                if (tipo.equals("rack") || tipo.equals("racks")) {
                    if (inv.getCapacidadUrRack() != null) rv.capacidadTotal = inv.getCapacidadUrRack();
                    if (inv.getMarca()       != null && !inv.getMarca().isBlank()       && !inv.getMarca().equals("N/A"))       rv.marcaRack   = inv.getMarca();
                    if (inv.getModelo()      != null && !inv.getModelo().isBlank()      && !inv.getModelo().equals("N/A"))      rv.modeloRack  = inv.getModelo();
                    if (inv.getCoordenadas() != null && !inv.getCoordenadas().isBlank() && !inv.getCoordenadas().equals("N/A")) rv.coordenadas = inv.getCoordenadas();
                } else {
                    rv.equipos.add(inv);
                }
            }

            for (RackViewModel rv : racksData.values()) {
                if (rv.capacidadTotal == null || rv.capacidadTotal <= 0) {
                    int maxPos = 0;
                    for (Inventario eq : rv.equipos) {
                        int pos  = parseUr(eq.getUbicacionUr());
                        int span = parseUr(eq.getUrUtilizada());
                        if (span <= 0) span = 1;
                        maxPos = Math.max(maxPos, pos + span - 1);
                    }
                    rv.capacidadTotal = Math.max(maxPos + 2, 12);
                }
                buildSlots(rv);
            }
        }

        model.addAttribute("racksData", racksData);
        return "layout-vertical";
    }

    private void buildSlots(RackViewModel rv) {
        int cap = rv.capacidadTotal;
        UnitSlot[] arr = new UnitSlot[cap + 1];
        for (int u = 1; u <= cap; u++) arr[u] = new UnitSlot(u);

        for (Inventario eq : rv.equipos) {
            int start = parseUr(eq.getUbicacionUr());
            int span  = parseUr(eq.getUrUtilizada());
            if (span  <= 0) span  = 1;
            if (start <= 0 || start > cap) continue;
            int end = Math.min(start + span - 1, cap);

            arr[start].isEmpty   = false;
            arr[start].equipo    = eq;
            arr[start].spanUnits = end - start + 1;
            for (int u = start + 1; u <= end; u++) arr[u].skip = true;
            rv.urUsadas += arr[start].spanUnits;
        }
        rv.urLibres = Math.max(0, cap - rv.urUsadas);

        for (int u = cap; u >= 1; u--) {
            if (!arr[u].skip) rv.slots.add(arr[u]);
        }
    }

    @GetMapping("/api/salas")
    @ResponseBody
    public List<String> salasPorSitio(@RequestParam(required = false) String sitio) {
        return inventarioRepository.findDistinctSalasBySitio(sitio);
    }

    @GetMapping("/api/racks")
    @ResponseBody
    public List<String> racksPorSitioSala(
            @RequestParam(required = false) String sitio,
            @RequestParam(required = false) String sala) {
        return inventarioRepository.findDistinctRacksBySitioAndSala(sitio, sala);
    }

    private int parseUr(String val) {
        if (val == null || val.isBlank() || val.equals("N/A") || val.equals("0")) return 0;
        try {
            String num = val.trim().replaceAll("[^0-9]", "");
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (NumberFormatException e) { return 0; }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ViewModels
    // ═══════════════════════════════════════════════════════════════════════

    public static class UnitSlot {
        public final int unitNumber;
        public boolean isEmpty   = true;
        public boolean skip      = false;
        public Inventario equipo = null;
        public int spanUnits     = 1;

        public UnitSlot(int u) { this.unitNumber = u; }

        public String getColorBg() {
            if (equipo == null) return "#111827";
            String t = equipo.getTipo() != null ? equipo.getTipo().toLowerCase() : "";
            if (t.contains("servidor") || t.contains("server") || t.contains("blade")) return "#1e3a8a";
            if (t.contains("switch"))   return "#134e4a";
            if (t.contains("router"))   return "#4c1d95";
            if (t.contains("firewall")) return "#7f1d1d";
            if (t.contains("ups"))      return "#78350f";
            if (t.contains("patch"))    return "#1f2937";
            if (t.contains("kvm"))      return "#312e81";
            if (t.contains("storage") || t.contains("almacen") || t.contains("san")) return "#064e3b";
            if (t.contains("pdu"))      return "#431407";
            return "#1e3a5f";
        }

        public String getAccentColor() {
            if (equipo == null) return "#1f2937";
            String t = equipo.getTipo() != null ? equipo.getTipo().toLowerCase() : "";
            if (t.contains("servidor") || t.contains("server") || t.contains("blade")) return "#3b82f6";
            if (t.contains("switch"))   return "#14b8a6";
            if (t.contains("router"))   return "#8b5cf6";
            if (t.contains("firewall")) return "#ef4444";
            if (t.contains("ups"))      return "#f59e0b";
            if (t.contains("patch"))    return "#6b7280";
            if (t.contains("kvm"))      return "#6366f1";
            if (t.contains("storage") || t.contains("almacen") || t.contains("san")) return "#10b981";
            if (t.contains("pdu"))      return "#f97316";
            return "#60a5fa";
        }

        public String getNombreDisplay() {
            if (equipo == null) return "";
            StringBuilder sb = new StringBuilder();
            if (equipo.getMarca()  != null && !equipo.getMarca().equals("N/A"))  sb.append(equipo.getMarca()).append(" ");
            if (equipo.getModelo() != null && !equipo.getModelo().equals("N/A")) sb.append(equipo.getModelo());
            String d = sb.toString().trim();
            if (equipo.getHotname() != null && !equipo.getHotname().equals("N/A") && !equipo.getHotname().isBlank())
                d = d.isEmpty() ? equipo.getHotname() : d + "  ·  " + equipo.getHotname();
            return d.isEmpty() ? "(sin nombre)" : d;
        }

        public String getEstadoColor() {
            if (equipo == null || equipo.getEstado() == null) return "#6b7280";
            switch (equipo.getEstado()) {
                case "Operativo": return "#22c55e";
                case "Alarmado":  return "#ef4444";
                case "Apagado":   return "#94a3b8";
                case "STAND BY":  return "#f59e0b";
                default:          return "#6b7280";
            }
        }

        public int getHeightPx() { return 30 * spanUnits; }

        public String getTipoLabel() {
            if (equipo == null || equipo.getTipo() == null) return "";
            String t = equipo.getTipo().trim().toLowerCase();
            if (t.contains("servidor") || t.contains("server")) return "SRV";
            if (t.contains("blade"))    return "BLD";
            if (t.contains("switch"))   return "SW";
            if (t.contains("router"))   return "RTR";
            if (t.contains("firewall")) return "FW";
            if (t.contains("ups"))      return "UPS";
            if (t.contains("patch"))    return "PP";
            if (t.contains("kvm"))      return "KVM";
            if (t.contains("storage") || t.contains("san")) return "SAN";
            if (t.contains("almacen"))  return "STR";
            if (t.contains("pdu"))      return "PDU";
            return equipo.getTipo().length() > 6 ? equipo.getTipo().substring(0,6).toUpperCase() : equipo.getTipo().toUpperCase();
        }
    }

    public static class RackViewModel {
        public String nombre;
        public String sitio = "";
        public String sala  = "";
        public String marcaRack     = "";
        public String modeloRack    = "";
        public String coordenadas   = "";
        public Integer capacidadTotal = null;
        public int urUsadas = 0;
        public int urLibres = 0;
        public List<Inventario> equipos = new ArrayList<>();
        public List<UnitSlot>   slots   = new ArrayList<>();

        public RackViewModel(String nombre) { this.nombre = nombre; }

        public int getPorcentaje() {
            if (capacidadTotal == null || capacidadTotal == 0) return 0;
            return Math.min(100, urUsadas * 100 / capacidadTotal);
        }

        public String getPorcentajeColor() {
            int p = getPorcentaje();
            return p >= 85 ? "#ef4444" : p >= 60 ? "#f59e0b" : "#22c55e";
        }
    }
}
