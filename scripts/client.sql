-- ============================================
-- Script de creación de tabla 'clientes'
-- Base de datos: PostgreSQL
-- Fecha: 2025-09-08
-- ============================================

-- Crear secuencia para el ID autoincremental
CREATE SEQUENCE IF NOT EXISTS clientes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Crear tabla clientes
CREATE TABLE IF NOT EXISTS clientes (
    -- Campos heredados de PersonEntity
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero CHAR(1)  NULL CHECK (genero IN ('M', 'F', 'O')),
    edad INTEGER CHECK (edad >= 0 AND edad <= 120),
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(15) NOT NULL,

    -- Campos específicos de ClientEntity
    cliente_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT true,

    -- Campos de auditoría (recomendados)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints adicionales
    CONSTRAINT chk_nombre_length CHECK (LENGTH(nombre) >= 2),
    CONSTRAINT chk_direccion_length CHECK (LENGTH(direccion) >= 5),
    CONSTRAINT chk_password_length CHECK (LENGTH(password) >= 8),
    CONSTRAINT chk_telefono_format CHECK (telefono ~ '^(\+593|0)9\d{8}$'),
    CONSTRAINT chk_identificacion_format CHECK (identificacion ~ '^\d{10}$|^\d{13}$|^[A-Za-z0-9]{6,12}$')
);

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_clientes_identificacion ON clientes(identificacion);
CREATE INDEX IF NOT EXISTS idx_clientes_cliente_id ON clientes(cliente_id);
CREATE INDEX IF NOT EXISTS idx_clientes_estado ON clientes(estado);
CREATE INDEX IF NOT EXISTS idx_clientes_nombre ON clientes(nombre);
CREATE INDEX IF NOT EXISTS idx_clientes_created_at ON clientes(created_at);

-- Crear función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Crear trigger para actualizar updated_at
DROP TRIGGER IF EXISTS update_clientes_updated_at ON clientes;
CREATE TRIGGER update_clientes_updated_at
    BEFORE UPDATE ON clientes
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comentarios en la tabla y columnas para documentación
COMMENT ON TABLE clientes IS 'Tabla de clientes del sistema - hereda campos de PersonEntity';
COMMENT ON COLUMN clientes.id IS 'Identificador único autoincrementable';
COMMENT ON COLUMN clientes.nombre IS 'Nombre completo del cliente (2-100 caracteres)';
COMMENT ON COLUMN clientes.genero IS 'Género: M=Masculino, F=Femenino, O=Otro';
COMMENT ON COLUMN clientes.edad IS 'Edad del cliente (0-120 años)';
COMMENT ON COLUMN clientes.identificacion IS 'Documento de identificación único (cédula, RUC, pasaporte)';
COMMENT ON COLUMN clientes.direccion IS 'Dirección completa del cliente (5-200 caracteres)';
COMMENT ON COLUMN clientes.telefono IS 'Teléfono formato Ecuador: +5939XXXXXXXX o 09XXXXXXXX';
COMMENT ON COLUMN clientes.cliente_id IS 'ID único del cliente para referencias externas';
COMMENT ON COLUMN clientes.password IS 'Contraseña hasheada del cliente (mín. 8 caracteres)';
COMMENT ON COLUMN clientes.estado IS 'Estado del cliente: true=activo, false=inactivo';
COMMENT ON COLUMN clientes.created_at IS 'Fecha y hora de creación del registro';
COMMENT ON COLUMN clientes.updated_at IS 'Fecha y hora de última actualización';

-- Datos de ejemplo (opcional - descomenta si necesitas datos de prueba)
/*
INSERT INTO clientes (
    nombre, genero, edad, identificacion, direccion, telefono,
    cliente_id, password, estado
) VALUES
(
    'Juan Carlos Pérez García',
    'M',
    35,
    '1234567890',
    'Av. Amazonas N24-03 y Colón, Quito, Ecuador',
    '0987654321',
    'CLI-12345678',
    '$2a$10$example.hash.password.here',
    true
),
(
    'María Fernanda López Silva',
    'F',
    28,
    '0987654321',
    'Calle 10 de Agosto y Mejía, Guayaquil, Ecuador',
    '+593987654322',
    'CLI-87654321',
    '$2a$10$another.example.hash.here',
    true
);
*/

-- Verificar la creación de la tabla
SELECT
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'clientes'
ORDER BY ordinal_position;

-- Mostrar constraints creados
SELECT
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_name = 'clientes'
ORDER BY tc.constraint_type, tc.constraint_name;
