-- V1: Creación de la tabla de pedidos
-- Debe coincidir exactamente con la entidad Pedido.java
CREATE TABLE IF NOT EXISTS pedidos (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id    BIGINT          NOT NULL,
    producto_id   BIGINT          NOT NULL,
    descripcion   VARCHAR(500)    NOT NULL,
    monto         DECIMAL(10, 2)  NOT NULL,
    estado        VARCHAR(50)     NOT NULL,
    fecha_creacion TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;