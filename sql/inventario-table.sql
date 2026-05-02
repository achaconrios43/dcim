-- Crear tabla INVENTARIO en dcimdb
CREATE TABLE IF NOT EXISTS inventario (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sala VARCHAR(100),
    sitio VARCHAR(100),
    tipo VARCHAR(100),
    marca VARCHAR(100),
    modelo VARCHAR(100),
    numero_serie VARCHAR(100) UNIQUE,
    tag VARCHAR(100) UNIQUE,
    cliente VARCHAR(100),
    coordenadas VARCHAR(255),
    nombre_rack VARCHAR(100),
    ubicacion_ur VARCHAR(100),
    ur_utilizada VARCHAR(50),
    numero_temporal VARCHAR(50),
    hotname VARCHAR(100),
    estado VARCHAR(50),
    fecha_alarma DATE,
    alarma_hardware BIT(1),
    alarma_ventilador BIT(1),
    alarma_fuente_poder BIT(1),
    alarma_hdd BIT(1),
    comentarios_alarma TEXT,
    ticket_relacion VARCHAR(100),
    observaciones TEXT,
    flujo_aire VARCHAR(100),
    peso_equipo_kg DECIMAL(8,2),
    fuentes_poder VARCHAR(100),
    tipos_enchufe VARCHAR(100),
    observacion_tipo_enchufe TEXT,
    potencia_consumo_watts DECIMAL(10,2),
    direccion_ip VARCHAR(50),
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para búsquedas rápidas
CREATE INDEX idx_numero_serie ON inventario(numero_serie);
CREATE INDEX idx_tag ON inventario(tag);
CREATE INDEX idx_cliente ON inventario(cliente);
CREATE INDEX idx_estado ON inventario(estado);
CREATE INDEX idx_sala ON inventario(sala);
