# DCIM - Sistema de Gestión de Ingresos

Sistema web desarrollado en Spring Boot para gestionar usuarios e ingresos en instalaciones de Data Centers y Mega Centrales.

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Runtime | Java 21 LTS |
| Framework | Spring Boot 3.5.9 |
| ORM | Spring Data JPA / Hibernate |
| Seguridad | Spring Security |
| Vistas | Thymeleaf |
| Frontend | TailwindCSS, HTML5, JavaScript |
| Base de datos | MySQL 8.4 |
| Build | Maven Wrapper |

## Requisitos previos

- Java 21 LTS
- MySQL 8.4 corriendo en `localhost:3307`
- Base de datos `dcimdb` creada

### Crear base de datos

```sql
CREATE DATABASE dcimdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Ejecución local

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación inicia en: `http://localhost:8081`

### Perfiles disponibles

| Perfil | Comando | Uso |
|--------|---------|-----|
| default | `.\mvnw.cmd spring-boot:run` | Desarrollo local |
| web | `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=web"` | Browser optimizado |
| mobile | `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mobile"` | API móvil |
| production | `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=production"` | Producción |

## Estructura del proyecto

```
src/main/java/com/example/dcim/
├── DcimApplication.java          # Clase principal
├── api/                          # Controladores REST
│   ├── AuthApiController.java
│   ├── GestionApiController.java
│   ├── IngresoApiController.java
│   └── UsuarioApiController.java
├── config/                       # Configuración
│   ├── CorsConfig.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── controllers/                  # Controladores web (Thymeleaf)
│   ├── ClienteDashboardController.java
│   ├── DashboardClienteDiarioController.java
│   ├── DashboardClienteMensualController.java
│   ├── DatabaseViewerController.java
│   ├── GestionAccesoController.java
│   ├── IngresoController.java
│   ├── LoginController.java
│   ├── MainController.java
│   └── UsuarioController.java
├── dao/                          # Repositorios JPA
├── entity/                       # Entidades JPA
│   ├── GestionAcceso.java
│   ├── IngresoAP.java
│   └── Usuario.java
└── service/                      # Lógica de negocio
```

## Endpoints principales

### Web

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/` | Página de inicio / Login |
| GET/POST | `/login` | Autenticación |
| GET/POST | `/ingresoap` | Registro de ingreso |
| GET | `/user/list` | Lista de usuarios |
| GET/POST | `/user/create` | Crear usuario |
| GET/POST | `/user/update` | Editar usuario |
| GET/POST | `/user/delete` | Eliminar usuario |
| GET | `/dashboard` | Panel principal |
| GET | `/database-viewer` | Visor de base de datos (requiere login) |

### API REST

| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/auth/login` | Login API |
| GET | `/api/usuarios` | Lista usuarios |
| GET | `/api/ingresos` | Lista ingresos |
| GET | `/api/gestion` | Lista gestión de accesos |

## Configuración de base de datos

Archivo: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/dcimdb
spring.datasource.username=root
spring.datasource.password=
```

Variables de entorno para producción:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Ubicaciones soportadas

- DC APOQUINDO
- DC SAN MARTIN
- MC LA FLORIDA
- MC INDEPENDENCIA
- MC CHILOÉ
- MC PROVIDENCIA
- MC PEDRO DE VALDIVIA
- MC MANUEL MONTT

## Build y tests

```powershell
# Compilar
.\mvnw.cmd compile

# Ejecutar tests
.\mvnw.cmd test

# Generar JAR
.\mvnw.cmd package -DskipTests
```

## Docker

```bash
docker build -t dcim .
docker run -p 8081:8081 dcim
```

---

**Versión:** 1.0.0 | **Java:** 21 LTS | **Spring Boot:** 3.5.9
