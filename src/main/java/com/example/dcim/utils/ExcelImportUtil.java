package com.example.dcim.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.example.dcim.entity.Inventario;

public class ExcelImportUtil {

    /**
     * Importa datos de un archivo Excel y retorna lista de Inventario
     * @param file archivo Excel (.xlsx)
     * @return lista de Inventario
     * @throws IOException si hay error al leer el archivo
     */
    public static List<Inventario> importarExcel(MultipartFile file) throws IOException {
        List<Inventario> inventarios = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Primera hoja

            // Mapeo de columnas (ajusta según tu Excel)
            int colSala = 0;
            int colTipo = 1;
            int colMarca = 2;
            int colModelo = 3;
            int colNumeroSerie = 4;
            int colTag = 5;
            int colCliente = 6;
            int colCoordenadas = 7;
            int colNombreRack = 8;
            int colUbicacionUr = 9;
            int colUrUtilizada = 10;
            int colNumeroTemporal = 11;
            int colHotname = 12;
            int colEstado = 13;
            int colFechaAlarma = 14;
            int colAlarmaHardware = 15;
            int colAlarmaVentilador = 16;
            int colAlarmaFuentePoder = 17;
            int colAlarmaHdd = 18;
            int colComentariosAlarma = 19;
            int colTicketRelacion = 20;
            int colObservaciones = 21;
            int colFlujoAire = 22;
            int colPesoEquipo = 23;
            int colFuentesPoder = 24;
            int colTiposEnchufe = 25;
            int colObservacionTipoEnchufe = 26;
            int colPotenciaConsumo = 27;
            int colDireccionIp = 28;
            int colSitio = 29;

            // Iterar filas (comenzar desde fila 1, omitir encabezados)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Inventario inventario = new Inventario();

                // Leer celdas y asignar valores
                inventario.setSala(getCellValue(row.getCell(colSala)));
                inventario.setTipo(getCellValue(row.getCell(colTipo)));
                inventario.setMarca(getCellValue(row.getCell(colMarca)));
                inventario.setModelo(getCellValue(row.getCell(colModelo)));
                inventario.setNumeroSerie(getCellValue(row.getCell(colNumeroSerie))); // Único
                inventario.setTag(getCellValue(row.getCell(colTag))); // Único
                inventario.setCliente(getCellValue(row.getCell(colCliente)));
                inventario.setCoordenadas(getCellValue(row.getCell(colCoordenadas)));
                inventario.setNombreRack(getCellValue(row.getCell(colNombreRack)));
                inventario.setUbicacionUr(getCellValue(row.getCell(colUbicacionUr)));
                inventario.setUrUtilizada(getCellValue(row.getCell(colUrUtilizada)));
                inventario.setNumeroTemporal(getCellValue(row.getCell(colNumeroTemporal)));
                inventario.setHotname(getCellValue(row.getCell(colHotname)));
                inventario.setEstado(getCellValue(row.getCell(colEstado)));
                inventario.setFechaAlarma(getCellDateValue(row.getCell(colFechaAlarma)));
                inventario.setAlarmaHardware(getCellBooleanValue(row.getCell(colAlarmaHardware)));
                inventario.setAlarmaVentilador(getCellBooleanValue(row.getCell(colAlarmaVentilador)));
                inventario.setAlarmaFuentePoder(getCellBooleanValue(row.getCell(colAlarmaFuentePoder)));
                inventario.setAlarmaHdd(getCellBooleanValue(row.getCell(colAlarmaHdd)));
                inventario.setComentariosAlarma(getCellValue(row.getCell(colComentariosAlarma)));
                inventario.setTicketRelacion(getCellValue(row.getCell(colTicketRelacion)));
                inventario.setObservaciones(getCellValue(row.getCell(colObservaciones)));
                inventario.setFlujoAire(getCellValue(row.getCell(colFlujoAire)));
                inventario.setPesoEquipoKg(getCellBigDecimalValue(row.getCell(colPesoEquipo)));
                inventario.setFuentesPoder(getCellValue(row.getCell(colFuentesPoder)));
                inventario.setTiposEnchufe(getCellValue(row.getCell(colTiposEnchufe)));
                inventario.setObservacionTipoEnchufe(getCellValue(row.getCell(colObservacionTipoEnchufe)));
                inventario.setPotenciaConsumoWatts(getCellBigDecimalValue(row.getCell(colPotenciaConsumo)));
                inventario.setDireccionIp(getCellValue(row.getCell(colDireccionIp)));
                inventario.setSitio(getCellValue(row.getCell(colSitio)));

                // Solo agregar si tiene al menos número de serie o tag
                if (inventario.getNumeroSerie() != null || inventario.getTag() != null) {
                    inventarios.add(inventario);
                }
            }
        }

        return inventarios;
    }

    /**
     * Obtiene valor de celda como String
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    /**
     * Obtiene valor de celda como LocalDate
     */
    private static LocalDate getCellDateValue(Cell cell) {
        if (cell == null) return null;
        
        if (DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
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
