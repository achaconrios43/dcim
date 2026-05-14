-- ============================================================
-- SEED DATA para PostgreSQL en Render
-- Convertido desde dcimdb_export.sql (MySQL)
-- Ejecutar con: docker run --rm -v "C:\Users\achac\Desktop\DCIM:/data" postgres:17 psql "EXTERNAL_URL" -f /data/postgres-seed.sql
-- ============================================================

-- Desactivar restricciones FK temporalmente
SET session_replication_role = replica;

-- ============================================================
-- TABLA: usuario
-- ============================================================
INSERT INTO usuario (id, rut, nombre, apellido, email, password, rol, modulos_permitidos, creat_at, update_at) VALUES
(1, '15.441.473-8', 'arturo',      'chacon',          'achaconrios@gmail.com',  '$2a$10$/X6Y2zeDlVhvtJVDsVI2jeVwXdfh4hGq2n9PTicbWCo9pzAi7hmCy', 'ADMIN', '', '2026-04-22 02:21:08', '2026-04-22 02:21:08'),
(2, '11111111-1',   'Administrador','Sistema',         'admin@dcim.com',          '$2a$10$NZq7HFDyeXSLCWXbKPYo/.kDnCajT6TEuoA3m5bbMCaIWh/8cLjDC', 'ADMIN', '', '2026-04-22 02:21:08', '2026-04-22 02:21:08'),
(3, '18.052.030-9', 'judith',      'linco espinoza',  'judithlinco@gmail.com',   '$2a$10$fk/.8YJEP4iSPqW4l0.V.e.yqScP8E0KJB5OkYEb89nH10as7IVCe', 'USER',  '', '2026-04-22 02:21:08', '2026-04-22 02:21:08');

-- Resetear secuencia para evitar conflictos de ID
ALTER TABLE usuario ALTER COLUMN id RESTART WITH 4;

-- ============================================================
-- TABLA: gestion_acceso
-- (bit(1): 1->true, 0->false)
-- ============================================================
INSERT INTO gestion_acceso (id, aprobadores_pendientes, comentario_final, comentario_inicio, comentario_intermedio, enviado_a_procesos, estado_aprobacion, fecha_cierre_gestion, fecha_envio_procesos, fecha_fin_remedy, fecha_inicio_actividad, fecha_inicio_remedy, fecha_registro, fecha_respuesta_cliente, fecha_termino_actividad, gestion_realizada, hora_cierre_gestion, hora_envio_procesos, hora_fin_remedy, hora_inicio_actividad, hora_inicio_remedy, hora_registro, hora_respuesta_cliente, hora_termino_actividad, nombre_actividad, numero_ticket, respuesta_cliente, sitio, ticket_cerrado, usuario_ingresa) VALUES
(1, 'Paulo Hernandez, Rodrigo Aguilera', NULL, NULL, NULL, NULL, 'Aprobada',          NULL, NULL, NULL, '2026-04-22', NULL, '2026-04-22', NULL, '2026-04-22', true,  NULL, NULL, NULL, '14:00:00', NULL, '08:30:00', NULL, '18:00:00', 'Mantenimiento preventivo servidor crítico Banco Estado - Sala CPD',       'CRQ0012345', NULL, 'DC SAN MARTIN',  NULL, 'Arturo Chacón'),
(2, 'Arturo Chacón',                     NULL, NULL, NULL, NULL, 'Aprobada',          NULL, NULL, NULL, '2026-04-22', NULL, '2026-04-22', NULL, '2026-04-22', true,  NULL, NULL, NULL, '10:00:00', NULL, '09:15:00', NULL, '16:00:00', 'Resolución incidencia red core Telefónica - Sala CrossConnect',            'INC0008765', NULL, 'DC SAN MARTIN',  NULL, 'Marcelo Robles'),
(3, 'Facilities, Rodrigo Aguilera',      NULL, NULL, NULL, NULL, 'Pendiente',         NULL, NULL, NULL, '2026-04-23', NULL, '2026-04-22', NULL, '2026-04-23', false, NULL, NULL, NULL, '08:00:00', NULL, '10:45:00', NULL, '12:00:00', 'Instalación nuevo switch Cisco 9500 - Sala Datos',                         'CRQ0012346', NULL, 'DC SAN MARTIN',  NULL, 'Paulo Hernandez'),
(4, 'Paulo Hernandez',                   NULL, NULL, NULL, NULL, 'Aprobada',          NULL, NULL, NULL, '2026-04-22', NULL, '2026-04-22', NULL, '2026-04-22', true,  NULL, NULL, NULL, '13:00:00', NULL, '07:30:00', NULL, '17:00:00', 'Actualización firmware equipos HPE - Sala Mainframe',                      'CRQ0012347', NULL, 'DC APOQUINDO',   NULL, 'Rodrigo Aguilera'),
(5, 'Facilities',                        NULL, NULL, NULL, NULL, 'Rechazada por NXT', NULL, NULL, NULL, '2026-04-22', NULL, '2026-04-22', NULL, '2026-04-22', true,  NULL, NULL, NULL, '15:00:00', NULL, '11:20:00', NULL, '19:00:00', 'Reparación sistema climatización CRAC - Sala CPD1',                        'INC0008766', NULL, 'DC APOQUINDO',   NULL, 'Arturo Chacón'),
(6, 'Paulo Hernandez, Rodrigo Aguilera, Facilities', NULL, NULL, NULL, NULL, 'Aprobada', NULL, NULL, NULL, '2026-04-21', NULL, '2026-04-20', NULL, '2026-04-27', true, NULL, NULL, NULL, '08:00:00', NULL, '08:00:00', NULL, '20:00:00', 'Proyecto migración servidores críticos - Múltiples salas', 'CRQ0012350', NULL, 'DC SAN MARTIN',  NULL, 'Arturo Chacón');

ALTER TABLE gestion_acceso ALTER COLUMN id RESTART WITH 7;

-- ============================================================
-- TABLA: ingresoap
-- (bit(1): 0->false)
-- ============================================================
INSERT INTO ingresoap (id, actividad_remedy, activo, aprobador, cargo_tecnico, coordenadas_gps, empresa_contratista, empresa_demandante, escolta, fecha_fin_ficticia, fecha_inicio, fecha_registro, fecha_segunda_supervision, fecha_supervision_media, fecha_termino, foto_tecnico, guia_despacho, hora_fin_ficticia, hora_inicio, hora_segunda_supervision, hora_supervision_media, hora_termino, motivo_ingreso, nombre_tecnico, nombre_usuario, numero_ticket, rack_ingresa, rut_tecnico, sala_ingresa, sala_remedy, segunda_supervision_realizada, sitio_ingreso, tipo_ticket, turno) VALUES
(1, 'Revisión completa de servidores HP DL380, actualización de firmware y verificación de conectividad de red principal',                                        false, 'Paulo Hernandez',  'Técnico Senior',       NULL, 'DataCenter Solutions', 'Banco Santander',   'Operador de Turno',    NULL, '2024-11-15', '2026-04-22 02:21:08', NULL, NULL, '2024-11-15', NULL, 'GD-2024-001', NULL, '08:30:00', NULL, NULL, '17:30:00', 'Mantenimiento preventivo de servidores críticos del banco',    'Carlos González',  'Arturo Chacón',   'CRQ0001234', 'Rack-A1-15',   '12.345.678-9', 'Sala A1',      'Salas TI',       NULL, 'DC SAN MARTIN',   'CRQ',             'AM'),
(2, 'Diagnóstico y reparación de switch Cisco Catalyst 9500, reemplazo de módulo defectuoso y restauración de servicios críticos',                               false, 'Arturo Chacón',    'Especialista en Redes',NULL, 'Network Pro',         'Telefónica Chile',  'Guardia',              NULL, '2024-11-15', '2026-04-22 02:21:08', NULL, NULL, '2024-11-15', NULL, 'GD-2024-002', NULL, '09:15:00', NULL, NULL, '16:45:00', 'Resolución urgente de incidencia en core de red',             'María Rodríguez',  'Arturo Chacón',   'INC0005678',  'Rack-B2-08',   '98.765.432-1', 'Sala B2',      'Salas de RED',   NULL, 'DC APOQUINDO',    'INC',             'AM'),
(3, 'Verificación integral del sistema HVAC, medición de temperaturas, revisión de filtros y validación de alarmas del sistema de climatización',                 false, 'Facilities',       'Jefe de Proyecto',     NULL, 'Server Management',   'Entel',             'Personal de Facilities',NULL,'2024-11-14', '2026-04-22 02:21:08', NULL, NULL, '2024-11-14', NULL, NULL,          NULL, '14:20:00', NULL, NULL, '18:00:00', 'Inspección programada de equipos de climatización',           'José Martínez',    'Marcelo Robles',  'VIS0009876',  'Área General',  '11.223.344-5', 'Área General', 'Salas TI',       NULL, 'MC LA FLORIDA',   'Visita Inspectiva','PM'),
(4, 'Implementación de parches de seguridad en firewalls Palo Alto, actualización de políticas y verificación de logs de seguridad',                             false, 'Rodrigo Aguilera', 'Analista de Seguridad',NULL, 'Security Systems',    'BCI',               'Operador de Turno',    NULL, '2024-11-14', '2026-04-22 02:21:08', NULL, NULL, '2024-11-14', NULL, 'GD-2024-003', NULL, '10:45:00', NULL, NULL, '15:30:00', 'Actualización crítica de sistemas de seguridad',              'Ana López',        'Rodrigo Aguilera','CRQ0002345',  'Rack-D1-22',   '55.667.788-9', 'Sala D1',      'Salas TI & RED', NULL, 'MC PROVIDENCIA',  'CRQ',             'AM'),
(5, 'Atención de emergencia en UPS APC Symmetra 200kVA, diagnóstico de falla, reemplazo de baterías defectuosas y restauración del respaldo eléctrico crítico',  false, 'No Autorizado',    'Técnico Eléctrico',    NULL, 'Power Solutions',     'Metro de Santiago', 'Guardia',              NULL, '2024-11-13', '2026-04-22 02:21:08', NULL, NULL, '2024-11-13', NULL, 'EMER-001',    NULL, '07:00:00', NULL, NULL, '12:15:00', 'Emergencia crítica en sistema UPS principal',                 'Diego Torres',     'Paulo Hernandez', 'INC0006789',  'UPS-Central',   '66.778.899-0', 'Sala UPS',     'Salas de RED',   NULL, 'DC SAN MARTIN',   'INC',             'AM');

ALTER TABLE ingresoap ALTER COLUMN id RESTART WITH 6;

-- Reactivar restricciones FK
SET session_replication_role = DEFAULT;

-- Verificación final
SELECT 'usuarios insertados:' AS tabla, COUNT(*) AS total FROM usuario
UNION ALL
SELECT 'gestiones insertadas:', COUNT(*) FROM gestion_acceso
UNION ALL
SELECT 'ingresos insertados:', COUNT(*) FROM ingresoap;
