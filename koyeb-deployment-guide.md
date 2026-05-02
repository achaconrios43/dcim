# Guía de Despliegue en Koyeb

Este documento describe el proceso para desplegar la aplicación DCIM en **Koyeb**.

## Requisitos Previos

- Cuenta en [Koyeb](https://www.koyeb.com/)
- Docker Hub account (para pushear la imagen)
- GitHub token (para CI/CD)
- Koyeb CLI instalado (opcional)

## Proceso de Despliegue

### Paso 1: Preparar la Imagen Docker

La imagen está lista en el `Dockerfile` con:
- **Base Image:** Red Hat UBI 9 con OpenJDK 21
- **Build Stage:** Maven 3.9.9 compila el JAR
- **Runtime Stage:** Optimizado y seguro (usuario no-root)

### Paso 2: Build y Push a Docker Hub

```bash
# Build de la imagen
docker build -t achaconrios43/dcim-app:latest .

# Login en Docker Hub
docker login

# Push de la imagen
docker push achaconrios43/dcim-app:latest
```

### Paso 3: Configurar Variables de Entorno en Koyeb

Crea un servicio en Koyeb y configura estas variables de entorno:

| Variable | Valor | Descripción |
|----------|-------|-----------|
| `SPRING_PROFILES_ACTIVE` | `production` | Perfil de producción |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<host>:<port>/dcimdb?useSSL=true&serverTimezone=UTC` | URL de BD en producción |
| `SPRING_DATASOURCE_USERNAME` | `<usuario>` | Usuario de BD |
| `SPRING_DATASOURCE_PASSWORD` | `<contraseña>` | Contraseña de BD |
| `PORT` | `8082` | Puerto de escucha |
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | Opciones JVM |

### Paso 4: Crear el Servicio en Koyeb

#### Opción A — Dashboard Koyeb

1. Ve a [Koyeb Dashboard](https://app.koyeb.com)
2. **Create Service**
3. Selecciona **Docker** como source
4. Ingresa: `achaconrios43/dcim-app:latest`
5. Configura:
   - **Name:** `dcim-spring-boot`
   - **Port:** `8082`
   - **Region:** `was` (Washington)
   - **Instance Type:** `nano`
   - **Health Check:** `/actuator/health`
6. Agrega las variables de entorno
7. Deploy

#### Opción B — Koyeb CLI

```bash
# Instalar Koyeb CLI
curl -fsSL https://www.koyeb.com/download/cli/install.sh | sh

# Login
koyeb auth login

# Deploy
koyeb service create dcim-spring-boot \
  --app dcim-app \
  --docker achaconrios43/dcim-app:latest \
  --ports 8082:http \
  --routes /:8082 \
  --env SPRING_PROFILES_ACTIVE=production \
  --env SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/dcimdb?useSSL=true \
  --env SPRING_DATASOURCE_USERNAME=<user> \
  --env SPRING_DATASOURCE_PASSWORD=<pass> \
  --env JAVA_OPTS="-Xmx512m -Xms256m" \
  --instance-type nano \
  --regions was \
  --health-checks http:8082:/actuator/health
```

### Paso 5: CI/CD Automático

El workflow en `.github/workflows/koyeb-deploy.yml` automatiza el despliegue:

1. **Trigger:** Cuando el workflow `Build and Push Docker Image` termina exitosamente
2. **Acción:** Redeploy o crea nuevo servicio en Koyeb
3. **Requisito:** Agregar secret `KOYEB_TOKEN` en GitHub

#### Configurar GitHub Secret

1. Ve a GitHub Repo → **Settings → Secrets and variables → Actions**
2. **New repository secret**
3. Name: `KOYEB_TOKEN`
4. Value: Tu token de Koyeb (obtenlo de [Koyeb Dashboard](https://app.koyeb.com/account/api-tokens))
5. **Add secret**

### Paso 6: Validar el Despliegue

Una vez deployed:

1. Ve a tu servicio en Koyeb Dashboard
2. Abre la URL pública (ej: `https://dcim-app-xxxxx.koyeb.app`)
3. Accede a `/login` para verificar conectividad
4. Revisa logs en **Koyeb → Logs** para debugging

## Monitoreo y Logs

En **Koyeb Dashboard:**
- **Logs:** Panel izquierdo → **Logs**
- **Health:** Panel izquierdo → **Health checks**
- **Metrics:** Panel izquierdo → **Metrics** (si aplica)

## Troubleshooting

### Error: "Connection refused"
- Verifica que `SPRING_DATASOURCE_URL` es accesible desde Koyeb
- Si BD está local, no será alcanzable; usa una BD en la nube (ej: AWS RDS, Azure Database)

### Error: "Health check failing"
- El endpoint `/actuator/health` debe retornar 200 OK
- Verifica logs en Koyeb

### Error: "Out of memory"
- Aumenta `JAVA_OPTS` (ej: `-Xmx1024m`)

## Escalado

Para escalar:

1. **Replicas:** En Koyeb → **Edit Service** → increase instance count
2. **Instance Type:** Cambia de `nano` a `small` o superior
3. **Región:** Agrega regiones para redundancia

## Rollback

Si hay problemas:

1. Ve a Koyeb Dashboard
2. **Deployments**
3. Selecciona una versión anterior
4. **Redeploy**

---

**Notas:**
- Red Hat UBI 9 es más grande (~500MB) pero certificado por Red Hat
- Para desarrollo local, sigue usando `./mvnw.cmd spring-boot:run`
- Para producción, usa el despliegue en Koyeb
