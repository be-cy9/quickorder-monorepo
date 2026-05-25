# QuickOrder - 10 Microservicios

## Integrantes
- Benjamín Almonacid

## Tecnologías
- Java 17, Spring Boot 3.2.0, MySQL, Docker, Maven

## Cómo ejecutar
1. `docker-compose up -d mysql_db`
2. `cd ms-pedidos && mvn spring-boot:run`

## Microservicios y puertos
| Microservicio | Puerto |
|---------------|--------|
| ms-pedidos | 8081 |
| ms-clientes | 8082 |
| ms-inventario | 8083 |
| ms-pagos | 8084 |
| ms-notificaciones | 8086 |
| ms-facturacion | 8087 |
| ms-envios | 8088 |
| ms-soporte | 8089 |
| ms-reportes | 8090 |
| ms-seguridad | 8091 |
