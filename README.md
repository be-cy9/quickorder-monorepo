# QuickOrder - 10 Microservicios

## Integrantes
- Benjamín Almonacid

## Tecnologías
- Java 17, Spring Boot 3.2.0, MySQL, Docker, Maven, JWT, Postman

## Arquitectura y Seguridad
Se ha implementado una arquitectura de microservicios dockerizada con **Seguridad JWT** de forma global. Cada microservicio cuenta con su propio `Dockerfile` e intercepta las peticiones mediante un filtro de seguridad para validar el token de autenticación.

## Cómo ejecutar

Gracias a la dockerización del proyecto, puedes levantar toda la infraestructura (Base de Datos + los 10 Microservicios) con un solo comando:

```bash
docker-compose up -d --build
```

Esto compilará y levantará todos los contenedores necesarios de forma automatizada.

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

## Pruebas (QA)
En la raíz del proyecto encontrarás el archivo `Tienda_Keys_Postman.json`. Es una colección de Postman pre-configurada para testear el flujo de los servicios con la autenticación JWT ya configurada.
