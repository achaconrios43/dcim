# 📋 Sistema de Gestión de Ingresos IPSS

Sistema web desarrollado en Spring Boot para gestionar usuarios e ingresos en instalaciones de Data Centers y Mega Centrales de Telefónica.

## 🔧 Tecnologías Utilizadas

- **Java 21 LTS** - Runtime principal del proyecto
- **Spring Boot 3.5.6** - Framework principal
- **Spring MVC** - Controladores web
- **Thymeleaf** - Motor de plantillas
- **Maven** - Gestión de dependencias
- **HTML5/CSS3** - Frontend
- **TailwindCSS** - Framework CSS
- **JavaScript** - Funcionalidades interactivas

## 🏗️ Arquitectura del Proyecto

### Estructura de Directorios
```
src/
├── main/
│   ├── java/com/example/clases/
│   │   ├── ClasesApplication.java          # Clase principal
│   │   ├── controllers/                    # Controladores web
│   │   │   ├── IngresoController.java      # Gestión de ingresos
│   │   │   ├── LoginController.java        # Autenticación
│   │   │   └── UserController.java         # CRUD usuarios
│   │   └── entity/
│   │       └── Usuario.java                # Entidad usuario
│   └── resources/
│       ├── templates/                      # Vistas Thymeleaf
│       │   ├── fragments/                  # Componentes reutilizables
│       │   ├── user/                       # Vistas de usuarios
│       │   ├── ingresoap.html             # Formulario ingreso
│       │   └── login.html                 # Página de login
│       └── application.properties          # Configuración
```

## 🎯 Funcionalidades Principales

### 1. Gestión de Usuarios (UserController)

**Endpoints principales:**
- `GET /user/list` - Lista todos los usuarios
- `GET /user/create` - Formulario de creación
- `POST /user/create` - Crear nuevo usuario
- `GET /user/update?idx={n}` - Formulario de edición
- `POST /user/update` - Actualizar usuario
- `GET /user/delete?idx={n}` - Confirmación de eliminación
- `POST /user/delete` - Eliminar usuario
- `GET /user/exists` - Validar si email existe (AJAX)
- `GET /user/validate` - Validar credenciales (AJAX)

**Características:**
- ✅ CRUD completo de usuarios
- ✅ Validación de campos obligatorios
- ✅ Verificación de duplicados por email
- ✅ Gestión de roles (USER/ADMIN)
- ✅ Soporte para múltiples ubicaciones
- ✅ Integración con sistema de login

### 2. Sistema de Login (LoginController)

**Endpoints:**
- `GET /` o `GET /login` - Página de login
- `POST /login` - Procesar autenticación

**Características:**
- ✅ Autenticación por email o nombre de usuario
- ✅ Selección de ubicación obligatoria
- ✅ Validación en tiempo real de existencia de usuario
- ✅ Redirección automática a formulario de ingreso tras login exitoso
- ✅ Funcionalidad "Mostrar/Ocultar contraseña"

### 3. Registro de Ingresos (IngresoController)

**Endpoints:**
- `GET /ingresoap` - Formulario de registro de ingreso
- `POST /ingresoap` - Procesar registro de ingreso

**Características:**
- ✅ Formulario completo de registro de visitantes
- ✅ Validación exhaustiva de todos los campos
- ✅ Validación de formato RUT chileno
- ✅ Auto-completado de fecha y hora actual
- ✅ Gestión de tipos de tickets (CRQ, INC, VISITA)
- ✅ Almacenamiento en memoria (demo)
- ✅ Pre-llenado desde login de usuario

## 📱 Interfaces de Usuario

### 1. Login (login.html)

**Funcionalidades:**
- Formulario de autenticación elegante con diseño moderno
- Validación en tiempo real de usuarios existentes
- Dropdown de ubicaciones predefinidas
- Integración AJAX para validación sin recarga
- Redirección automática tras login exitoso
- Enlace directo a registro de nuevos usuarios

**Campos:**
- Usuario o correo electrónico
- Contraseña (con opción mostrar/ocultar)
- Ubicación (dropdown obligatorio)
- Checkbox "Recuérdame"

### 2. Formulario de Ingreso (ingresoap.html)

**Funcionalidades:**
- Interfaz intuitiva para registro de visitantes
- Validación de RUT en formato chileno
- Auto-completado de fecha y hora
- Dropdown de tipos de REMEDY
- Validación client-side y server-side
- Mensajes de error y éxito mediante modales

**Campos obligatorios:**
- Nombre completo
- RUT (formato: 12.345.678-9)
- Fecha y hora de ingreso
- Empresa y contratista
- Tipo de REMEDY (CRQ/INC/VISITA)
- Número de ticket
- Aprobador
- Motivo de la visita

### 3. Gestión de Usuarios (user/)

#### a) Lista de Usuarios (list.html)
- Tabla completa con todos los usuarios registrados
- Acciones rápidas: Editar, Eliminar, Registrar Ingreso
- Diseño responsive con estilos 3D modernos
- Identificación visual de roles (ADMIN resaltado)

#### b) Crear Usuario (create.html)
- Formulario completo de registro
- Sistema de tags para múltiples ubicaciones
- Botones de sugerencia para ubicaciones comunes
- Validación en tiempo real
- Modal de confirmación de errores

#### c) Actualizar Usuario (update.html)
- Formulario pre-llenado con datos existentes
- Misma funcionalidad que creación
- Validación de campos modificados

#### d) Eliminar Usuario (delete.html)
- Página de confirmación con datos del usuario
- Prevención de eliminaciones accidentales

### 4. Componentes Reutilizables (fragments/)

#### a) Headers (headers.html)
- Configuración de metadatos y estilos globales
- Importación de Tailwind CSS y fuentes
- Variables CSS para colores corporativos
- Topbar con información de la empresa

#### b) Navegación (navdar.html)
- Barra de navegación responsive
- Logo corporativo IPSS
- Enlaces principales del sistema
- Menú móvil adaptativo

## 🎨 Diseño y Estilos

### Paleta de Colores Telefónica
- **Azul Principal:** `#003DA5` (tf-blue)
- **Azul Cyan:** `#00A1E6` (tf-cyan)
- **Azul Oscuro:** `#002B5C` (tf-dark)
- **Backgrounds:** Gradientes suaves azul-blancos

### Características de Diseño
- ✅ Efectos 3D sutiles en tarjetas y componentes
- ✅ Diseño responsive para móviles y escritorio
- ✅ Animaciones CSS suaves
- ✅ Glassmorphism y backdrop blur
- ✅ Iconografía SVG personalizada
- ✅ Tipografía Inter para legibilidad óptima

## 📋 Entidad Usuario

```java
public class Usuario {
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String ubicacion;
    private String rol;
    private Date creatAt;
    private Date updateAt;
}
```

**Validaciones:**
- Todos los campos son obligatorios
- Email debe ser único en el sistema
- RUT debe seguir formato chileno
- Rol puede ser "USER" o "ADMIN"
- Timestamps automáticos de creación y actualización

## 🔄 Flujo de Trabajo

### 1. Registro de Usuario Nuevo
1. Usuario accede a `/user/create`
2. Completa formulario con validación en tiempo real
3. Sistema verifica unicidad del email
4. Usuario se guarda en memoria
5. Redirección a lista de usuarios

### 2. Proceso de Login
1. Usuario accede a `/login`
2. Ingresa credenciales y selecciona ubicación
3. Sistema valida mediante AJAX (`/user/validate`)
4. Si es válido: redirección a `/ingresoap` con datos pre-llenados
5. Si no es válido: mensaje de error

### 3. Registro de Ingreso
1. Usuario (autenticado o manual) accede a `/ingresoap`
2. Formulario puede venir pre-llenado desde login
3. Completa todos los campos obligatorios
4. Validación de RUT y otros campos
5. Registro se almacena en memoria
6. Confirmación de éxito

## 🛠️ Instalación y Configuración

### Prerrequisitos
- Java 21 LTS instalado
- Maven 3.6+ instalado
- Navegador web moderno

### Pasos de Instalación

1. **Clonar el repositorio:**
```bash
git clone [repository-url]
cd clases

2. **Acceder a la aplicación:**
```
http://localhost:8080
```

### Configuración de Entorno

Si necesitas configurar Java 21 permanentemente, ejecuta:
```powershell
.\setup-java21.ps1
```

## 🧪 Testing

### Tests Incluidos
- **ClasesApplicationTests:** Test de contexto Spring Boot
- Verificación de carga correcta de todos los componentes
- Validación de configuración de aplicación

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Test específico
./mvnw test -Dtest=ClasesApplicationTests
```

## 📊 Ubicaciones Soportadas

El sistema soporta las siguientes ubicaciones predefinidas:
- **DC APOQUINDO** - Data Center Apoquindo
- **DC SAN MARTIN** - Data Center San Martín
- **MC LA FLORIDA** - Mega Central La Florida
- **MC INDEPENDENCIA** - Mega Central Independencia
- **MC CHILOÉ** - Mega Central Chiloé
- **MC PROVIDENCIA** - Mega Central Providencia
- **MC PEDRO DE VALDIVIA** - Mega Central Pedro de Valdivia
- **MC MANUEL MONTT** - Mega Central Manuel Montt

## 🔐 Seguridad

### Medidas Implementadas
- ✅ Validación server-side de todos los inputs
- ✅ Sanitización de datos de entrada
- ✅ Validación de formato RUT
- ✅ Prevención de duplicados
- ✅ Validación de emails
- ✅ Protección CSRF (preparado)

### Consideraciones de Producción
- [ ] Implementar base de datos persistente
- [ ] Añadir Spring Security para autenticación real
- [ ] Encriptar contraseñas con BCrypt
- [ ] Implementar JWT para sesiones
- [ ] Añadir logs de auditoría
- [ ] Configurar HTTPS

## 🚀 Mejoras Futuras

### Funcionalidades Pendientes
- [ ] Persistencia en base de datos (JPA/Hibernate)
- [ ] Sistema de roles granular
- [ ] Reportes de ingresos en PDF/Excel
- [ ] Notificaciones por email
- [ ] API REST para integración
- [ ] Dashboard de analytics
- [ ] Sistema de aprobaciones workflow
- [ ] Integración con Active Directory

### Optimizaciones Técnicas
- [ ] Cache de usuarios frecuentes
- [ ] Paginación en listados
- [ ] Búsqueda y filtros avanzados
- [ ] Validación asíncrona mejorada
- [ ] Progressive Web App (PWA)
- [ ] Internacionalización (i18n)

## 📞 Soporte

Para soporte técnico o consultas sobre el sistema:

**IPSS - SERVICIO COMMODITIES**  
DATA CENTER & MEGA CENTRALES  
📍 Av. Ejemplo 1234, Providencia, Santiago  
📞 Tel: +56 2 1234 5678  
✉️ soporte@ipss.cl  

---

## 📄 Licencia

Este proyecto es propiedad de Telefónica Chile - IPSS Servicio Commodities.  
Todos los derechos reservados © 2025

---

**Versión:** 1.0.0  
**Última actualización:** Noviembre 2025  
**Java Runtime:** OpenJDK 21 LTS
