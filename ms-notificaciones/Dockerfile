# Dockerfile para microservicios QuickOrder
# Usa Java 17 como base (compatible con GraalVM si decides usarlo después)

FROM openjdk:17-jdk-slim

# Información del mantenedor
LABEL maintainer="QuickOrder Team"
LABEL description="Microservicio para QuickOrder"

# Crear directorio de trabajo
WORKDIR /app

# Copiar el JAR generado por Maven
COPY target/*.jar app.jar

# Exponer el puerto (cambiar según el microservicio)
# 8081: ms-pedidos
# 8082: ms-clientes  
# 8083: ms-inventario
# 8084: ms-pagos
# 8085: ms-notificaciones
EXPOSE 8080

# Variables de entorno para Spring Boot
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx128m -Xms64m"

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]