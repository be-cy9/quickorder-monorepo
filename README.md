# QuickOrder - Arquitectura de Microservicios

## Integrantes
- Benjamín [Tu Apellido]
- [Nombre Compañero 1]
- [Nombre Compañero 2]

## Descripción del Proyecto
QuickOrder es una plataforma de gestión de pedidos basada en microservicios que permite:
- Gestión de pedidos, clientes, inventario, pagos
- Facturación, envíos, soporte al cliente
- Reportes y seguridad

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Docker / Docker Compose
- Maven
- Flyway (Migraciones)

## Requisitos Previos
- Docker Desktop instalado y ejecutándose
- Java 17
- Maven
- Postman (para pruebas)

## Estructura del Proyecto
quickorder-monorepo/
├── ms-pedidos/ (Puerto 8081)
├── ms-clientes/ (Puerto 8082)
├── ms-inventario/ (Puerto 8083)
├── ms-pagos/ (Puerto 8084)
├── ms-notificaciones/ (Puerto 8086)
├── ms-facturacion/ (Puerto 8087)
├── ms-envios/ (Puerto 8088)
├── ms-soporte/ (Puerto 8089)
├── ms-reportes/ (Puerto 8090)
├── ms-seguridad/ (Puerto 8091)
└── init-db/ (Scripts SQL)

