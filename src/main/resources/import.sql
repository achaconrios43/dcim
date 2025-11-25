-- Inserción de datos para tabla de usuarios
-- Estructura: rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at

-- Usuarios del sistema Data Center Management
INSERT INTO usuario (rut, nombre, apellido, email, password, ubicacion, rol, creat_at, update_at) VALUES ('15.441.473-8', 'arturo', 'chacon', 'achaconrios@gmail.com', 'Ayj05102017', 'DC APOQUINDO', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ═══════════════════════════════════════════════════════════════
-- DATOS DE EJEMPLO PARA REGISTROS DE INGRESO AL DATA CENTER
-- ═══════════════════════════════════════════════════════════════
-- ESTRUCTURA ACTUALIZADA - 25 campos sincronizados con entidad IngresoAP
-- Campos: turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino,
--         nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista,
--         cargo_tecnico, sitio_ingreso, sala_remedy, tipo_ticket, numero_ticket, aprobador,
--         escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa,
--         actividad_remedy, fecha_registro, activo

-- Registros históricos SIN supervisión programada (campos nuevos en NULL)
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sitio_ingreso, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo, fecha_fin_ficticia, hora_fin_ficticia, fecha_supervision_media, hora_supervision_media, segunda_supervision_realizada, fecha_segunda_supervision, hora_segunda_supervision) VALUES ('AM', 'Arturo Chacón', '2024-11-15', '08:30:00', '2024-11-15', '17:30:00', 'Carlos González', '12.345.678-9', 'Banco Santander', 'DataCenter Solutions', 'Técnico Senior', 'DC San Martin', 'Salas TI', 'CRQ', 'CRQ0001234', 'Paulo Hernandez', 'Operador de Turno', 'Mantenimiento preventivo de servidores críticos del banco', 'GD-2024-001', 'CPD, MidRange', 'Rack-A1-15', 'Revisión completa de servidores HP DL380, actualización de firmware y verificación de conectividad de red principal', CURRENT_TIMESTAMP, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sitio_ingreso, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo, fecha_fin_ficticia, hora_fin_ficticia, fecha_supervision_media, hora_supervision_media, segunda_supervision_realizada, fecha_segunda_supervision, hora_segunda_supervision) VALUES ('AM', 'Arturo Chacón', '2024-11-15', '09:15:00', '2024-11-15', '16:45:00', 'María Rodríguez', '98.765.432-1', 'Telefónica Chile', 'Network Pro', 'Especialista en Redes', 'DC Apoquindo', 'Salas de RED', 'INC', 'INC0005678', 'Arturo Chacón', 'Guardia', 'Resolución urgente de incidencia en core de red', 'GD-2024-002', 'Alcatel, Noc1, SDH', 'Rack-B2-08', 'Diagnóstico y reparación de switch Cisco Catalyst 9500, reemplazo de módulo defectuoso y restauración de servicios críticos', CURRENT_TIMESTAMP, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sitio_ingreso, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo, fecha_fin_ficticia, hora_fin_ficticia, fecha_supervision_media, hora_supervision_media, segunda_supervision_realizada, fecha_segunda_supervision, hora_segunda_supervision) VALUES ('PM', 'Marcelo Robles', '2024-11-14', '14:20:00', '2024-11-14', '18:00:00', 'José Martínez', '11.223.344-5', 'Entel', 'Server Management', 'Jefe de Proyecto', 'MC La Florida', 'Salas TI', 'Visita Inspectiva', 'VIS0009876', 'Facilities', 'Personal de Facilities', 'Inspección programada de equipos de climatización', NULL, 'Comunicaciones, Mundo', 'Área General', 'Verificación integral del sistema HVAC, medición de temperaturas, revisión de filtros y validación de alarmas del sistema de climatización', CURRENT_TIMESTAMP, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sitio_ingreso, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo, fecha_fin_ficticia, hora_fin_ficticia, fecha_supervision_media, hora_supervision_media, segunda_supervision_realizada, fecha_segunda_supervision, hora_segunda_supervision) VALUES ('AM', 'Rodrigo Aguilera', '2024-11-14', '10:45:00', '2024-11-14', '15:30:00', 'Ana López', '55.667.788-9', 'BCI', 'Security Systems', 'Analista de Seguridad', 'MC Providencia', 'Salas TI & RED', 'CRQ', 'CRQ0002345', 'Rodrigo Aguilera', 'Operador de Turno', 'Actualización crítica de sistemas de seguridad', 'GD-2024-003', 'CPD, Reso, Conmutación, CrossConnect', 'Rack-D1-22', 'Implementación de parches de seguridad en firewalls Palo Alto, actualización de políticas y verificación de logs de seguridad', CURRENT_TIMESTAMP, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO ingresoap (turno, nombre_usuario, fecha_inicio, hora_inicio, fecha_termino, hora_termino, nombre_tecnico, rut_tecnico, empresa_demandante, empresa_contratista, cargo_tecnico, sitio_ingreso, sala_remedy, tipo_ticket, numero_ticket, aprobador, escolta, motivo_ingreso, guia_despacho, sala_ingresa, rack_ingresa, actividad_remedy, fecha_registro, activo, fecha_fin_ficticia, hora_fin_ficticia, fecha_supervision_media, hora_supervision_media, segunda_supervision_realizada, fecha_segunda_supervision, hora_segunda_supervision) VALUES ('AM', 'Paulo Hernandez', '2024-11-13', '07:00:00', '2024-11-13', '12:15:00', 'Diego Torres', '66.778.899-0', 'Metro de Santiago', 'Power Solutions', 'Técnico Eléctrico', 'DC San Martin', 'Salas de RED', 'INC', 'INC0006789', 'No Autorizado', 'Guardia', 'Emergencia crítica en sistema UPS principal', 'EMER-001', 'Housing, Multiportadores, Principal 9', 'UPS-Central', 'Atención de emergencia en UPS APC Symmetra 200kVA, diagnóstico de falla, reemplazo de baterías defectuosas y restauración del respaldo eléctrico crítico', CURRENT_TIMESTAMP, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ═══════════════════════════════════════════════════════════════
-- RESUMEN DE DATOS DE EJEMPLO:
-- • 5 registros de técnicos con diferentes perfiles
-- • 3 tipos de tickets: CRQ (2), INC (2), Visita Inspectiva (1)
-- • Diferentes empresas demandantes y contratistas
-- • Variedad de aprobadores y escoltas
-- • Actividades detalladas y realistas
-- • Todos los campos mapeados correctamente a la entidad
-- ═══════════════════════════════════════════════════════════════

-- ═══════════════════════════════════════════════════════════════
-- DATOS DE EJEMPLO PARA GESTIÓN DE ACCESOS
-- ═══════════════════════════════════════════════════════════════
-- ESTRUCTURA: fecha_registro, hora_registro, usuario_ingresa, numero_ticket,
--             fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad,
--             fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes,
--             gestion_realizada, estado_aprobacion, sitio

-- Gestiones del día actual - DC San Martin
INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '08:30:00', 'Arturo Chacón', 'CRQ0012345', CURRENT_DATE, '14:00:00', 'Mantenimiento preventivo servidor crítico Banco Estado - Sala CPD', CURRENT_DATE, '18:00:00', 'Paulo Hernandez, Rodrigo Aguilera', TRUE, 'Aprobada', 'DC San Martin');

INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '09:15:00', 'Marcelo Robles', 'INC0008765', CURRENT_DATE, '10:00:00', 'Resolución incidencia red core Telefónica - Sala CrossConnect', CURRENT_DATE, '16:00:00', 'Arturo Chacón', TRUE, 'Aprobada', 'DC San Martin');

INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '10:45:00', 'Paulo Hernandez', 'CRQ0012346', DATEADD('DAY', 1, CURRENT_DATE), '08:00:00', 'Instalación nuevo switch Cisco 9500 - Sala Datos', DATEADD('DAY', 1, CURRENT_DATE), '12:00:00', 'Facilities, Rodrigo Aguilera', FALSE, 'Pendiente', 'DC San Martin');

-- Gestiones del día actual - DC Apoquindo
INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '07:30:00', 'Rodrigo Aguilera', 'CRQ0012347', CURRENT_DATE, '13:00:00', 'Actualización firmware equipos HPE - Sala Mainframe', CURRENT_DATE, '17:00:00', 'Paulo Hernandez', TRUE, 'Aprobada', 'DC Apoquindo');

INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '11:20:00', 'Arturo Chacón', 'INC0008766', CURRENT_DATE, '15:00:00', 'Reparación sistema climatización CRAC - Sala CPD1', CURRENT_DATE, '19:00:00', 'Facilities', TRUE, 'Rechazada por NXT', 'DC Apoquindo');

-- Gestiones del día actual - MC La Florida
INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '08:00:00', 'Marcelo Robles', 'CRQ0012348', CURRENT_DATE, '14:00:00', 'Instalación equipos nuevos Entel - Sala Movil', CURRENT_DATE, '18:00:00', 'Arturo Chacón, Rodrigo Aguilera', TRUE, 'Aprobada', 'MC La Florida');

INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '12:30:00', 'Paulo Hernandez', 'INC0008767', DATEADD('DAY', 2, CURRENT_DATE), '09:00:00', 'Revisión enlaces fibra óptica - Sala Datos', DATEADD('DAY', 2, CURRENT_DATE), '13:00:00', 'Operador Turno', FALSE, 'Devuelta al cliente por falta de Datos', 'MC La Florida');

-- Gestiones del día actual - MC Chiloé
INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (CURRENT_DATE, '09:45:00', 'Rodrigo Aguilera', 'CRQ0012349', CURRENT_DATE, '15:00:00', 'Mantenimiento equipos Huawei - Sala Transmision', CURRENT_DATE, '19:00:00', 'Paulo Hernandez', TRUE, 'Aprobada', 'MC Chiloé');

-- Gestiones vigentes (actividad en curso)
INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (DATEADD('DAY', -2, CURRENT_DATE), '08:00:00', 'Arturo Chacón', 'CRQ0012350', DATEADD('DAY', -1, CURRENT_DATE), '08:00:00', 'Proyecto migración servidores críticos - Múltiples salas', DATEADD('DAY', 5, CURRENT_DATE), '20:00:00', 'Paulo Hernandez, Rodrigo Aguilera, Facilities', TRUE, 'Aprobada', 'DC San Martin');

INSERT INTO gestion_acceso (fecha_registro, hora_registro, usuario_ingresa, numero_ticket, fecha_inicio_actividad, hora_inicio_actividad, nombre_actividad, fecha_termino_actividad, hora_termino_actividad, aprobadores_pendientes, gestion_realizada, estado_aprobacion, sitio) VALUES (DATEADD('DAY', -1, CURRENT_DATE), '10:00:00', 'Marcelo Robles', 'CRQ0012351', CURRENT_DATE, '00:00:00', 'Actualización masiva seguridad perimetral', DATEADD('DAY', 3, CURRENT_DATE), '23:59:00', 'Arturo Chacón', TRUE, 'Aprobada', 'DC Apoquindo');

-- ═══════════════════════════════════════════════════════════════
-- RESUMEN GESTIÓN DE ACCESOS:
-- • 10 registros de gestión de acceso
-- • Estados: Aprobada (7), Pendiente (1), Rechazada por NXT (1), Devuelta (1)
-- • Sitios: DC San Martin (4), DC Apoquindo (3), MC La Florida (2), MC Chiloé (1)
-- • Gestiones del día actual: 8
-- • Gestiones que no llegan en el día: 2
-- • Gestiones vigentes (en curso): 2
-- ═══════════════════════════════════════════════════════════════
