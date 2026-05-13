package com.example.dcim.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dcim.dao.MedicionTemperaturaRepository;
import com.example.dcim.dao.SalaRepository;
import com.example.dcim.dao.SitioRepository;
import com.example.dcim.service.TemperaturaService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/temperaturas")
public class TemperaturaController {

    @Autowired
    private TemperaturaService temperaturaService;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private SitioRepository sitioRepository;

    @Autowired
    private MedicionTemperaturaRepository medicionTemperaturaRepository;

    @GetMapping
    public String vistaPrincipal(@RequestParam(required = false) Long salaId,
            @RequestParam(required = false) Long sitioId,
            @RequestParam(required = false) String sitioReporte,
            Model model) {

        LocalDate hoy = LocalDate.now();

        // Salas activas para el selector (opcionalmente filtradas por sitio)
        if (sitioId != null) {
            model.addAttribute("salas", salaRepository.findByActivoTrueAndSitioIdOrderByNombreAsc(sitioId));
        } else {
            model.addAttribute("salas", salaRepository.findByActivoTrueOrderByNombreAsc());
        }
        model.addAttribute("sitios", sitioRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("sitioIdSeleccionado", sitioId);
        model.addAttribute("salaIdSeleccionada", salaId);

        // Puntos de la sala seleccionada (todos, incluidos inactivos para mostrar en tabla CRUD)
        if (salaId != null) {
            model.addAttribute("puntos", temperaturaService.listarTodosPuntosPorSala(salaId));
            model.addAttribute("salaSeleccionada", salaRepository.findById(salaId).orElse(null));
        } else {
            model.addAttribute("puntos", List.of());
            model.addAttribute("salaSeleccionada", null);
        }

        // Resumen diario de la sala seleccionada
        if (salaId != null) {
            // Obtener el sitioId desde la sala si no fue pasado
            salaRepository.findById(salaId).ifPresent(s -> {
                if (s.getSitio() != null) {
                    model.addAttribute("resumenSalas",
                            temperaturaService.obtenerResumenDiarioPorSitioId(s.getSitio().getId(), hoy));
                }
            });
        }
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

    @PostMapping("/puntos")
    public String crearPunto(@RequestParam Long salaId,
            @RequestParam String codigo,
            @RequestParam String nombre,
            @RequestParam BigDecimal temperaturaMinima,
            @RequestParam BigDecimal temperaturaMaxima,
            RedirectAttributes redirectAttributes) {
        try {
            temperaturaService.crearPunto(salaId, codigo, nombre, temperaturaMinima, temperaturaMaxima);
            redirectAttributes.addFlashAttribute("success", "Punto de medición creado correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas?salaId=" + salaId;
    }

    @PostMapping("/puntos/{id}/editar")
    public String editarPunto(@PathVariable Long id,
            @RequestParam Long salaId,
            @RequestParam String codigo,
            @RequestParam String nombre,
            @RequestParam BigDecimal temperaturaMinima,
            @RequestParam BigDecimal temperaturaMaxima,
            RedirectAttributes redirectAttributes) {
        try {
            temperaturaService.actualizarPunto(id, codigo, nombre, temperaturaMinima, temperaturaMaxima);
            redirectAttributes.addFlashAttribute("success", "Punto actualizado correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas?salaId=" + salaId;
    }

    @PostMapping("/puntos/{id}/eliminar")
    public String eliminarPunto(@PathVariable Long id,
            @RequestParam Long salaId,
            RedirectAttributes redirectAttributes) {
        try {
            temperaturaService.eliminarPunto(id);
            redirectAttributes.addFlashAttribute("success", "Punto eliminado correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas?salaId=" + salaId;
    }

    @PostMapping("/mediciones")
    public String registrarMedicion(@RequestParam Long salaId,
            @RequestParam Long puntoId,
            @RequestParam BigDecimal temperatura,
            @RequestParam(required = false) String fechaMedicion,
            RedirectAttributes redirectAttributes) {
        try {
            LocalDate fecha = (fechaMedicion != null && !fechaMedicion.isBlank()) ? LocalDate.parse(fechaMedicion) : LocalDate.now();
            temperaturaService.registrarMedicion(puntoId, fecha, temperatura);
            redirectAttributes.addFlashAttribute("success", "Medición guardada correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas?salaId=" + salaId;
    }

    @GetMapping("/registro")
    public String vistaRegistro(@RequestParam(required = false) Long sitioId,
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false) String horario,
            Model model) {
        LocalDate hoy = LocalDate.now();
        String horarioActivo = (horario != null && (horario.equals("08:00") || horario.equals("20:00"))) ? horario : "08:00";

        // Auto-seleccionar primer sitio si no se proporcionó
        var sitios = sitioRepository.findByActivoTrueOrderByNombreAsc();
        if (sitioId == null && !sitios.isEmpty()) {
            sitioId = sitios.get(0).getId();
        }

        model.addAttribute("sitios", sitios);
        var salas = salaRepository.findByActivoTrueAndSitioIdOrderByNombreAsc(sitioId);
        // Auto-seleccionar primera sala si no se proporcionó
        if (salaId == null && !salas.isEmpty()) {
            salaId = salas.get(0).getId();
        }
        model.addAttribute("salas", salas);

        List<com.example.dcim.entity.PuntoMedicion> todosLosPuntos = temperaturaService.listarPuntosPorSala(salaId);
        model.addAttribute("puntos", todosLosPuntos);
        model.addAttribute("totalPuntosSala", (long) todosLosPuntos.size());
        model.addAttribute("puntosPendientesManana", temperaturaService.listarPuntosPendientesPorSalaYHorario(salaId, hoy, "08:00"));
        model.addAttribute("puntosPendientesNoche", temperaturaService.listarPuntosPendientesPorSalaYHorario(salaId, hoy, "20:00"));
        model.addAttribute("registradosManana", temperaturaService.contarPuntosRegistradosHoyHorario(salaId, hoy, "08:00"));
        model.addAttribute("registradosNoche", temperaturaService.contarPuntosRegistradosHoyHorario(salaId, hoy, "20:00"));
        model.addAttribute("salaSeleccionada", salaId != null ? salaRepository.findById(salaId).orElse(null) : null);
        model.addAttribute("sitioIdSeleccionado", sitioId);
        model.addAttribute("salaIdSeleccionada", salaId);
        model.addAttribute("horarioActivo", horarioActivo);
        model.addAttribute("fechaSeleccionada", hoy);
        model.addAttribute("promedioSalaDiario", temperaturaService.obtenerPromedioDiarioSala(salaId, hoy));
        model.addAttribute("medicionesSalaHoy", temperaturaService.ultimasMedicionesSala(salaId, hoy));
        return "temperaturas-registro";
    }

    @PostMapping("/registro-lote")
    public String registrarLoteDesdePagina(@RequestParam(required = false) Long sitioId,
            @RequestParam Long salaId,
            @RequestParam(required = false) String fechaMedicion,
            @RequestParam(required = false, defaultValue = "08:00") String horario,
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
            TemperaturaService.RegistroLoteResultado resultado = temperaturaService.registrarMedicionesPorSalaConHorario(salaId, fecha, horario, temperaturasPorPunto);

            redirectAttributes.addFlashAttribute("success",
                    "✓ " + resultado.getTotalGuardadas() + " mediciones guardadas para el horario " + horario
                            + ". Promedio: " + resultado.getPromedioSala() + " °C");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas/registro?sitioId=" + sitioId + "&salaId=" + salaId + "&horario=" + horario;
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
            redirectAttributes.addFlashAttribute("success", "Temperatura guardada correctamente");
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
            redirectAttributes.addFlashAttribute("success", "Medición eliminada correctamente");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/temperaturas/registro?sitioId=" + sitioId + "&salaId=" + salaId;
    }

    @GetMapping("/reporte/excel")
    public void exportarExcel(
            @RequestParam Long sitioId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            HttpServletResponse response) throws java.io.IOException {

        LocalDate inicio = (fechaInicio != null && !fechaInicio.isBlank())
                ? LocalDate.parse(fechaInicio) : LocalDate.now().withDayOfMonth(1);
        LocalDate fin = (fechaFin != null && !fechaFin.isBlank())
                ? LocalDate.parse(fechaFin) : LocalDate.now();

        var sitioOpt = sitioRepository.findById(sitioId);
        if (sitioOpt.isEmpty()) { response.sendError(404); return; }
        var sitio = sitioOpt.get();

        var mediciones = medicionTemperaturaRepository.findBySitioIdAndFechaRange(sitioId, inicio, fin);
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Temperaturas");

            // ── Estilos ───────────────────────────────────────
            XSSFFont fuenteBlanca = wb.createFont();
            fuenteBlanca.setBold(true); fuenteBlanca.setFontHeightInPoints((short) 12);
            fuenteBlanca.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());

            XSSFFont fuenteNegrita = wb.createFont();
            fuenteNegrita.setBold(true); fuenteNegrita.setFontHeightInPoints((short) 10);

            XSSFCellStyle estTitulo = wb.createCellStyle();
            estTitulo.setFont(fuenteBlanca);
            estTitulo.setFillForegroundColor(new XSSFColor(new byte[]{19, 78, 74}, null));
            estTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estTitulo.setAlignment(HorizontalAlignment.CENTER);

            XSSFCellStyle estEncabezado = wb.createCellStyle();
            estEncabezado.setFont(fuenteBlanca);
            estEncabezado.setFillForegroundColor(new XSSFColor(new byte[]{13, (byte)148, (byte)136}, null));
            estEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estEncabezado.setAlignment(HorizontalAlignment.CENTER);
            estEncabezado.setBorderBottom(BorderStyle.THIN);

            XSSFCellStyle estDato = wb.createCellStyle();
            estDato.setBorderBottom(BorderStyle.THIN); estDato.setBorderLeft(BorderStyle.THIN);
            estDato.setBorderRight(BorderStyle.THIN); estDato.setBorderTop(BorderStyle.THIN);

            XSSFCellStyle estNum = wb.createCellStyle();
            estNum.cloneStyleFrom(estDato);
            estNum.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            estNum.setAlignment(HorizontalAlignment.CENTER);

            XSSFCellStyle estCentro = wb.createCellStyle();
            estCentro.cloneStyleFrom(estDato);
            estCentro.setAlignment(HorizontalAlignment.CENTER);

            XSSFCellStyle estResumenLabel = wb.createCellStyle();
            estResumenLabel.setFont(fuenteNegrita);
            estResumenLabel.setFillForegroundColor(new XSSFColor(new byte[]{(byte)209, (byte)250, (byte)229}, null));
            estResumenLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estResumenLabel.setBorderBottom(BorderStyle.THIN); estResumenLabel.setBorderLeft(BorderStyle.THIN);
            estResumenLabel.setBorderRight(BorderStyle.THIN); estResumenLabel.setBorderTop(BorderStyle.THIN);

            XSSFCellStyle estResumenNum = wb.createCellStyle();
            estResumenNum.cloneStyleFrom(estResumenLabel);
            estResumenNum.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            estResumenNum.setAlignment(HorizontalAlignment.CENTER);

            // ── Fila título ───────────────────────────────────
            int r = 0;
            XSSFRow rowTitulo = sheet.createRow(r++);
            rowTitulo.setHeightInPoints(22);
            var cTitulo = rowTitulo.createCell(0);
            cTitulo.setCellValue("REPORTE DE TEMPERATURAS — " + sitio.getNombre().toUpperCase());
            cTitulo.setCellStyle(estTitulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            XSSFRow rowPeriodo = sheet.createRow(r++);
            var cPer = rowPeriodo.createCell(0);
            cPer.setCellValue("Período: " + inicio.format(fmtFecha) + " al " + fin.format(fmtFecha));
            cPer.setCellStyle(estDato);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
            var cGen = rowPeriodo.createCell(4);
            cGen.setCellValue("Generado: " + LocalDate.now().format(fmtFecha));
            cGen.setCellStyle(estDato);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 6));

            r++; // fila vacía

            // ── Encabezados ───────────────────────────────────
            XSSFRow rowEnc = sheet.createRow(r++);
            String[] cols = {"Fecha", "Sala", "Código", "Punto de Medición", "Horario", "Temperatura (°C)", "Estado"};
            for (int i = 0; i < cols.length; i++) {
                var c = rowEnc.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(estEncabezado);
            }

            // ── Datos ─────────────────────────────────────────
            for (var m : mediciones) {
                XSSFRow row = sheet.createRow(r++);
                var c0 = row.createCell(0); c0.setCellValue(m.getFechaMedicion().format(fmtFecha)); c0.setCellStyle(estCentro);
                var c1 = row.createCell(1); c1.setCellValue(m.getPunto().getSala().getNombre()); c1.setCellStyle(estDato);
                var c2 = row.createCell(2); c2.setCellValue(m.getPunto().getCodigo()); c2.setCellStyle(estCentro);
                var c3 = row.createCell(3); c3.setCellValue(m.getPunto().getNombre()); c3.setCellStyle(estDato);
                var c4 = row.createCell(4); c4.setCellValue(m.getHorario() != null ? m.getHorario() : "—"); c4.setCellStyle(estCentro);
                var c5 = row.createCell(5); c5.setCellValue(m.getTemperaturaCelsius().doubleValue()); c5.setCellStyle(estNum);
                var c6 = row.createCell(6); c6.setCellValue(m.getEstado()); c6.setCellStyle(estCentro);
            }

            // ── Resumen estadístico ───────────────────────────
            r += 2;
            XSSFRow rowRTitulo = sheet.createRow(r++);
            var cRT = rowRTitulo.createCell(0);
            cRT.setCellValue("RESUMEN ESTADÍSTICO");
            XSSFCellStyle estRT = wb.createCellStyle();
            estRT.setFont(fuenteBlanca);
            estRT.setFillForegroundColor(new XSSFColor(new byte[]{19, 78, 74}, null));
            estRT.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estRT.setAlignment(HorizontalAlignment.CENTER);
            cRT.setCellStyle(estRT);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 6));

            if (!mediciones.isEmpty()) {
                DoubleSummaryStatistics stats = mediciones.stream()
                        .mapToDouble(m -> m.getTemperaturaCelsius().doubleValue())
                        .summaryStatistics();

                String[][] resumen = {
                    {"Promedio General (Media)", String.format("%.2f", stats.getAverage())},
                    {"Temperatura Máxima", String.format("%.2f", stats.getMax())},
                    {"Temperatura Mínima", String.format("%.2f", stats.getMin())},
                    {"Total de Registros", String.valueOf(stats.getCount())}
                };
                for (String[] par : resumen) {
                    XSSFRow rowR = sheet.createRow(r++);
                    var lbl = rowR.createCell(0); lbl.setCellValue(par[0]); lbl.setCellStyle(estResumenLabel);
                    sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 4));
                    var val = rowR.createCell(5); val.setCellValue(par[1]); val.setCellStyle(estResumenNum);
                    sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 5, 6));
                }
            }

            // ── Auto-ancho columnas ───────────────────────────
            for (int i = 0; i < 7; i++) sheet.autoSizeColumn(i);
            sheet.setColumnWidth(0, Math.max(sheet.getColumnWidth(0), 3800));
            sheet.setColumnWidth(3, Math.max(sheet.getColumnWidth(3), 7000));
            sheet.setColumnWidth(5, Math.max(sheet.getColumnWidth(5), 4500));

            String nombre = "temperaturas_" + sitio.getNombre().replaceAll("[^a-zA-Z0-9]", "_")
                    + "_" + inicio + "_" + fin + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + "\"");
            wb.write(response.getOutputStream());
        }
    }
}

