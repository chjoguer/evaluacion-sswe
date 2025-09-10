-- ============================================
-- Script de inicialización de base de datos
-- Base de datos: PostgreSQL
-- Fecha: 2025-09-08
-- ============================================

-- Ejecutar script de creación de la base de datos
\i create_database_script.sql

-- Ejecutar script de creación de tabla movements
\i create_movements_table.sql

-- Mensaje de finalización
SELECT 'Database initialization completed successfully' as status;
