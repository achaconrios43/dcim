-- Inserción de datos para tabla de usuarios
-- Estructura: rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at

-- Usuarios del sistema Data Center Management
INSERT INTO usuario (rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at) VALUES ('15.441.473-8', 'arturo', 'chacon', 'achaconrios@gmail.com', '$2a$10$/X6Y2zeDlVhvtJVDsVI2jeVwXdfh4hGq2n9PTicbWCo9pzAi7hmCy', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO usuario (rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at) VALUES ('11111111-1', 'Administrador', 'Sistema', 'admin@clases.com', '$2a$10$NZq7HFDyeXSLCWXbKPYo/.kDnCajT6TEuoA3m5bbMCaIWh/8cLjDC', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO usuario (rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at) VALUES ('18.052.030-9', 'judith', 'linco espinoza', 'judithlinco@gmail.com', '$2a$10$fk/.8YJEP4iSPqW4l0.V.e.yqScP8E0KJB5OkYEb89nH10as7IVCe', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ═══════════════════════════════════════════════════════════════
-- DATOS DE EJEMPLO PARA REGISTROS DE INGRESO AL DATA CENTER
-- ═══════════════════════════════════════════════════════════════
-- ESTRUCTURA ACTUALIZADA - 24 campos sincronizados con entidad IngresoAP
-- Campos: turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino,
--         nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista,
--         cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador,
--         escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa,
--         actividad_remedy, fecha_registro, activo

INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Arturo Chacón', '2024-11-15', '08:30:00', '2024-11-15', '17:30:00', 'Carlos González', '12.345.678-9', 'Banco Santander', 'DataCenter Solutions', 'Técnico Senior', 'Salas TI', 'CRQ', 'CRQ0001234', 'Paulo Hernandez', 'Operador de Turno', 'Mantenimiento preventivo de servidores críticos del banco', 'GD-2024-001', 'Sala A1', 'Rack-A1-15', 'Revisión completa de servidores HP DL380, actualización de firmware y verificación de conectividad de red principal', CURRENT_TIMESTAMP, TRUE);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Arturo Chacón', '2024-11-15', '09:15:00', '2024-11-15', '16:45:00', 'María Rodríguez', '98.765.432-1', 'Telefónica Chile', 'Network Pro', 'Especialista en Redes', 'Salas de RED', 'INC', 'INC0005678', 'Arturo Chacón', 'Guardia', 'Resolución urgente de incidencia en core de red', 'GD-2024-002', 'Sala B2', 'Rack-B2-08', 'Diagnóstico y reparación de switch Cisco Catalyst 9500, reemplazo de módulo defectuoso y restauración de servicios críticos', CURRENT_TIMESTAMP, TRUE);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('PM', 'Marcelo Robles', '2024-11-14', '14:20:00', '2024-11-14', '18:00:00', 'José Martínez', '11.223.344-5', 'Entel', 'Server Management', 'Jefe de Proyecto', 'Salas TI', 'Visita Inspectiva', 'VIS0009876', 'Facilities', 'Personal de Facilities', 'Inspección programada de equipos de climatización', NULL, 'Sala C3', 'Área General', 'Verificación integral del sistema HVAC, medición de temperaturas, revisión de filtros y validación de alarmas del sistema de climatización', CURRENT_TIMESTAMP, TRUE);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Rodrigo Aguilera', '2024-11-14', '10:45:00', '2024-11-14', '15:30:00', 'Ana López', '55.667.788-9', 'BCI', 'Security Systems', 'Analista de Seguridad', 'Salas TI', 'CRQ', 'CRQ0002345', 'Rodrigo Aguilera', 'Operador de Turno', 'Actualización crítica de sistemas de seguridad', 'GD-2024-003', 'Sala D1', 'Rack-D1-22', 'Implementación de parches de seguridad en firewalls Palo Alto, actualización de políticas y verificación de logs de seguridad', CURRENT_TIMESTAMP, TRUE);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo) VALUES ('AM', 'Paulo Hernandez', '2024-11-13', '07:00:00', '2024-11-13', '12:15:00', 'Diego Torres', '66.778.899-0', 'Metro de Santiago', 'Power Solutions', 'Técnico Eléctrico', 'Salas TI', 'INC', 'INC0006789', 'No Autorizado', 'Guardia', 'Emergencia crítica en sistema UPS principal', 'EMER-001', 'Sala UPS', 'UPS-Central', 'Atención de emergencia en UPS APC Symmetra 200kVA, diagnóstico de falla, reemplazo de baterías defectuosas y restauración del respaldo eléctrico crítico', CURRENT_TIMESTAMP, TRUE);

-- ═══════════════════════════════════════════════════════════════
-- RESUMEN DE DATOS DE EJEMPLO:
-- • 5 registros de técnicos con diferentes perfiles
-- • 3 tipos de tickets: CRQ (2), INC (2), Visita Inspectiva (1)
-- • Diferentes empresas demandantes y contratistas
-- • Variedad de aprobadores y escoltas
-- • Actividades detalladas y realistas
-- • Todos los campos mapeados correctamente a la entidad
-- ═══════════════════════════════════════════════════════════════
