# ========================================
# ETAPA 1: BUILD - Construcción de la aplicación
# ========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para aprovechar caché de Docker)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (esta capa se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación (sin ejecutar tests para build más rápido)
RUN mvn clean package -DskipTests

# ========================================
# ETAPA 2: RUNTIME - Imagen final con Red Hat UBI 9
# ========================================
FROM registry.access.redhat.com/ubi9/openjdk-21:latest AS runtime

# Metadatos de la imagen
LABEL maintainer="achaconrios@gmail.com"
LABEL description="Sistema de Control de Acceso DCIM - Spring Boot Application"
LABEL version="1.0"
LABEL source="Red Hat UBI 9 with OpenJDK 21"

# Instalar herramientas necesarias (wget para healthcheck, crear usuario no-root)
RUN yum update -y && \
    yum install -y wget && \
    yum clean all && \
    groupadd -r spring && useradd -r -g spring spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar propietario del archivo
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto (variable de entorno PORT o 8082 por defecto)
EXPOSE 8082

# Variables de entorno por defecto (se pueden sobrescribir en runtime)
ENV JAVA_OPTS="-Xmx450m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
    SPRING_PROFILES_ACTIVE=production \
    PORT=8082

# Health check para monitoreo - MUY tolerante
HEALTHCHECK --interval=60s --timeout=10s --start-period=240s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Ejecutar la aplicación
CMD java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar
