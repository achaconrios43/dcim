-- ========================================
-- Script de Inicialización de Base de Datos
-- Sistema de Control de Acceso
-- ========================================

-- Crear tabla de usuarios si no existe
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(12) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'USER',
    creat_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_rut (rut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Usuario Administrador por defecto
-- Email: admin@clases.com
-- Password: Admin123! (cambiar en producción)
-- BCrypt hash de "Admin123!": $2a$10$5F3j8eqZqZ1.gKqZ1.gKqOeZ1.gKqOeZ1.gKqOeZ1.gKqOeZ1.gKqOe
INSERT INTO usuario (rut, nombre, apellido, email, password, rol) 
VALUES ('11111111-1', 'Administrador', 'Sistema', 'admin@clases.com', '$2a$10$5F3j8eqZqZ1gKqZ1gKqOeZ1gKqOeZ1gKqOeZ1gKqOeZ1gKqOe', 'ADMIN')
ON DUPLICATE KEY UPDATE password = password;

-- Crear tabla de ingresos AP si no existe
CREATE TABLE IF NOT EXISTS ingreso_ap (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    rut VARCHAR(12) NOT NULL,
    empresa VARCHAR(200),
    motivo TEXT,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_salida TIMESTAMP NULL,
    usuario_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE SET NULL,
    INDEX idx_rut (rut),
    INDEX idx_fecha_ingreso (fecha_ingreso)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Crear tabla de gestión de accesos si no existe
CREATE TABLE IF NOT EXISTS gestion_acceso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_acceso VARCHAR(50) NOT NULL,
    descripcion TEXT,
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    usuario_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE SET NULL,
    INDEX idx_estado (estado),
    INDEX idx_fecha_solicitud (fecha_solicitud)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices adicionales para optimización
CREATE INDEX IF NOT EXISTS idx_usuario_rol ON usuario(rol);
CREATE INDEX IF NOT EXISTS idx_usuario_creat_at ON usuario(creat_at);
