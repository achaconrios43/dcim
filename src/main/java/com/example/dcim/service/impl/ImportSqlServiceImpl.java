package com.example.dcim.service.impl;

/**
 * Servicio para regenerar el archivo import.sql con el estado actual de los
 * usuarios de prueba. Este archivo se usa solo como semilla documental o de
 * apoyo en desarrollo; la aplicación trabaja contra MySQL real.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.dcim.entity.Usuario;
import com.example.dcim.service.ImportSqlService;
import com.example.dcim.service.UsuarioService;

@Service
public class ImportSqlServiceImpl implements ImportSqlService {
    
    @Autowired
    @Lazy
    private UsuarioService usuarioService;
    
    private static final String IMPORT_SQL_PATH = "src/main/resources/import.sql";
    
    /**
     * MÉTODO PRINCIPAL DE REGENERACIÓN
     * ────────────────────────────────────────────────────────────────
     * Regenera completamente el archivo import.sql con datos actuales
     * 
     * PROCESO:
     * 1. Genera INSERT statements para usuarios
     * 2. Genera INSERT statements para registros de ingreso
     * 3. Escribe todo al archivo import.sql
     * 
     * USO: Se llama automáticamente después de operaciones CRUD
     */
    @Override
    public void regenerateImportSql() {
        List<String> insertStatements = generateUserInserts();
        insertStatements.addAll(generateIngresoInserts());
        writeImportSqlFile(insertStatements);
    }
    
    /**
     * HOOK PARA OPERACIONES DE USUARIO
     * ────────────────────────────────────────────────────────────────
     * Se ejecuta automáticamente después de crear/actualizar/eliminar usuarios
     * Mantiene sincronizados los datos de prueba
     */
    @Override
    public void updateImportSqlAfterUserOperation() {
        regenerateImportSql();
    }
    
    /**
     * GENERADOR DE DATOS DE USUARIOS
     * ────────────────────────────────────────────────────────────────
     * Crea INSERT statements para todos los usuarios del sistema
     * 
     * CARACTERÍSTICAS:
     * - Consulta usuarios actuales de la base de datos
     * - Escapa caracteres especiales en SQL
     * - Incluye comentarios descriptivos
     * - Maneja timestamps automáticos
     */
    @Override
    public List<String> generateUserInserts() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        List<String> insertStatements = new ArrayList<>();
        
        insertStatements.add("-- Inserción de datos para tabla de usuarios");
        insertStatements.add("-- Estructura: rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at");
        insertStatements.add("");
        insertStatements.add("-- Usuarios del sistema Data Center Management");
        
        for (Usuario usuario : usuarios) {
            String insert = String.format(
                "INSERT INTO usuario (rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at) " +
                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);",
                usuario.getRut(),
                escapeSql(usuario.getNombre()),
                escapeSql(usuario.getApellido()),
                escapeSql(usuario.getEmail()),
                escapeSql(usuario.getPassword()),
                usuario.getRol()
            );
            insertStatements.add(insert);
        }
        
        return insertStatements;
    }
    
    /**
     * GENERADOR DE DATOS DE EJEMPLO PARA INGRESOS
     * ────────────────────────────────────────────────────────────────
     * Crea registros de ejemplo para pruebas del sistema de ingresos
     * 
     * DATOS GENERADOS:
     * - 5 registros de técnicos diferentes
     * - Diferentes tipos de tickets (CRQ, INC, VISITA)
     * - Diferentes empresas y aprobadores
     * - Horarios realistas de trabajo
     * - Motivos variados de ingreso
     * 
     * IMPORTANTE: Estos datos DEBEN coincidir con la estructura 
     * de la entidad IngresoAP (24 campos)
     */
    private List<String> generateIngresoInserts() {
        List<String> insertStatements = new ArrayList<>();
        
        insertStatements.add("");
        insertStatements.add("-- ═══════════════════════════════════════════════════════════════");
        insertStatements.add("-- DATOS DE EJEMPLO PARA REGISTROS DE INGRESO AL DATA CENTER");
        insertStatements.add("-- ═══════════════════════════════════════════════════════════════");
        insertStatements.add("-- ESTRUCTURA ACTUALIZADA - 24 campos sincronizados con entidad IngresoAP");
        insertStatements.add("-- Campos: turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino,");
        insertStatements.add("--         nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista,");
        insertStatements.add("--         cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador,");
        insertStatements.add("--         escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa,");
        insertStatements.add("--         actividad_remedy, fecha_registro, activo");
        insertStatements.add("");
        
        // Técnico 1: Carlos González - Mantenimiento CRQ
        insertStatements.add("INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Arturo Chacón', '2024-11-15', '08:30:00', '2024-11-15', '17:30:00', 'Carlos González', '12.345.678-9', 'Banco Santander', 'DataCenter Solutions', 'Técnico Senior', 'Salas TI', 'CRQ', 'CRQ0001234', 'Paulo Hernandez', 'Operador de Turno', 'Mantenimiento preventivo de servidores críticos del banco', 'GD-2024-001', 'Sala A1', 'Rack-A1-15', 'Revisión completa de servidores HP DL380, actualización de firmware y verificación de conectividad de red principal', CURRENT_TIMESTAMP, TRUE);");
        
        // Técnico 2: María Rodríguez - Incidencia INC
        insertStatements.add("INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Arturo Chacón', '2024-11-15', '09:15:00', '2024-11-15', '16:45:00', 'María Rodríguez', '98.765.432-1', 'Telefónica Chile', 'Network Pro', 'Especialista en Redes', 'Salas de RED', 'INC', 'INC0005678', 'Arturo Chacón', 'Guardia', 'Resolución urgente de incidencia en core de red', 'GD-2024-002', 'Sala B2', 'Rack-B2-08', 'Diagnóstico y reparación de switch Cisco Catalyst 9500, reemplazo de módulo defectuoso y restauración de servicios críticos', CURRENT_TIMESTAMP, TRUE);");
        
        // Técnico 3: José Martínez - Visita Inspectiva
        insertStatements.add("INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('PM', 'Marcelo Robles', '2024-11-14', '14:20:00', '2024-11-14', '18:00:00', 'José Martínez', '11.223.344-5', 'Entel', 'Server Management', 'Jefe de Proyecto', 'Salas TI', 'Visita Inspectiva', 'VIS0009876', 'Facilities', 'Personal de Facilities', 'Inspección programada de equipos de climatización', NULL, 'Sala C3', 'Área General', 'Verificación integral del sistema HVAC, medición de temperaturas, revisión de filtros y validación de alarmas del sistema de climatización', CURRENT_TIMESTAMP, TRUE);");
        
        // Técnico 4: Ana López - CRQ Seguridad
        insertStatements.add("INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Rodrigo Aguilera', '2024-11-14', '10:45:00', '2024-11-14', '15:30:00', 'Ana López', '55.667.788-9', 'BCI', 'Security Systems', 'Analista de Seguridad', 'Salas TI', 'CRQ', 'CRQ0002345', 'Rodrigo Aguilera', 'Operador de Turno', 'Actualización crítica de sistemas de seguridad', 'GD-2024-003', 'Sala D1', 'Rack-D1-22', 'Implementación de parches de seguridad en firewalls Palo Alto, actualización de políticas y verificación de logs de seguridad', CURRENT_TIMESTAMP, TRUE);");
        
        // Técnico 5: Diego Torres - INC Emergencia
        insertStatements.add("INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Paulo Hernandez', '2024-11-13', '07:00:00', '2024-11-13', '12:15:00', 'Diego Torres', '66.778.899-0', 'Metro de Santiago', 'Power Solutions', 'Técnico Eléctrico', 'Salas TI', 'INC', 'INC0006789', 'No Autorizado', 'Guardia', 'Emergencia crítica en sistema UPS principal', 'EMER-001', 'Sala UPS', 'UPS-Central', 'Atención de emergencia en UPS APC Symmetra 200kVA, diagnóstico de falla, reemplazo de baterías defectuosas y restauración del respaldo eléctrico crítico', CURRENT_TIMESTAMP, TRUE);");
        
        insertStatements.add("");
        insertStatements.add("-- ═══════════════════════════════════════════════════════════════");
        insertStatements.add("-- RESUMEN DE DATOS DE EJEMPLO:");
        insertStatements.add("-- • 5 registros de técnicos con diferentes perfiles");
        insertStatements.add("-- • 3 tipos de tickets: CRQ (2), INC (2), Visita Inspectiva (1)");
        insertStatements.add("-- • Diferentes empresas demandantes y contratistas");
        insertStatements.add("-- • Variedad de aprobadores y escoltas");
        insertStatements.add("-- • Actividades detalladas y realistas");
        insertStatements.add("-- • Todos los campos mapeados correctamente a la entidad");
        insertStatements.add("-- ═══════════════════════════════════════════════════════════════");
        
        return insertStatements;
    }
    
    /**
     * ESCRITOR DE ARCHIVO IMPORT.SQL
     * ────────────────────────────────────────────────────────────────
     * Escribe todos los INSERT statements al archivo import.sql
     * 
     * CARACTERÍSTICAS:
     * - Sobrescribe el archivo existente
     * - Maneja excepciones de E/S
     * - Proporciona feedback del resultado
     * - Formato limpio con saltos de línea
     */
    @Override
    public void writeImportSqlFile(List<String> insertStatements) {
        try (FileWriter writer = new FileWriter(IMPORT_SQL_PATH)) {
            for (String statement : insertStatements) {
                writer.write(statement + "\n");
            }
            System.out.println("✅ Archivo import.sql actualizado correctamente");
            System.out.println("📊 Total de statements generados: " + insertStatements.size());
        } catch (IOException e) {
            System.err.println("❌ Error al actualizar import.sql: " + e.getMessage());
        }
    }
    
    /**
     * UTILIDAD DE ESCAPE SQL
     * ────────────────────────────────────────────────────────────────
     * Escapa caracteres especiales para prevenir errores SQL
     * 
     * SEGURIDAD:
     * - Previene inyección SQL básica
     * - Maneja comillas simples
     * - Protege valores nulos
     * 
     * EJEMPLO: "O'Connor" -> "O''Connor"
     */
    private String escapeSql(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("'", "''");
    }
}