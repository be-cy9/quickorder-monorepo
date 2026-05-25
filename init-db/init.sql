-- =====================================================
-- SCRIPT DE INICIALIZACIÓN PARA QUICKORDER
-- Crea las 10 bases de datos para los microservicios
-- =====================================================

-- Eliminar bases de datos si existen (para limpiar)
DROP DATABASE IF EXISTS db_pedidos;
DROP DATABASE IF EXISTS db_clientes;
DROP DATABASE IF EXISTS db_inventario;
DROP DATABASE IF EXISTS db_pagos;
DROP DATABASE IF EXISTS db_facturacion;
DROP DATABASE IF EXISTS db_envios;
DROP DATABASE IF EXISTS db_notificaciones;
DROP DATABASE IF EXISTS db_soporte;
DROP DATABASE IF EXISTS db_reportes;
DROP DATABASE IF EXISTS db_seguridad;

-- Crear las 10 bases de datos
CREATE DATABASE IF NOT EXISTS db_pedidos 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_clientes 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_inventario 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_pagos 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_facturacion 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_envios 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_notificaciones 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_soporte 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_reportes 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS db_seguridad 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Verificar que todas se crearon correctamente
SELECT '=== BASES DE DATOS CREADAS EXITOSAMENTE ===' AS Mensaje;
SHOW DATABASES;

-- Otorgar privilegios (opcional, para seguridad)
GRANT ALL PRIVILEGES ON db_pedidos.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_clientes.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_inventario.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_pagos.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_facturacion.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_envios.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_notificaciones.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_soporte.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_reportes.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON db_seguridad.* TO 'root'@'%';

FLUSH PRIVILEGES;

-- Mensaje final
SELECT '=== TODO LISTO: QUICKORDER ESTÁ CONFIGURADO ===' AS Estado;