-- =============================================================
-- DCIM — Schema consolidado
-- Base de datos: dcimdb (MySQL 8+, utf8mb4)
-- Generado: 2026-05-04
-- Reemplaza: inventario-table.sql, inventario-add-sitio.sql,
--            fix-inventario-duplicate-indexes.sql,
--            fase2-fk-usuario-operativo.sql,
--            optimizacion-usuarios-relaciones-mysql.sql
-- =============================================================

SET NAMES utf8mb4;
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================
-- 1. USUARIOS Y ROLES
-- =============================================================

CREATE TABLE IF NOT EXISTS usuario (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    rut        VARCHAR(20)  DEFAULT NULL,
    nombre     VARCHAR(100) DEFAULT NULL,
    apellido   VARCHAR(100) DEFAULT NULL,
    email      VARCHAR(150) DEFAULT NULL,
    password   VARCHAR(255) DEFAULT NULL,
    ubicacion  VARCHAR(100) DEFAULT NULL,
    rol        VARCHAR(30)  DEFAULT NULL,
    creat_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Catálogo de roles
CREATE TABLE IF NOT EXISTS tipo_usuario (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    codigo      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(120) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tipo_usuario_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Relación usuario ↔ tipo_usuario (N:M)
CREATE TABLE IF NOT EXISTS usuario_tipo_usuario (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id      BIGINT NOT NULL,
    tipo_usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_tipo (usuario_id, tipo_usuario_id),
    CONSTRAINT fk_usuario_tipo_usuario FOREIGN KEY (usuario_id)      REFERENCES usuario(id)      ON DELETE CASCADE,
    CONSTRAINT fk_usuario_tipo_tipo    FOREIGN KEY (tipo_usuario_id) REFERENCES tipo_usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos base de roles
INSERT INTO tipo_usuario (codigo, descripcion) VALUES
    ('ADMIN',  'Administrador del sistema'),
    ('USER',   'Usuario técnico'),
    ('VIEWER', 'Solo lectura')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- =============================================================
-- 2. SITIOS Y SALAS
-- =============================================================

CREATE TABLE IF NOT EXISTS sitio (
    id     BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sala (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(100) DEFAULT NULL,
    sitio_id BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sala_sitio FOREIGN KEY (sitio_id) REFERENCES sitio(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- 3. INGRESO AL DATA CENTER
-- =============================================================

CREATE TABLE IF NOT EXISTS ingresoap (
    id                   BIGINT NOT NULL AUTO_INCREMENT,
    turno                VARCHAR(20)  DEFAULT NULL,
    nombre_usuario       VARCHAR(200) DEFAULT NULL,
    fecha_inicio         DATE         DEFAULT NULL,
    hora_inicio          TIME         DEFAULT NULL,
    fecha_termino        DATE         DEFAULT NULL,
    hora_termino         TIME         DEFAULT NULL,
    nombre_tecnico       VARCHAR(200) DEFAULT NULL,
    rut_tecnico          VARCHAR(20)  DEFAULT NULL,
    empresa_demandante   VARCHAR(200) DEFAULT NULL,
    empresa_contratista  VARCHAR(200) DEFAULT NULL,
    cargo_tecnico        VARCHAR(100) DEFAULT NULL,
    sala_remedy          VARCHAR(100) DEFAULT NULL,
    tipo_ticket          VARCHAR(50)  DEFAULT NULL,
    numero_ticket        VARCHAR(50)  DEFAULT NULL,
    aprobador            VARCHAR(200) DEFAULT NULL,
    escolta              VARCHAR(200) DEFAULT NULL,
    motivo_ingreso       TEXT         DEFAULT NULL,
    guia_despacho        VARCHAR(100) DEFAULT NULL,
    sala_ingresa         VARCHAR(100) DEFAULT NULL,
    rack_ingresa         VARCHAR(100) DEFAULT NULL,
    actividad_remedy     TEXT         DEFAULT NULL,
    fecha_registro       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    activo               BIT(1)       DEFAULT b'1',
    -- Fase 2: referencias FK a usuario
    usuario_registra_id  BIGINT       DEFAULT NULL,
    aprobador_id         BIGINT       DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ingreso_usuario_registra FOREIGN KEY (usuario_registra_id) REFERENCES usuario(id) ON DELETE SET NULL,
    CONSTRAINT fk_ingreso_aprobador        FOREIGN KEY (aprobador_id)        REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- 4. GESTIÓN DE ACCESO
-- =============================================================

CREATE TABLE IF NOT EXISTS gestion_acceso (
    id                    BIGINT NOT NULL AUTO_INCREMENT,
    usuario_ingresa       VARCHAR(200) DEFAULT NULL,
    fecha_solicitud       DATE         DEFAULT NULL,
    hora_solicitud        TIME         DEFAULT NULL,
    tipo_acceso           VARCHAR(100) DEFAULT NULL,
    motivo                TEXT         DEFAULT NULL,
    sala_destino          VARCHAR(100) DEFAULT NULL,
    rack_destino          VARCHAR(100) DEFAULT NULL,
    empresa               VARCHAR(200) DEFAULT NULL,
    aprobadores_pendientes TEXT         DEFAULT NULL,
    estado                VARCHAR(50)  DEFAULT NULL,
    fecha_resolucion      DATETIME     DEFAULT NULL,
    observaciones         TEXT         DEFAULT NULL,
    activo                BIT(1)       DEFAULT b'1',
    fecha_creacion        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Fase 2: referencia FK a usuario
    usuario_registra_id   BIGINT       DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_gestion_usuario_registra FOREIGN KEY (usuario_registra_id) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- 5. INVENTARIO
-- =============================================================

CREATE TABLE IF NOT EXISTS inventario (
    id                         BIGINT NOT NULL AUTO_INCREMENT,
    sala                       VARCHAR(100)   DEFAULT NULL,
    sitio                      VARCHAR(100)   DEFAULT NULL,
    tipo                       VARCHAR(100)   DEFAULT NULL,
    marca                      VARCHAR(100)   DEFAULT NULL,
    modelo                     VARCHAR(100)   DEFAULT NULL,
    numero_serie               VARCHAR(100)   DEFAULT NULL,
    tag                        VARCHAR(100)   DEFAULT NULL,
    cliente                    VARCHAR(100)   DEFAULT NULL,
    coordenadas                VARCHAR(255)   DEFAULT NULL,
    nombre_rack                VARCHAR(100)   DEFAULT NULL,
    ubicacion_ur               VARCHAR(100)   DEFAULT NULL,
    ur_utilizada               VARCHAR(50)    DEFAULT NULL,
    numero_temporal            VARCHAR(50)    DEFAULT NULL,
    hotname                    VARCHAR(100)   DEFAULT NULL,
    estado                     VARCHAR(50)    DEFAULT NULL,
    fecha_alarma               DATE           DEFAULT NULL,
    alarma_hardware            BIT(1)         DEFAULT NULL,
    alarma_ventilador          BIT(1)         DEFAULT NULL,
    alarma_fuente_poder        BIT(1)         DEFAULT NULL,
    alarma_hdd                 BIT(1)         DEFAULT NULL,
    comentarios_alarma         TEXT           DEFAULT NULL,
    ticket_relacion            VARCHAR(100)   DEFAULT NULL,
    observaciones              TEXT           DEFAULT NULL,
    flujo_aire                 VARCHAR(100)   DEFAULT NULL,
    peso_equipo_kg             DECIMAL(8,2)   DEFAULT NULL,
    fuentes_poder              VARCHAR(100)   DEFAULT NULL,
    tipos_enchufe              VARCHAR(100)   DEFAULT NULL,
    observacion_tipo_enchufe   TEXT           DEFAULT NULL,
    potencia_consumo_watts     DECIMAL(10,2)  DEFAULT NULL,
    direccion_ip               VARCHAR(50)    DEFAULT NULL,
    fecha_creacion             DATETIME       DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion         DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventario_numero_serie (numero_serie),
    UNIQUE KEY uk_inventario_tag (tag),
    KEY idx_inventario_cliente (cliente),
    KEY idx_inventario_estado  (estado),
    KEY idx_inventario_sala    (sala)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- 6. TEMPERATURAS
-- =============================================================

CREATE TABLE IF NOT EXISTS punto_medicion (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(100) DEFAULT NULL,
    sala_id  BIGINT       DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_punto_sala FOREIGN KEY (sala_id) REFERENCES sala(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS medicion_temperatura (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    punto_medicion_id BIGINT         DEFAULT NULL,
    temperatura       DECIMAL(5,2)   DEFAULT NULL,
    humedad           DECIMAL(5,2)   DEFAULT NULL,
    fecha_hora        DATETIME       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_medicion_punto FOREIGN KEY (punto_medicion_id) REFERENCES punto_medicion(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- 7. PLANOS DE SALA
-- =============================================================

CREATE TABLE IF NOT EXISTS plano_sala (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) DEFAULT NULL,
    sala_id     BIGINT       DEFAULT NULL,
    descripcion TEXT         DEFAULT NULL,
    activo      BIT(1)       DEFAULT b'1',
    PRIMARY KEY (id),
    CONSTRAINT fk_plano_sala FOREIGN KEY (sala_id) REFERENCES sala(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS plano_sala_elemento (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    plano_id     BIGINT       DEFAULT NULL,
    tipo         VARCHAR(50)  DEFAULT NULL,
    etiqueta     VARCHAR(100) DEFAULT NULL,
    pos_x        INT          DEFAULT NULL,
    pos_y        INT          DEFAULT NULL,
    ancho        INT          DEFAULT NULL,
    alto         INT          DEFAULT NULL,
    color        VARCHAR(20)  DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_elemento_plano FOREIGN KEY (plano_id) REFERENCES plano_sala(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- 8. TABLAS PUENTE USUARIO ↔ MÓDULOS
-- =============================================================

CREATE TABLE IF NOT EXISTS usuario_ingresoap_rel (
    id             BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id     BIGINT      NOT NULL,
    ingresoap_id   BIGINT      NOT NULL,
    tipo_relacion  VARCHAR(40) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_ingresoap_rel (usuario_id, ingresoap_id, tipo_relacion),
    KEY idx_uir_ingreso_tipo  (ingresoap_id, tipo_relacion),
    KEY idx_uir_usuario_tipo  (usuario_id,   tipo_relacion),
    CONSTRAINT fk_uir_usuario FOREIGN KEY (usuario_id)   REFERENCES usuario(id)   ON DELETE CASCADE,
    CONSTRAINT fk_uir_ingreso FOREIGN KEY (ingresoap_id) REFERENCES ingresoap(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario_gestion_rel (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id        BIGINT      NOT NULL,
    gestion_acceso_id BIGINT      NOT NULL,
    tipo_relacion     VARCHAR(40) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_gestion_rel (usuario_id, gestion_acceso_id, tipo_relacion),
    KEY idx_ugr_gestion_tipo (gestion_acceso_id, tipo_relacion),
    KEY idx_ugr_usuario_tipo (usuario_id,         tipo_relacion),
    CONSTRAINT fk_ugr_usuario FOREIGN KEY (usuario_id)        REFERENCES usuario(id)        ON DELETE CASCADE,
    CONSTRAINT fk_ugr_gestion FOREIGN KEY (gestion_acceso_id) REFERENCES gestion_acceso(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario_inventario_rel (
    id             BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id     BIGINT      NOT NULL,
    inventario_id  BIGINT      NOT NULL,
    tipo_relacion  VARCHAR(40) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_inventario_rel (usuario_id, inventario_id, tipo_relacion),
    KEY idx_uivr_inventario_tipo (inventario_id, tipo_relacion),
    KEY idx_uivr_usuario_tipo    (usuario_id,    tipo_relacion),
    CONSTRAINT fk_uivr_usuario    FOREIGN KEY (usuario_id)    REFERENCES usuario(id)    ON DELETE CASCADE,
    CONSTRAINT fk_uivr_inventario FOREIGN KEY (inventario_id) REFERENCES inventario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================
-- 9. VISTA DE CONTROL UNIFICADA
-- =============================================================

CREATE OR REPLACE VIEW vw_usuario_modulo_relaciones AS
SELECT
    u.id   AS usuario_id,
    u.email,
    CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo,
    tu.codigo AS tipo_usuario,
    'INGRESOAP'      AS modulo,
    r.tipo_relacion,
    r.ingresoap_id   AS entidad_id
FROM usuario u
JOIN usuario_tipo_usuario utu ON utu.usuario_id = u.id
JOIN tipo_usuario tu          ON tu.id = utu.tipo_usuario_id
JOIN usuario_ingresoap_rel r  ON r.usuario_id = u.id
UNION ALL
SELECT
    u.id, u.email, CONCAT(u.nombre, ' ', u.apellido),
    tu.codigo, 'GESTION_ACCESO', r.tipo_relacion, r.gestion_acceso_id
FROM usuario u
JOIN usuario_tipo_usuario utu ON utu.usuario_id = u.id
JOIN tipo_usuario tu          ON tu.id = utu.tipo_usuario_id
JOIN usuario_gestion_rel r    ON r.usuario_id = u.id
UNION ALL
SELECT
    u.id, u.email, CONCAT(u.nombre, ' ', u.apellido),
    tu.codigo, 'INVENTARIO', r.tipo_relacion, r.inventario_id
FROM usuario u
JOIN usuario_tipo_usuario utu ON utu.usuario_id = u.id
JOIN tipo_usuario tu          ON tu.id = utu.tipo_usuario_id
JOIN usuario_inventario_rel r ON r.usuario_id = u.id;

SET FOREIGN_KEY_CHECKS = 1;
