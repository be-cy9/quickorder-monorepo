-- V2: Datos de ejemplo (seed) para la tabla pedidos
-- Tienda de videojuegos QuickOrder
INSERT INTO pedidos (cliente_id, producto_id, descripcion, monto, estado, fecha_creacion) VALUES
    (1, 101, 'The Legend of Zelda: Tears of the Kingdom - Nintendo Switch', 59990.00, 'PENDIENTE',  NOW()),
    (2, 205, 'God of War Ragnarok - PlayStation 5',                         64990.00, 'EN_PROCESO', NOW()),
    (1, 312, 'Elden Ring - PC Steam Key',                                   49990.00, 'COMPLETADO', NOW()),
    (3, 407, 'Control Inalambrico DualSense - Midnight Black',              54990.00, 'PENDIENTE',  NOW()),
    (4, 518, 'Hogwarts Legacy - Xbox Series X',                             59990.00, 'CANCELADO',  NOW());