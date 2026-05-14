package com.example.dcim.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import com.example.dcim.entity.Inventario;

public class ExcelImportUtil {

    // Alias de encabezados aceptados (en minúsculas sin espacios/guiones)
    private static final Map<String, String> HEADER_ALIASES = new HashMap<>();
    static {
        HEADER_ALIASES.put("sala",                      "sala");
        HEADER_ALIASES.put("tipo",                      "tipo");
        HEADER_ALIASES.put("marca",                     "marca");
        HEADER_ALIASES.put("modelo",                    "modelo");
        HEADER_ALIASES.put("numeroserie",               "numeroSerie");
        HEADER_ALIASES.put("numero_serie",              "numeroSerie");
        HEADER_ALIASES.put("nserie",                    "numeroSerie");
        HEADER_ALIASES.put("n_serie",                   "numeroSerie");
        HEADER_ALIASES.put("nroserie",                  "numeroSerie");
        HEADER_ALIASES.put("numeroserie",               "numeroSerie");
        HEADER_ALIASES.put("sn",                        "numeroSerie");
        HEADER_ALIASES.put("serialnumber",              "numeroSerie");
        HEADER_ALIASES.put("serial",                    "numeroSerie");
        HEADER_ALIASES.put("tag",                       "tag");
        HEADER_ALIASES.put("cliente",                   "cliente");
        HEADER_ALIASES.put("coordenadas",               "coordenadas");
        HEADER_ALIASES.put("nombrerack",                "nombreRack");
        HEADER_ALIASES.put("nombre_rack",               "nombreRack");
        HEADER_ALIASES.put("rack",                      "nombreRack");
        HEADER_ALIASES.put("ubicacionur",               "ubicacionUr");
        HEADER_ALIASES.put("ubicacion_ur",              "ubicacionUr");
        HEADER_ALIASES.put("urutilizada",               "urUtilizada");
        HEADER_ALIASES.put("ur_utilizada",              "urUtilizada");
        HEADER_ALIASES.put("ur",                        "urUtilizada");
        HEADER_ALIASES.put("numerotemporal",            "numeroTemporal");
        HEADER_ALIASES.put("numero_temporal",           "numeroTemporal");
        HEADER_ALIASES.put("hotname",                   "hotname");
        HEADER_ALIASES.put("hostname",                  "hotname");
        HEADER_ALIASES.put("estado",                    "estado");
        HEADER_ALIASES.put("fechaalarma",               "fechaAlarma");
        HEADER_ALIASES.put("fecha_alarma",              "fechaAlarma");
        HEADER_ALIASES.put("alarmahardware",            "alarmaHardware");
        HEADER_ALIASES.put("alarma_hardware",           "alarmaHardware");
        HEADER_ALIASES.put("alarmaventilador",          "alarmaVentilador");
        HEADER_ALIASES.put("alarma_ventilador",         "alarmaVentilador");
        HEADER_ALIASES.put("alarmafuentepoder",         "alarmaFuentePoder");
        HEADER_ALIASES.put("alarma_fuente_poder",       "alarmaFuentePoder");
        HEADER_ALIASES.put("alarmahdd",                 "alarmaHdd");
        HEADER_ALIASES.put("alarma_hdd",                "alarmaHdd");
        HEADER_ALIASES.put("comentariosalarma",         "comentariosAlarma");
        HEADER_ALIASES.put("comentarios_alarma",        "comentariosAlarma");
        HEADER_ALIASES.put("ticketrelacion",            "ticketRelacion");
        HEADER_ALIASES.put("ticket_relacion",           "ticketRelacion");
        HEADER_ALIASES.put("ticket",                    "ticketRelacion");
        HEADER_ALIASES.put("observaciones",             "observaciones");
        HEADER_ALIASES.put("flujoaire",                 "flujoAire");
        HEADER_ALIASES.put("flujo_aire",                "flujoAire");
        HEADER_ALIASES.put("pesoequipokg",              "pesoEquipoKg");
        HEADER_ALIASES.put("peso_equipo_kg",            "pesoEquipoKg");
        HEADER_ALIASES.put("peso",                      "pesoEquipoKg");
        HEADER_ALIASES.put("fuentespoder",              "fuentesPoder");
        HEADER_ALIASES.put("fuentes_poder",             "fuentesPoder");
        HEADER_ALIASES.put("tiposenchufe",              "tiposEnchufe");
        HEADER_ALIASES.put("tipos_enchufe",             "tiposEnchufe");
        HEADER_ALIASES.put("observaciontipoenchufe",    "observacionTipoEnchufe");
        HEADER_ALIASES.put("observacion_tipo_enchufe",  "observacionTipoEnchufe");
        HEADER_ALIASES.put("potenciaconsumowatts",      "potenciaConsumoWatts");
        HEADER_ALIASES.put("potencia_consumo_watts",    "potenciaConsumoWatts");
        HEADER_ALIASES.put("potencia",                  "potenciaConsumoWatts");
        HEADER_ALIASES.put("direccionip",               "direccionIp");
        HEADER_ALIASES.put("direccion_ip",              "direccionIp");
        HEADER_ALIASES.put("ip",                        "direccionIp");
        HEADER_ALIASES.put("sitio",                     "sitio");
    }

    // Resultado del import con diagnóstico
    public static class ImportResult {
        public final List<Inventario> items;
        public final int totalFilasHoja;      // filas que ve POI
        public final int filasConDatos;       // filas no vacías
        public final boolean encabezadosDetectados;
        public final String columnasMapeadas;

        ImportResult(List<Inventario> items, int totalFilasHoja, int filasConDatos,
                     boolean encabezadosDetectados, String columnasMapeadas) {
            this.items = items;
            this.totalFilasHoja = totalFilasHoja;
            this.filasConDatos = filasConDatos;
            this.encabezadosDetectados = encabezadosDetectados;
            this.columnasMapeadas = columnasMapeadas;
        }
    }

    public static List<Inventario> importarExcel(MultipartFile file) throws IOException {
        return importarExcelConDiagnostico(file).items;
    }

    public static ImportResult importarExcelConDiagnostico(MultipartFile file) throws IOException {
        List<Inventario> inventarios = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalFilasHoja = sheet.getLastRowNum(); // 0-based, última fila con contenido

            // --- Leer encabezados de la primera fila ---
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colIndex = new HashMap<>();
            if (headerRow != null) {
                for (int c = 0; c <= headerRow.getLastCellNum(); c++) {
                    Cell hCell = headerRow.getCell(c);
                    if (hCell == null) continue;
                    String raw = getCellValue(hCell);
                    if (raw == null) continue;
                    // Normalizar: quitar acentos, minúsculas, quitar espacios/guiones/puntos
                    String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
                            .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                            .toLowerCase()
                            .replaceAll("[\\s_\\-./°#]+", "")
                            .trim();
                    String fieldName = HEADER_ALIASES.get(normalized);
                    if (fieldName != null) {
                        colIndex.put(fieldName, c);
                    }
                }
            }

            // Si no se encontraron encabezados, usar índices fijos como fallback
            boolean usoEncabezados = !colIndex.isEmpty();
            if (!usoEncabezados) {
                colIndex.put("sala",                     0);
                colIndex.put("tipo",                     1);
                colIndex.put("marca",                    2);
                colIndex.put("modelo",                   3);
                colIndex.put("numeroSerie",              4);
                colIndex.put("tag",                      5);
                colIndex.put("cliente",                  6);
                colIndex.put("coordenadas",              7);
                colIndex.put("nombreRack",               8);
                colIndex.put("ubicacionUr",              9);
                colIndex.put("urUtilizada",              10);
                colIndex.put("numeroTemporal",           11);
                colIndex.put("hotname",                  12);
                colIndex.put("estado",                   13);
                colIndex.put("fechaAlarma",              14);
                colIndex.put("alarmaHardware",           15);
                colIndex.put("alarmaVentilador",         16);
                colIndex.put("alarmaFuentePoder",        17);
                colIndex.put("alarmaHdd",                18);
                colIndex.put("comentariosAlarma",        19);
                colIndex.put("ticketRelacion",           20);
                colIndex.put("observaciones",            21);
                colIndex.put("flujoAire",                22);
                colIndex.put("pesoEquipoKg",             23);
                colIndex.put("fuentesPoder",             24);
                colIndex.put("tiposEnchufe",             25);
                colIndex.put("observacionTipoEnchufe",   26);
                colIndex.put("potenciaConsumoWatts",     27);
                colIndex.put("direccionIp",              28);
                colIndex.put("sitio",                    29);
            }

            // --- Iterar filas de datos ---
            // Buscar la fila de encabezado real: puede no estar en la fila 0
            // (algunos Excel tienen títulos/subtítulos antes de los datos)
            int headerRowIdx = 0;
            if (usoEncabezados) {
                headerRowIdx = 0; // ya detectamos en fila 0
            }

            // También iterar desde fila 1 hasta el último número físico de filas
            // Usar getPhysicalNumberOfRows() para no saltarse filas con contenido
            int filasConDatos = 0;
            int startRow = headerRowIdx + 1;
            for (int i = startRow; i <= totalFilasHoja; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Intentar leer al menos las primeras columnas de manera cruda
                // para detectar si la fila tiene cualquier dato
                boolean hayAlgoCrudo = false;
                for (int c = 0; c < Math.min(row.getLastCellNum() + 1, 35); c++) {
                    Cell rawCell = row.getCell(c);
                    if (rawCell != null) {
                        String v = getCellValue(rawCell);
                        if (v != null) { hayAlgoCrudo = true; break; }
                    }
                }
                if (!hayAlgoCrudo) continue;
                filasConDatos++;

                // --- Leer todos los campos ---
                String sala          = str(row, colIndex, "sala");
                String tipo          = str(row, colIndex, "tipo");
                String marca         = str(row, colIndex, "marca");
                String modelo        = str(row, colIndex, "modelo");
                String numeroSerie   = str(row, colIndex, "numeroSerie");
                String tag           = str(row, colIndex, "tag");
                String cliente       = str(row, colIndex, "cliente");
                String coordenadas   = str(row, colIndex, "coordenadas");
                String nombreRack    = str(row, colIndex, "nombreRack");
                String ubicacionUr   = str(row, colIndex, "ubicacionUr");
                String urUtilizada   = str(row, colIndex, "urUtilizada");
                String numeroTemporal= str(row, colIndex, "numeroTemporal");
                String hotname       = str(row, colIndex, "hotname");
                String estado        = str(row, colIndex, "estado");
                String flujoAire     = str(row, colIndex, "flujoAire");
                String fuentesPoder  = str(row, colIndex, "fuentesPoder");
                String tiposEnchufe  = str(row, colIndex, "tiposEnchufe");
                String obsEnchufe    = str(row, colIndex, "observacionTipoEnchufe");
                String direccionIp   = str(row, colIndex, "direccionIp");
                String sitio         = str(row, colIndex, "sitio");
                String comentarios   = str(row, colIndex, "comentariosAlarma");
                String ticket        = str(row, colIndex, "ticketRelacion");
                String observaciones = str(row, colIndex, "observaciones");

                // --- Aplicar "N/A" a campos vacíos no únicos ---
                Inventario inv = new Inventario();
                inv.setSala(sala != null           ? sala           : "N/A");
                inv.setTipo(tipo != null           ? tipo           : "N/A");
                inv.setMarca(marca != null         ? marca          : "N/A");
                inv.setModelo(modelo != null       ? modelo         : "N/A");
                inv.setCliente(cliente != null     ? cliente        : "N/A");
                inv.setCoordenadas(coordenadas != null ? coordenadas : "N/A");
                inv.setNombreRack(nombreRack != null ? nombreRack   : "N/A");
                inv.setUbicacionUr(ubicacionUr != null ? ubicacionUr : "N/A");
                inv.setUrUtilizada(urUtilizada != null ? urUtilizada : "N/A");
                inv.setNumeroTemporal(numeroTemporal != null ? numeroTemporal : "N/A");
                inv.setHotname(hotname != null     ? hotname        : "N/A");
                inv.setEstado(estado != null       ? estado         : "N/A");
                inv.setFlujoAire(flujoAire != null ? flujoAire      : "N/A");
                inv.setFuentesPoder(fuentesPoder != null ? fuentesPoder : "N/A");
                inv.setTiposEnchufe(tiposEnchufe != null ? tiposEnchufe : "N/A");
                inv.setObservacionTipoEnchufe(obsEnchufe != null ? obsEnchufe : "N/A");
                inv.setDireccionIp(direccionIp != null ? direccionIp : "N/A");
                inv.setSitio(sitio != null         ? sitio          : "N/A");
                inv.setComentariosAlarma(comentarios != null ? comentarios : "N/A");
                inv.setTicketRelacion(ticket != null ? ticket       : "N/A");
                inv.setObservaciones(observaciones != null ? observaciones : "N/A");
                inv.setFechaAlarma(getCellDateValue(cell(row, colIndex, "fechaAlarma")));
                inv.setAlarmaHardware(getCellBooleanValue(cell(row, colIndex, "alarmaHardware")));
                inv.setAlarmaVentilador(getCellBooleanValue(cell(row, colIndex, "alarmaVentilador")));
                inv.setAlarmaFuentePoder(getCellBooleanValue(cell(row, colIndex, "alarmaFuentePoder")));
                inv.setAlarmaHdd(getCellBooleanValue(cell(row, colIndex, "alarmaHdd")));
                inv.setPesoEquipoKg(getCellBigDecimalValue(cell(row, colIndex, "pesoEquipoKg")));
                inv.setPotenciaConsumoWatts(getCellBigDecimalValue(cell(row, colIndex, "potenciaConsumoWatts")));

                // Campos únicos: placeholder único por fila si están vacíos
                inv.setNumeroSerie(numeroSerie != null ? numeroSerie : "N/A-F" + i);
                inv.setTag(tag != null             ? tag            : "N/A-F" + i);

                inventarios.add(inv);
            }

            // Log de diagnóstico en consola
            System.out.println("[IMPORT] Hoja: " + sheet.getSheetName()
                    + " | getLastRowNum()=" + totalFilasHoja
                    + " | getPhysicalNumberOfRows()=" + sheet.getPhysicalNumberOfRows()
                    + " | encabezados=" + (usoEncabezados ? colIndex.keySet() : "FALLBACK_FIJO")
                    + " | filasConDatos=" + filasConDatos
                    + " | parsedItems=" + inventarios.size());

            String columnasMapeadas = usoEncabezados ? colIndex.toString() : "índices fijos";
            return new ImportResult(inventarios, totalFilasHoja, filasConDatos,
                    usoEncabezados, columnasMapeadas);
        }
    }

    private static Cell cell(Row row, Map<String, Integer> colIndex, String field) {
        Integer idx = colIndex.get(field);
        return (idx != null) ? row.getCell(idx) : null;
    }

    private static String str(Row row, Map<String, Integer> colIndex, String field) {
        return getCellValue(cell(row, colIndex, field));
    }

    /**
     * Obtiene valor de celda como String. Retorna null para celdas vacías o con solo espacios.
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String s = cell.getStringCellValue().trim();
                return s.isEmpty() ? null : s;   // <-- cadena vacía = null
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double d = cell.getNumericCellValue();
                    // Evitar ".0" al final si es entero
                    return (d == Math.floor(d) && !Double.isInfinite(d))
                            ? String.valueOf((long) d)
                            : String.valueOf(d);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    String fs = cell.getStringCellValue().trim();
                    return fs.isEmpty() ? null : fs;
                } catch (Exception e) {
                    try {
                        double fd = cell.getNumericCellValue();
                        return (fd == Math.floor(fd) && !Double.isInfinite(fd))
                                ? String.valueOf((long) fd)
                                : String.valueOf(fd);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            case BLANK:
            default:
                return null;
        }
    }

    /**
     * Obtiene valor de celda como LocalDate
     */
    private static LocalDate getCellDateValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }
                return null;
            case STRING:
                String str = cell.getStringCellValue().trim();
                if (!str.isEmpty()) {
                    try {
                        return LocalDate.parse(str); // formato ISO: yyyy-MM-dd
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            default:
                return null;
        }
    }

    /**
     * Obtiene valor de celda como Boolean
     */
    private static Boolean getCellBooleanValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case STRING:
                String val = cell.getStringCellValue().toLowerCase();
                return val.equals("sí") || val.equals("si") || val.equals("true") || val.equals("1");
            case NUMERIC:
                return cell.getNumericCellValue() != 0;
            default:
                return null;
        }
    }

    /**
     * Obtiene valor de celda como BigDecimal
     */
    private static BigDecimal getCellBigDecimalValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return new BigDecimal(cell.getNumericCellValue());
            case STRING:
                try {
                    return new BigDecimal(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
