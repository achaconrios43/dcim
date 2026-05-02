package com.example.dcim.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Ejecuta limpieza de datos heredados y auditoría de integridad usando JDBC.
 * Modos soportados: clean, audit, all.
 */
public final class DatabaseMaintenanceTool {

    private static final Path APP_PROPERTIES = Paths.get("src", "main", "resources", "application.properties");

    private DatabaseMaintenanceTool() {
    }

    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0].trim().toLowerCase() : "all";
        Properties properties = loadDatasourceProperties();

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("spring.datasource.url"),
                properties.getProperty("spring.datasource.username"),
                properties.getProperty("spring.datasource.password", ""))) {
            connection.setAutoCommit(false);

            if ("clean".equals(mode) || "all".equals(mode)) {
                runCleanup(connection);
                connection.commit();
                System.out.println("Limpieza aplicada correctamente.");
            }

            if ("audit".equals(mode) || "all".equals(mode)) {
                runAudit(connection);
            }
        }
    }

    private static Properties loadDatasourceProperties() throws IOException {
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(APP_PROPERTIES, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static void runCleanup(Connection connection) throws SQLException {
        int updatedRows = 0;

        updatedRows += ensureRelationshipSchema(connection);

        updatedRows += updateUsuario(connection, "UPDATE usuario SET email = 'admin@dcim.com' WHERE rut = '11111111-1' AND email = 'admin@clases.com'");

        updatedRows += updateIngreso(connection, "CRQ0001234", "Arturo Chacón", "Carlos González", "Técnico Senior", "Paulo Hernandez", "Sala A1", "Rack-A1-15", "Mantenimiento preventivo de servidores críticos del banco", "Revisión completa de servidores HP DL380, actualización de firmware y verificación de conectividad de red principal", "Banco Santander");
        updatedRows += updateIngreso(connection, "INC0005678", "Arturo Chacón", "María Rodríguez", "Especialista en Redes", "Arturo Chacón", "Sala B2", "Rack-B2-08", "Resolución urgente de incidencia en core de red", "Diagnóstico y reparación de switch Cisco Catalyst 9500, reemplazo de módulo defectuoso y restauración de servicios críticos", "Telefónica Chile");
        updatedRows += updateIngreso(connection, "VIS0009876", "Marcelo Robles", "José Martínez", "Jefe de Proyecto", "Facilities", "Área General", "Área General", "Inspección programada de equipos de climatización", "Verificación integral del sistema HVAC, medición de temperaturas, revisión de filtros y validación de alarmas del sistema de climatización", "Entel");
        updatedRows += updateIngreso(connection, "CRQ0002345", "Rodrigo Aguilera", "Ana López", "Analista de Seguridad", "Rodrigo Aguilera", "Sala D1", "Rack-D1-22", "Actualización crítica de sistemas de seguridad", "Implementación de parches de seguridad en firewalls Palo Alto, actualización de políticas y verificación de logs de seguridad", "BCI");
        updatedRows += updateIngreso(connection, "INC0006789", "Paulo Hernandez", "Diego Torres", "Técnico Eléctrico", "No Autorizado", "Sala UPS", "UPS-Central", "Emergencia crítica en sistema UPS principal", "Atención de emergencia en UPS APC Symmetra 200kVA, diagnóstico de falla, reemplazo de baterías defectuosas y restauración del respaldo eléctrico crítico", "Metro de Santiago");

        updatedRows += updateGestion(connection, 1L, "Paulo Hernandez, Rodrigo Aguilera", "Arturo Chacón", "Mantenimiento preventivo servidor crítico Banco Estado - Sala CPD");
        updatedRows += updateGestion(connection, 2L, "Arturo Chacón", "Marcelo Robles", "Resolución incidencia red core Telefónica - Sala CrossConnect");
        updatedRows += updateGestion(connection, 3L, "Facilities, Rodrigo Aguilera", "Paulo Hernandez", "Instalación nuevo switch Cisco 9500 - Sala Datos");
        updatedRows += updateGestion(connection, 4L, "Paulo Hernandez", "Rodrigo Aguilera", "Actualización firmware equipos HPE - Sala Mainframe");
        updatedRows += updateGestion(connection, 5L, "Facilities", "Arturo Chacón", "Reparación sistema climatización CRAC - Sala CPD1");
        updatedRows += updateGestion(connection, 6L, "Paulo Hernandez, Rodrigo Aguilera, Facilities", "Arturo Chacón", "Proyecto migración servidores críticos - Múltiples salas");

        updatedRows += removeDuplicateInventarioIndexes(connection);
        updatedRows += syncUserRoleRelations(connection);
        updatedRows += syncModuleRelations(connection);

        System.out.println("Filas/ajustes aplicados: " + updatedRows);
    }

    private static int ensureRelationshipSchema(Connection connection) throws SQLException {
        int changes = 0;

        changes += executeDdl(connection, """
                CREATE TABLE IF NOT EXISTS tipo_usuario (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    codigo VARCHAR(30) NOT NULL,
                    descripcion VARCHAR(120) DEFAULT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_tipo_usuario_codigo (codigo)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        changes += executeDdl(connection, """
                CREATE TABLE IF NOT EXISTS usuario_tipo_usuario (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    usuario_id BIGINT NOT NULL,
                    tipo_usuario_id BIGINT NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_usuario_tipo (usuario_id, tipo_usuario_id),
                    CONSTRAINT fk_usuario_tipo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
                    CONSTRAINT fk_usuario_tipo_tipo FOREIGN KEY (tipo_usuario_id) REFERENCES tipo_usuario(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        changes += executeDdl(connection, """
                CREATE TABLE IF NOT EXISTS usuario_ingresoap_rel (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    usuario_id BIGINT NOT NULL,
                    ingresoap_id BIGINT NOT NULL,
                    tipo_relacion VARCHAR(40) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_usuario_ingresoap_rel (usuario_id, ingresoap_id, tipo_relacion),
                    CONSTRAINT fk_uir_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
                    CONSTRAINT fk_uir_ingreso FOREIGN KEY (ingresoap_id) REFERENCES ingresoap(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        changes += executeDdl(connection, """
                CREATE TABLE IF NOT EXISTS usuario_gestion_rel (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    usuario_id BIGINT NOT NULL,
                    gestion_acceso_id BIGINT NOT NULL,
                    tipo_relacion VARCHAR(40) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_usuario_gestion_rel (usuario_id, gestion_acceso_id, tipo_relacion),
                    CONSTRAINT fk_ugr_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
                    CONSTRAINT fk_ugr_gestion FOREIGN KEY (gestion_acceso_id) REFERENCES gestion_acceso(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        changes += executeDdl(connection, """
                CREATE TABLE IF NOT EXISTS usuario_inventario_rel (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    usuario_id BIGINT NOT NULL,
                    inventario_id BIGINT NOT NULL,
                    tipo_relacion VARCHAR(40) NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_usuario_inventario_rel (usuario_id, inventario_id, tipo_relacion),
                    CONSTRAINT fk_uivr_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
                    CONSTRAINT fk_uivr_inventario FOREIGN KEY (inventario_id) REFERENCES inventario(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

        changes += executeDdl(connection, """
                CREATE OR REPLACE VIEW vw_usuario_modulo_relaciones AS
                SELECT u.id AS usuario_id,
                       u.email,
                       CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo,
                       tu.codigo AS tipo_usuario,
                       'INGRESOAP' AS modulo,
                       r.tipo_relacion,
                       r.ingresoap_id AS entidad_id
                  FROM usuario u
                  JOIN usuario_tipo_usuario utu ON utu.usuario_id = u.id
                  JOIN tipo_usuario tu ON tu.id = utu.tipo_usuario_id
                  JOIN usuario_ingresoap_rel r ON r.usuario_id = u.id
                UNION ALL
                SELECT u.id AS usuario_id,
                       u.email,
                       CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo,
                       tu.codigo AS tipo_usuario,
                       'GESTION_ACCESO' AS modulo,
                       r.tipo_relacion,
                       r.gestion_acceso_id AS entidad_id
                  FROM usuario u
                  JOIN usuario_tipo_usuario utu ON utu.usuario_id = u.id
                  JOIN tipo_usuario tu ON tu.id = utu.tipo_usuario_id
                  JOIN usuario_gestion_rel r ON r.usuario_id = u.id
                UNION ALL
                SELECT u.id AS usuario_id,
                       u.email,
                       CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo,
                       tu.codigo AS tipo_usuario,
                       'INVENTARIO' AS modulo,
                       r.tipo_relacion,
                       r.inventario_id AS entidad_id
                  FROM usuario u
                  JOIN usuario_tipo_usuario utu ON utu.usuario_id = u.id
                  JOIN tipo_usuario tu ON tu.id = utu.tipo_usuario_id
                  JOIN usuario_inventario_rel r ON r.usuario_id = u.id
                """);

        return changes;
    }

    private static int executeDdl(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return 1;
        }
    }

    private static int syncUserRoleRelations(Connection connection) throws SQLException {
        int changes = 0;

        changes += upsertTipoUsuario(connection, "ADMIN", "Administrador del sistema");
        changes += upsertTipoUsuario(connection, "USER", "Usuario técnico");

        try (Statement deleteStatement = connection.createStatement()) {
            changes += deleteStatement.executeUpdate("DELETE FROM usuario_tipo_usuario");
        }

        String insertSql = """
                INSERT INTO usuario_tipo_usuario (usuario_id, tipo_usuario_id)
                SELECT u.id, tu.id
                  FROM usuario u
                  JOIN tipo_usuario tu ON tu.codigo = UPPER(COALESCE(u.rol, 'USER'))
                """;

        try (Statement statement = connection.createStatement()) {
            changes += statement.executeUpdate(insertSql);
        }

        return changes;
    }

    private static int upsertTipoUsuario(Connection connection, String codigo, String descripcion) throws SQLException {
        String sql = """
                INSERT INTO tipo_usuario (codigo, descripcion)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, codigo);
            statement.setString(2, descripcion);
            return statement.executeUpdate();
        }
    }

    private static int syncModuleRelations(Connection connection) throws SQLException {
        int changes = 0;

        try (Statement statement = connection.createStatement()) {
            changes += statement.executeUpdate("DELETE FROM usuario_ingresoap_rel");
            changes += statement.executeUpdate("DELETE FROM usuario_gestion_rel");
            changes += statement.executeUpdate("DELETE FROM usuario_inventario_rel");
        }

        changes += insertIngresoRel(connection, "nombre_usuario", "REGISTRADOR");
        changes += insertIngresoRel(connection, "aprobador", "APROBADOR");
        changes += insertGestionRel(connection, "usuario_ingresa", "REGISTRADOR");
        changes += insertGestionPendingApprovers(connection);

        return changes;
    }

    private static int insertIngresoRel(Connection connection, String sourceField, String relationType) throws SQLException {
        String sql = """
                INSERT IGNORE INTO usuario_ingresoap_rel (usuario_id, ingresoap_id, tipo_relacion)
                SELECT u.id, i.id, ?
                  FROM ingresoap i
                  JOIN usuario u
                    ON LOWER(TRIM(CONCAT(u.nombre, ' ', u.apellido))) = LOWER(TRIM(
                        CASE ?
                            WHEN 'nombre_usuario' THEN i.nombre_usuario
                            ELSE i.aprobador
                        END
                    ))
                 WHERE CASE ?
                        WHEN 'nombre_usuario' THEN i.nombre_usuario IS NOT NULL AND TRIM(i.nombre_usuario) <> ''
                        ELSE i.aprobador IS NOT NULL AND TRIM(i.aprobador) <> ''
                       END
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, relationType);
            statement.setString(2, sourceField);
            statement.setString(3, sourceField);
            return statement.executeUpdate();
        }
    }

    private static int insertGestionRel(Connection connection, String sourceField, String relationType) throws SQLException {
        String sql = """
                INSERT IGNORE INTO usuario_gestion_rel (usuario_id, gestion_acceso_id, tipo_relacion)
                SELECT u.id, g.id, ?
                  FROM gestion_acceso g
                  JOIN usuario u
                    ON LOWER(TRIM(CONCAT(u.nombre, ' ', u.apellido))) = LOWER(TRIM(g.usuario_ingresa))
                 WHERE g.usuario_ingresa IS NOT NULL
                   AND TRIM(g.usuario_ingresa) <> ''
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, relationType);
            return statement.executeUpdate();
        }
    }

    private static int insertGestionPendingApprovers(Connection connection) throws SQLException {
        int changes = 0;
        String selectSql = "SELECT id, aprobadores_pendientes FROM gestion_acceso WHERE aprobadores_pendientes IS NOT NULL AND TRIM(aprobadores_pendientes) <> ''";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSql)) {
            while (resultSet.next()) {
                long gestionId = resultSet.getLong("id");
                String approvers = resultSet.getString("aprobadores_pendientes");
                if (approvers == null) {
                    continue;
                }

                for (String token : approvers.split(",")) {
                    String normalized = token == null ? "" : token.trim();
                    if (normalized.isEmpty()) {
                        continue;
                    }
                    Long userId = findUserIdByFullName(connection, normalized);
                    if (userId != null) {
                        changes += insertGestionRelationRow(connection, userId, gestionId, "APROBADOR_PENDIENTE");
                    }
                }
            }
        }
        return changes;
    }

    private static Long findUserIdByFullName(Connection connection, String fullName) throws SQLException {
        String sql = """
                SELECT id
                  FROM usuario
                 WHERE LOWER(TRIM(CONCAT(nombre, ' ', apellido))) = LOWER(TRIM(?))
                 LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        }
        return null;
    }

    private static int insertGestionRelationRow(Connection connection, Long userId, Long gestionId, String relationType) throws SQLException {
        String sql = """
                INSERT IGNORE INTO usuario_gestion_rel (usuario_id, gestion_acceso_id, tipo_relacion)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, gestionId);
            statement.setString(3, relationType);
            return statement.executeUpdate();
        }
    }

    private static int updateUsuario(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    private static int updateIngreso(Connection connection,
                                     String numeroTicket,
                                     String nombreUsuario,
                                     String nombreTecnico,
                                     String cargoTecnico,
                                     String aprobador,
                                     String salaIngresa,
                                     String rackIngresa,
                                     String motivoIngreso,
                                     String actividadRemedy,
                                     String empresaDemandante) throws SQLException {
        String sql = """
                UPDATE ingresoap
                   SET nombre_usuario = ?,
                       nombre_tecnico = ?,
                       cargo_tecnico = ?,
                       aprobador = ?,
                       sala_ingresa = ?,
                       rack_ingresa = ?,
                       motivo_ingreso = ?,
                       actividad_remedy = ?,
                       empresa_demandante = ?
                 WHERE numero_ticket = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombreUsuario);
            statement.setString(2, nombreTecnico);
            statement.setString(3, cargoTecnico);
            statement.setString(4, aprobador);
            statement.setString(5, salaIngresa);
            statement.setString(6, rackIngresa);
            statement.setString(7, motivoIngreso);
            statement.setString(8, actividadRemedy);
            statement.setString(9, empresaDemandante);
            statement.setString(10, numeroTicket);
            return statement.executeUpdate();
        }
    }

    private static int updateGestion(Connection connection,
                                     Long id,
                                     String aprobadoresPendientes,
                                     String usuarioIngresa,
                                     String nombreActividad) throws SQLException {
        String sql = """
                UPDATE gestion_acceso
                   SET aprobadores_pendientes = ?,
                       usuario_ingresa = ?,
                       nombre_actividad = ?
                 WHERE id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, aprobadoresPendientes);
            statement.setString(2, usuarioIngresa);
            statement.setString(3, nombreActividad);
            statement.setLong(4, id);
            return statement.executeUpdate();
        }
    }

    private static int removeDuplicateInventarioIndexes(Connection connection) throws SQLException {
        List<String> duplicatedIndexes = new ArrayList<>();
        String query = """
                SELECT index_name
                  FROM information_schema.statistics
                 WHERE table_schema = DATABASE()
                   AND table_name = 'inventario'
                   AND index_name IN ('UKflqo55bt7et99jx7jq90pi1ce', 'UKtnn8m48p09qbas80uhl8dh0ra')
                """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                duplicatedIndexes.add(resultSet.getString(1));
            }
        }

        int changes = 0;
        for (String indexName : duplicatedIndexes) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("ALTER TABLE inventario DROP INDEX " + indexName);
                changes++;
            }
        }
        return changes;
    }

    private static void runAudit(Connection connection) throws SQLException {
        System.out.println("=== AUDITORIA DCIM ===");
        System.out.println("Usuarios totales: " + scalar(connection, "SELECT COUNT(*) FROM usuario"));
        System.out.println("Ingresos totales: " + scalar(connection, "SELECT COUNT(*) FROM ingresoap"));
        System.out.println("Gestiones totales: " + scalar(connection, "SELECT COUNT(*) FROM gestion_acceso"));
        System.out.println("Inventario total: " + scalar(connection, "SELECT COUNT(*) FROM inventario"));
        System.out.println("Emails duplicados: " + scalar(connection, "SELECT COUNT(*) FROM (SELECT email FROM usuario GROUP BY email HAVING COUNT(*) > 1) t"));
        System.out.println("RUT duplicados: " + scalar(connection, "SELECT COUNT(*) FROM (SELECT rut FROM usuario GROUP BY rut HAVING COUNT(*) > 1) t"));
        System.out.println("Serie duplicada inventario: " + scalar(connection, "SELECT COUNT(*) FROM (SELECT numero_serie FROM inventario WHERE numero_serie IS NOT NULL AND numero_serie <> '' GROUP BY numero_serie HAVING COUNT(*) > 1) t"));
        System.out.println("Tag duplicado inventario: " + scalar(connection, "SELECT COUNT(*) FROM (SELECT tag FROM inventario WHERE tag IS NOT NULL AND tag <> '' GROUP BY tag HAVING COUNT(*) > 1) t"));
        System.out.println("Textos con '??' en ingresoap: " + scalar(connection, "SELECT COUNT(*) FROM ingresoap WHERE actividad_remedy LIKE '%??%' OR motivo_ingreso LIKE '%??%' OR nombre_usuario LIKE '%??%' OR nombre_tecnico LIKE '%??%' OR cargo_tecnico LIKE '%??%' OR empresa_demandante LIKE '%??%' OR aprobador LIKE '%??%' OR sala_ingresa LIKE '%??%' OR rack_ingresa LIKE '%??%' OR sala_remedy LIKE '%??%'"));
        System.out.println("Textos con '??' en gestion_acceso: " + scalar(connection, "SELECT COUNT(*) FROM gestion_acceso WHERE aprobadores_pendientes LIKE '%??%' OR usuario_ingresa LIKE '%??%' OR nombre_actividad LIKE '%??%'"));
        System.out.println("Emails legados clases: " + scalar(connection, "SELECT COUNT(*) FROM usuario WHERE email LIKE '%@clases.%'"));
        System.out.println("Indices unicos duplicados en inventario: " + scalar(connection, "SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'inventario' AND index_name IN ('UKflqo55bt7et99jx7jq90pi1ce', 'UKtnn8m48p09qbas80uhl8dh0ra')"));
        System.out.println("Tipos de usuario: " + scalar(connection, "SELECT COUNT(*) FROM tipo_usuario"));
        System.out.println("Relaciones usuario-tipo: " + scalar(connection, "SELECT COUNT(*) FROM usuario_tipo_usuario"));
        System.out.println("Relaciones usuario-ingresoap: " + scalar(connection, "SELECT COUNT(*) FROM usuario_ingresoap_rel"));
        System.out.println("Relaciones usuario-gestion: " + scalar(connection, "SELECT COUNT(*) FROM usuario_gestion_rel"));
        System.out.println("Relaciones usuario-inventario: " + scalar(connection, "SELECT COUNT(*) FROM usuario_inventario_rel"));
        System.out.println("Filas vista unificada: " + scalar(connection, "SELECT COUNT(*) FROM vw_usuario_modulo_relaciones"));
    }

    private static long scalar(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }
}