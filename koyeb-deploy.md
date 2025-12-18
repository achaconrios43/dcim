# 🚀 Despliegue en Koyeb

Guía completa para desplegar la aplicación Spring Boot en Koyeb.

## 📋 Prerrequisitos

1. Cuenta en [Koyeb](https://www.koyeb.com/)
2. [Koyeb CLI](https://www.koyeb.com/docs/cli) instalada (opcional)
3. Imagen Docker publicada en Docker Hub: `achaconrios43/clases-app`
4. Base de datos MySQL accesible (puedes usar Railway, PlanetScale, etc.)

## 🛠️ Instalación de Koyeb CLI (Opcional)

### Windows (PowerShell):
```powershell
# Descargar e instalar Koyeb CLI
iwr https://www.koyeb.com/download/cli/windows -OutFile koyeb.exe
Move-Item koyeb.exe C:\Windows\System32\koyeb.exe
```

### Linux/macOS:
```bash
curl -fsSL https://www.koyeb.com/download/cli/install.sh | sh
```

### Autenticación:
```bash
koyeb login
```

## 🌐 Método 1: Despliegue desde la Consola Web

### 1. Acceder a Koyeb
- Ve a https://app.koyeb.com
- Inicia sesión o crea una cuenta

### 2. Crear Nueva Aplicación
- Click en **"Create App"**
- Selecciona **"Docker"** como source

### 3. Configurar la Aplicación
```
Docker Image: achaconrios43/clases-app:latest
Service Name: clases-spring-boot
Port: 8082
Instance Type: Nano (o según necesites)
Region: Washington (o el más cercano)
```

### 4. Configurar Variables de Entorno
Agregar las siguientes variables en la sección **Environment Variables**:

```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m -Xms256m
PORT=8082
SPRING_DATASOURCE_URL=jdbc:mysql://tu-host:3306/clases_db
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_contraseña
```

### 5. Configurar Health Check
```
Type: HTTP
Path: /actuator/health
Port: 8082
Interval: 30s
Timeout: 5s
Grace Period: 60s
```

### 6. Desplegar
- Click en **"Deploy"**
- Espera a que el despliegue termine
- Obtén tu URL: `https://clases-spring-boot-tu-usuario.koyeb.app`

## 💻 Método 2: Despliegue con Koyeb CLI

### Paso 1: Crear Secrets
```bash
# Crear secrets para credenciales de base de datos
koyeb secret create DATABASE_URL --value "jdbc:mysql://host:3306/clases_db"
koyeb secret create DATABASE_USERNAME --value "usuario"
koyeb secret create DATABASE_PASSWORD --value "contraseña"
```

### Paso 2: Desplegar desde archivo de configuración
```bash
# Usar el archivo .koyeb.yaml
koyeb app init clases-app --config .koyeb.yaml

# O desplegar directamente
koyeb service create clases-spring-boot \
  --app clases-app \
  --docker achaconrios43/clases-app:latest \
  --ports 8082:http \
  --routes /:8082 \
  --env SPRING_PROFILES_ACTIVE=production \
  --env JAVA_OPTS="-Xmx512m -Xms256m" \
  --env PORT=8082 \
  --env SPRING_DATASOURCE_URL=@DATABASE_URL \
  --env SPRING_DATASOURCE_USERNAME=@DATABASE_USERNAME \
  --env SPRING_DATASOURCE_PASSWORD=@DATABASE_PASSWORD \
  --instance-type nano \
  --regions was \
  --health-checks http:8082:/actuator/health
```

### Paso 3: Verificar el despliegue
```bash
# Ver estado del servicio
koyeb service list

# Ver logs
koyeb service logs clases-spring-boot --follow

# Ver detalles
koyeb service describe clases-spring-boot
```

## 📊 Gestión del Servicio

### Ver logs en tiempo real:
```bash
koyeb service logs clases-spring-boot -f
```

### Actualizar la aplicación:
```bash
# Después de hacer push de nueva imagen Docker
koyeb service redeploy clases-spring-boot
```

### Escalar la aplicación:
```bash
koyeb service scale clases-spring-boot --min 1 --max 5
```

### Cambiar tamaño de instancia:
```bash
koyeb service update clases-spring-boot --instance-type small
```

### Agregar/actualizar variable de entorno:
```bash
koyeb service update clases-spring-boot \
  --env NEW_VAR=value
```

### Pausar servicio:
```bash
koyeb service pause clases-spring-boot
```

### Reanudar servicio:
```bash
koyeb service resume clases-spring-boot
```

### Eliminar servicio:
```bash
koyeb service delete clases-spring-boot
```

## 🔄 Integración con GitHub Actions

El workflow ya está configurado en `.github/workflows/docker-publish.yml`.

Para despliegue automático a Koyeb, agrega estos secrets en GitHub:
- `KOYEB_TOKEN`: Token de API de Koyeb

Y agrega este job al workflow:

```yaml
  deploy-to-koyeb:
    needs: docker
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
      - name: Deploy to Koyeb
        uses: koyeb-community/deploy-action@v1
        with:
          api_token: ${{ secrets.KOYEB_TOKEN }}
          service_name: clases-spring-boot
          docker_image: achaconrios43/clases-app:latest
```

## 📈 Tipos de Instancia Koyeb

| Tipo   | vCPU | RAM   | Precio/mes | Uso recomendado |
|--------|------|-------|------------|------------------|
| Nano   | 0.1  | 512MB | ~$0       | Desarrollo/Testing |
| Micro  | 0.5  | 1GB   | ~$5       | Apps pequeñas |
| Small  | 1    | 2GB   | ~$10      | Apps medianas |
| Medium | 2    | 4GB   | ~$20      | Apps grandes |
| Large  | 4    | 8GB   | ~$40      | Apps intensivas |

## 🌍 Regiones Disponibles

- `was` - Washington, DC (US East)
- `fra` - Frankfurt (Europe)
- `par` - Paris (Europe)
- `sin` - Singapore (Asia)

## 🔒 Configuración de Base de Datos

### Opción 1: Railway
```bash
# URL de conexión típica de Railway
jdbc:mysql://containers-us-west-xxx.railway.app:6789/railway
```

### Opción 2: PlanetScale
```bash
# URL de conexión de PlanetScale
jdbc:mysql://aws.connect.psdb.cloud/tu-db?sslMode=VERIFY_IDENTITY
```

### Opción 3: Azure Database for MySQL
```bash
jdbc:mysql://tu-server.mysql.database.azure.com:3306/clases_db?useSSL=true
```

## 🐛 Troubleshooting

### Ver logs de errores:
```bash
koyeb service logs clases-spring-boot --type error
```

### Verificar health checks:
```bash
koyeb service describe clases-spring-boot | grep -A 10 health
```

### Problema: Out of Memory
```bash
# Aumentar memoria en JAVA_OPTS
koyeb service update clases-spring-boot \
  --env JAVA_OPTS="-Xmx1g -Xms512m"
  
# O cambiar a instancia más grande
koyeb service update clases-spring-boot --instance-type small
```

### Problema: Base de datos no conecta
- Verifica que la base de datos permita conexiones externas
- Revisa que las credenciales sean correctas
- Asegúrate que el firewall permita la IP de Koyeb

### Problema: App tarda en arrancar
- Aumenta el `grace_period` en health checks a 120s
- Verifica los logs de inicio de Spring Boot

## 📝 Comandos Útiles

```bash
# Ver todas las apps
koyeb app list

# Ver información de la app
koyeb app describe clases-app

# Ver métricas
koyeb service metrics clases-spring-boot

# Ver eventos
koyeb service events clases-spring-boot

# Exportar configuración
koyeb service get clases-spring-boot -o yaml > backup.yaml
```

## 🎯 URLs de Acceso

Después del despliegue, tu aplicación estará disponible en:
```
https://clases-spring-boot-<tu-koyeb-id>.koyeb.app
```

Puedes también configurar un dominio personalizado desde la consola de Koyeb.

## 🔗 Enlaces Útiles

- [Documentación de Koyeb](https://www.koyeb.com/docs)
- [Koyeb CLI Reference](https://www.koyeb.com/docs/cli)
- [Koyeb Status](https://status.koyeb.com/)
- [Pricing](https://www.koyeb.com/pricing)

## 💡 Tips

1. **Free Tier**: Koyeb ofrece una instancia nano gratuita
2. **Auto-deploy**: Conecta tu repo de GitHub para despliegue automático
3. **Custom Domain**: Puedes usar tu propio dominio gratuitamente
4. **HTTPS**: SSL/TLS incluido automáticamente
5. **Logs**: Los logs se mantienen por 7 días en el free tier
6. **Backup**: Koyeb hace backups automáticos de tu configuración
