# Esquema de Base de Datos DCIM

**Base de datos:** `dcimdb` en MySQL 8.4  
**Puerto:** 3307  
**Host:** localhost (127.0.0.1)

---

## Tablas

### 1. **usuario**
Gestión de usuarios del sistema.

| Campo | Tipo | Null | Key | Descripción |
|-------|------|------|-----|-----------|
| `id` | bigint | NO | PRI | ID único del usuario |
| `nombre` | varchar(100) | NO | | Nombre del usuario |
| `apellido` | varchar(100) | NO | | Apellido del usuario |
| `email` | varchar(150) | NO | UNI | Email único del usuario |
| `rut` | varchar(12) | NO | UNI | RUT único del usuario |
| `password` | varchar(255) | NO | | Contraseña encriptada |
| `rol` | varchar(20) | NO | | Rol del usuario (ej: ADMIN, USER, SUPERVISOR) |
| `creat_at` | datetime(6) | YES | | Fecha/hora de creación |
| `update_at` | datetime(6) | YES | | Fecha/hora de última actualización |

**Registros:** ~3-5 usuarios

---

### 2. **gestion_acceso**
Registro de gestiones de acceso y tickets en Data Centers.

| Campo | Tipo | Null | Key | Descripción |
|-------|------|------|-----|-----------|
| `id` | bigint | NO | PRI | ID único de la gestión |
| `numero_ticket` | varchar(255) | YES | | Número del ticket asociado |
| `nombre_actividad` | text | YES | | Nombre descriptivo de la actividad |
| `sitio` | varchar(255) | YES | | Sitio/Data Center donde ocurre |
| `estado_aprobacion` | varchar(255) | YES | | Estado: PENDIENTE, APROBADO, RECHAZADO |
| `aprobadores_pendientes` | text | YES | | Lista de aprobadores pendientes |
| `usuario_ingresa` | varchar(255) | YES | | Usuario que ingresa la gestión |
| `comentario_inicio` | text | YES | | Comentario inicial |
| `comentario_intermedio` | text | YES | | Comentario intermedio |
| `comentario_final` | text | YES | | Comentario final |
| `fecha_registro` | date | YES | | Fecha de registro |
| `hora_registro` | time(6) | YES | | Hora de registro |
| `fecha_inicio_actividad` | date | YES | | Fecha de inicio de la actividad |
| `hora_inicio_actividad` | time(6) | YES | | Hora de inicio de la actividad |
| `fecha_termino_actividad` | date | YES | | Fecha de término de la actividad |
| `hora_termino_actividad` | time(6) | YES | | Hora de término de la actividad |
| `fecha_inicio_remedy` | date | YES | | Fecha de inicio de remedy |
| `hora_inicio_remedy` | time(6) | YES | | Hora de inicio de remedy |
| `fecha_fin_remedy` | date | YES | | Fecha de fin de remedy |
| `hora_fin_remedy` | time(6) | YES | | Hora de fin de remedy |
| `fecha_respuesta_cliente` | date | YES | | Fecha de respuesta del cliente |
| `hora_respuesta_cliente` | time(6) | YES | | Hora de respuesta del cliente |
| `respuesta_cliente` | bit(1) | YES | | ¿Cliente respondió? (0=no, 1=sí) |
| `fecha_cierre_gestion` | date | YES | | Fecha de cierre de la gestión |
| `hora_cierre_gestion` | time(6) | YES | | Hora de cierre de la gestión |
| `ticket_cerrado` | bit(1) | YES | | ¿Ticket cerrado? (0=no, 1=sí) |
| `gestion_realizada` | bit(1) | YES | | ¿Gestión realizada? (0=no, 1=sí) |
| `enviado_a_procesos` | bit(1) | YES | | ¿Enviado a procesos? (0=no, 1=sí) |
| `fecha_envio_procesos` | date | YES | | Fecha de envío a procesos |
| `hora_envio_procesos` | time(6) | YES | | Hora de envío a procesos |

**Registros:** 6

---

### 3. **ingresoap**
Registro detallado de ingresos de técnicos a instalaciones (AP = "Acceso Personas").

| Campo | Tipo | Null | Key | Descripción |
|-------|------|------|-----|-----------|
| `id` | bigint | NO | PRI | ID único del ingreso |
| `numero_ticket` | varchar(50) | NO | | Número del ticket |
| `nombre_tecnico` | varchar(100) | NO | | Nombre completo del técnico |
| `rut_tecnico` | varchar(12) | NO | | RUT del técnico |
| `nombre_usuario` | varchar(100) | NO | | Nombre del usuario que registra |
| `empresa_demandante` | varchar(100) | NO | | Empresa que solicita el acceso |
| `empresa_contratista` | varchar(100) | NO | | Empresa contratista del técnico |
| `cargo_tecnico` | varchar(100) | NO | | Cargo/Especialidad del técnico |
| `motivo_ingreso` | varchar(100) | NO | | Motivo del ingreso |
| `sitio_ingreso` | varchar(100) | YES | | Sitio/Data Center de ingreso |
| `sala_ingresa` | text | NO | | Sala de ingreso |
| `rack_ingresa` | varchar(50) | YES | | Rack de ingreso |
| `tipo_ticket` | varchar(30) | NO | | Tipo de ticket (ej: MANTENIMIENTO, INSTALACIÓN) |
| `turno` | varchar(10) | NO | | Turno (DIURNO, NOCTURNO, etc.) |
| `escolta` | varchar(50) | NO | | Personal de escolta asignado |
| `aprobador` | varchar(100) | NO | | Usuario aprobador |
| `fecha_inicio` | date | NO | | Fecha de inicio |
| `hora_inicio` | time(6) | NO | | Hora de inicio |
| `fecha_termino` | date | YES | | Fecha de término |
| `hora_termino` | time(6) | YES | | Hora de término |
| `fecha_inicio_ficticia` | date | YES | | Fecha de inicio ficticia (para reporting) |
| `hora_inicio_ficticia` | time(6) | YES | | Hora de inicio ficticia |
| `fecha_fin_ficticia` | date | YES | | Fecha de fin ficticia |
| `hora_fin_ficticia` | time(6) | YES | | Hora de fin ficticia |
| `fecha_supervision_media` | date | YES | | Fecha de supervisión media |
| `hora_supervision_media` | time(6) | YES | | Hora de supervisión media |
| `fecha_segunda_supervision` | date | YES | | Fecha de segunda supervisión |
| `hora_segunda_supervision` | time(6) | YES | | Hora de segunda supervisión |
| `segunda_supervision_realizada` | bit(1) | YES | | ¿Segunda supervisión realizada? (0=no, 1=sí) |
| `sala_remedy` | varchar(50) | YES | | Sala de remedy |
| `actividad_remedy` | text | NO | | Actividad ejecutada en remedy |
| `foto_tecnico` | longtext | YES | | Foto del técnico (base64 o URL) |
| `guia_despacho` | varchar(50) | YES | | Número de guía de despacho |
| `coordenadas_gps` | text | YES | | Coordenadas GPS (formato JSON o texto) |
| `activo` | bit(1) | NO | | ¿Registro activo? (0=no, 1=sí) |
| `fecha_registro` | datetime(6) | YES | | Fecha/hora de registro en sistema |

**Registros:** 0 (tabla vacía)

---

### 4. **inventario**
Inventario técnico de equipos instalados en salas, racks y sitios.

| Campo | Tipo | Null | Key | Descripción |
|-------|------|------|-----|-----------|
| `id` | bigint | NO | PRI | ID único del equipo |
| `sala` | varchar(100) | YES | | Sala donde está el equipo |
| `sitio` | varchar(100) | YES | | Sitio o Data Center |
| `tipo` | varchar(100) | YES | | Tipo de equipo |
| `marca` | varchar(100) | YES | | Marca del equipo |
| `modelo` | varchar(100) | YES | | Modelo |
| `numero_serie` | varchar(100) | YES | UNI | Número de serie único |
| `tag` | varchar(100) | YES | UNI | Tag interno único |
| `cliente` | varchar(100) | YES | | Cliente asociado |
| `coordenadas` | varchar(255) | YES | | Coordenadas o referencia física |
| `nombre_rack` | varchar(100) | YES | | Rack donde está instalado |
| `ubicacion_ur` | varchar(100) | YES | | Posición UR |
| `ur_utilizada` | varchar(50) | YES | | UR ocupada |
| `numero_temporal` | varchar(50) | YES | | Código temporal |
| `hotname` | varchar(100) | YES | | Hostname registrado |
| `estado` | varchar(50) | YES | | Estado del activo |
| `fecha_alarma` | date | YES | | Fecha de alarma detectada |
| `alarma_hardware` | bit(1) | YES | | Alarma de hardware |
| `alarma_ventilador` | bit(1) | YES | | Alarma de ventilador |
| `alarma_fuente_poder` | bit(1) | YES | | Alarma de fuente de poder |
| `alarma_hdd` | bit(1) | YES | | Alarma de disco |
| `comentarios_alarma` | text | YES | | Comentarios de alarmas |
| `ticket_relacion` | varchar(100) | YES | | Ticket asociado |
| `observaciones` | text | YES | | Observaciones generales |
| `flujo_aire` | varchar(100) | YES | | Dirección de flujo de aire |
| `peso_equipo_kg` | decimal(8,2) | YES | | Peso del equipo |
| `fuentes_poder` | varchar(100) | YES | | Cantidad/tipo de fuentes de poder |
| `tipos_enchufe` | varchar(100) | YES | | Tipos de enchufe requeridos |
| `observacion_tipo_enchufe` | text | YES | | Observación del tipo de enchufe |
| `potencia_consumo_watts` | decimal(10,2) | YES | | Consumo eléctrico estimado |
| `direccion_ip` | varchar(50) | YES | | IP del equipo |
| `fecha_creacion` | datetime | YES | | Fecha de creación del registro |
| `fecha_modificacion` | datetime | YES | | Última modificación |

**Registros:** variable según operación de inventario

---

## Relaciones

- **usuario** ← No tiene FK explícitas, pero `gestion_acceso.usuario_ingresa` referencia nombre de usuario
- **gestion_acceso** ← Registra tickets generales
- **ingresoap** ← Detalle específico de acceso de personas (técnicos)
- **inventario** ← Inventario físico/técnico de equipos, sin FK explícitas

---

## Notas

- **Encoding:** utf8mb4 (soporte completo Unicode)
- **Collation:** utf8mb4_unicode_ci
- Todas las tablas usan `InnoDB` con soporte transacional
- Campos con `datetime(6)` soportan microsegundos de precisión
- Campos `text` pueden almacenar datos largos (comentarios, coordenadas, etc.)
- Campos `longtext` para datos muy grandes (fotos codificadas)
