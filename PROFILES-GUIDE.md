# Guía de Perfiles de Spring Boot

Este proyecto soporta **múltiples perfiles** para diferentes contextos de ejecución.

## Perfiles Disponibles

### 1. **web** (DEFAULT)
- **Uso**: Aplicación web desde navegador
- **Características**:
  - CSRF habilitado para formularios
  - Encoding UTF-8 estándar (sin BOM)
  - Thymeleaf templates
  - Optimizado para navegadores web

### 2. **mobile**
- **Uso**: API para aplicación móvil
- **Características**:
  - CORS habilitado para peticiones cross-origin
  - Encoding UTF-8 con soporte BOM
  - JSON serialization optimizado
  - Pool de conexiones ampliado (15 conexiones)
  - CSRF puede ser deshabilitado para API

### 3. **production**
- **Uso**: Despliegue en Koyeb (producción)
- **Características**:
  - Variables de entorno para credenciales
  - Pool optimizado para micro instance (10 conexiones)
  - Logs minimizados
  - Health checks configurados

## Cómo Cambiar de Perfil

### Desarrollo Local

**Maven:**
```bash
# Perfil web (default)
mvn spring-boot:run

# Perfil mobile
mvn spring-boot:run -Dspring-boot.run.profiles=mobile

# Perfil production
mvn spring-boot:run -Dspring-boot.run.profiles=production
```

**Ejecutar JAR:**
```bash
# Construir JAR
mvn clean package

# Perfil web
java -jar target/clases-0.0.1-SNAPSHOT.jar

# Perfil mobile
java -jar -Dspring.profiles.active=mobile target/clases-0.0.1-SNAPSHOT.jar

# Perfil production
java -jar -Dspring.profiles.active=production target/clases-0.0.1-SNAPSHOT.jar
```

### Variable de Entorno

**Windows PowerShell:**
```powershell
$env:SPRING_PROFILES_ACTIVE="mobile"
mvn spring-boot:run
```

**Linux/Mac:**
```bash
export SPRING_PROFILES_ACTIVE=mobile
mvn spring-boot:run
```

### Koyeb (Producción)

En Koyeb, configura la variable de entorno:
```
SPRING_PROFILES_ACTIVE=production
```

## Diferencias Clave

| Característica | Web | Mobile | Production |
|---------------|-----|--------|------------|
| BOM UTF-8 | ❌ No | ✅ Sí | ❌ No |
| CSRF | ✅ Habilitado | ⚠️ Configurable | ✅ Habilitado |
| CORS | ❌ Restringido | ✅ Abierto | ❌ Restringido |
| Pool Conexiones | 10 | 15 | 10 |
| Show SQL | ❌ | ❌ | ❌ |
| Thymeleaf Cache | ❌ | N/A | ✅ |

## Configuración Actual

El proyecto está configurado con **perfil web** como default. Si no se especifica ningún perfil, se usará `web`.

## Archivos de Configuración

- `application.properties` - Configuración base + perfil default
- `application-web.properties` - Configuración para navegador web
- `application-mobile.properties` - Configuración para app móvil
- `application-production.properties` - Configuración para Koyeb

## Ejemplo: Proyecto Conjunto Web + Móvil

Si tienes una app móvil que consume la misma API:

1. **Desarrollo local de la web**: `mvn spring-boot:run` (usa perfil web)
2. **Testing con app móvil**: `mvn spring-boot:run -Dspring-boot.run.profiles=mobile`
3. **Producción en Koyeb**: Variable `SPRING_PROFILES_ACTIVE=production`

De esta forma, el mismo proyecto sirve tanto para la aplicación web como para la móvil, solo cambiando el perfil activo.
