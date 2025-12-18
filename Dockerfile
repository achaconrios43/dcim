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
# ETAPA 2: RUNTIME - Imagen final optimizada
# ========================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Metadatos de la imagen
LABEL maintainer="achaconrios@gmail.com"
LABEL description="Sistema de Control de Acceso - Spring Boot Application"
LABEL version="1.0"

# Instalar wget para healthcheck y crear usuario no-root
RUN apk add --no-cache wget && \
    addgroup -S spring && adduser -S spring -G spring

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
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=production

# Health check para monitoreo
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8082}/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
