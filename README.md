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

## Stack y herramientas usadas

- Lenguaje principal: Java 21
- Framework backend: Spring Boot 3.5.9
- Plantillas UI: Thymeleaf + HTML + JavaScript + TailwindCSS
- Persistencia: Spring Data JPA con Hibernate
- Seguridad: Spring Security + BCrypt + CSRF para web
- IDE/Extensión: VS Code con Java (Red Hat Language Support)
- Contenedores: Docker multi-stage
- Runtime de contenedor: Red Hat UBI 9 con OpenJDK 21
- Despliegue cloud (opcional): Koyeb

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

## Seguridad implementada

- Autenticación web por formulario (`/login`) con sesión
- Password hashing con BCrypt
- CSRF habilitado en rutas web
- API REST en `/api/**` stateless y sin CSRF
- Health endpoint de Actuator permitido para monitoreo

Referencia: `src/main/java/com/example/dcim/config/SecurityConfig.java`

## MySQL local y Workbench

Conexión local actual:

- Hostname: `127.0.0.1`
- Puerto: `3307`
- Usuario: `root`
- Password: vacío
- Schema: `dcimdb`

En MySQL Workbench:

1. Crear nueva conexión con esos parámetros.
2. Abrir schema `dcimdb`.
3. Revisar la vista unificada: `vw_usuario_modulo_relaciones`.
4. Revisar tablas relacionales nuevas:
	- `tipo_usuario`
	- `usuario_tipo_usuario`
	- `usuario_ingresoap_rel`
	- `usuario_gestion_rel`
	- `usuario_inventario_rel`

## Unión de tablas por tipo de usuario

Se implementó un modelo relacional para unir los módulos con usuario y tipo de usuario:

- `usuario` conserva datos de identidad y rol operativo
- `tipo_usuario` define catálogo de tipos (ADMIN, USER)
- `usuario_tipo_usuario` asigna tipo a cada usuario
- `usuario_ingresoap_rel` une usuarios con `ingresoap`
- `usuario_gestion_rel` une usuarios con `gestion_acceso`
- `usuario_inventario_rel` une usuarios con `inventario`
- `vw_usuario_modulo_relaciones` entrega una vista unificada para consulta

Herramientas para mantenimiento/normalización:

- `com.example.dcim.tools.DatabaseMaintenanceTool`
- `com.example.dcim.tools.DatabaseExportTool`

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
