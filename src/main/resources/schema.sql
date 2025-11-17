-- Esquema de base de datos para Sistema Data Center Management
-- Base de datos H2 en archivo persistente

-- Tabla de usuarios del sistema
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(12) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ubicacion VARCHAR(100),
    rol VARCHAR(20) NOT NULL DEFAULT 'USER',
    creat_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para registros de ingreso técnico al Data Center - Estructura completa
CREATE TABLE IF NOT EXISTS ingresoap (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    turno VARCHAR(10) NOT NULL, -- AM / PM
    nombre_usuario VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    fecha_termino DATE,
    hora_termino TIME,
    nombre_tecnico VARCHAR(100) NOT NULL,
    rut_tecnico VARCHAR(12) NOT NULL,
    empresa_demandante VARCHAR(100) NOT NULL,
    empresa_contratista VARCHAR(100) NOT NULL,
    cargo_tecnico VARCHAR(100) NOT NULL,
    sala_remedy VARCHAR(50) NOT NULL, -- Salas de RED o Salas TI
    tipo_ticket VARCHAR(30) NOT NULL, -- CRQ / INC / Visita Inspectiva / Ronda Rutinaria
    numero_ticket VARCHAR(50) NOT NULL,
    aprobador VARCHAR(100) NOT NULL, -- Paulo Hernandez / Arturo Chacón / Marcelo Robles / Facilities / Rodrigo Aguilera / Operador Turno / No Autorizado
    escolta VARCHAR(50) NOT NULL, -- Operador de Turno / Guardia / Personal de Facilities
    motivo_ingreso VARCHAR(100) NOT NULL, -- Inspectiva / Actividad Rutinaria / Instalación / Mediciones / Sin ticket / ticket de Empresa / ticket de Otro Sitio
    guia_despacho VARCHAR(50),
    sala_ingresa VARCHAR(50) NOT NULL,
    rack_ingresa VARCHAR(50),
    actividad_remedy TEXT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);
CREATE INDEX IF NOT EXISTS idx_usuario_rut ON usuario(rut);
CREATE INDEX IF NOT EXISTS idx_ingresoap_fecha_inicio ON ingresoap(fecha_inicio);
CREATE INDEX IF NOT EXISTS idx_ingresoap_rut_tecnico ON ingresoap(rut_tecnico);
CREATE INDEX IF NOT EXISTS idx_ingresoap_numero_ticket ON ingresoap(numero_ticket);
CREATE INDEX IF NOT EXISTS idx_ingresoap_tipo_ticket ON ingresoap(tipo_ticket);
CREATE INDEX IF NOT EXISTS idx_ingresoap_sala_ingresa ON ingresoap(sala_ingresa);
CREATE INDEX IF NOT EXISTS idx_ingresoap_turno ON ingresoap(turno);
CREATE INDEX IF NOT EXISTS idx_ingresoap_aprobador ON ingresoap(aprobador);