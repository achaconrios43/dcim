# 🚀 Configuración Manual de Koyeb (Recomendado)

## ⚠️ IMPORTANTE: Configuración Paso a Paso

Sigue estos pasos exactos en la consola web de Koyeb:

### 1. Ve a https://app.koyeb.com/services

### 2. Selecciona tu servicio existente (few-laureen-webipss-1b5927a6)

### 3. Click en "Settings" o "Edit Service"

### 4. Configura estos valores EXACTOS:

#### **General:**
- **Service name**: `clases-spring-boot` (o deja el actual)
- **Service type**: Web Service
- **Docker image**: `achaconrios43/clases-app:latest`

#### **Instance:**
- **Instance type**: **Micro** (1 vCPU, 1GB RAM) - ⚠️ NO usar Nano
- **Regions**: Washington (was)
- **Scaling**: Min 1, Max 1 (para empezar)

#### **Ports:**
- **Port**: `8082`
- **Protocol**: HTTP
- **Expose publicly**: ✅ Yes

#### **Environment Variables:**
Agregar estas variables:
```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx384m -Xms192m -XX:+UseContainerSupport
PORT=8082
```

**OPCIONAL** - Si quieres usar variables de entorno para la DB:
```
SPRING_DATASOURCE_URL=jdbc:mysql://gateway01.us-east-1.prod.aws.tidbcloud.com:4000/test?sslMode=VERIFY_IDENTITY&useSSL=true
SPRING_DATASOURCE_USERNAME=Tx5LgXBUqorHfYX.root
SPRING_DATASOURCE_PASSWORD=SGRAbutT9e8sGwdD
```

#### **Health Checks:**
- **Type**: HTTP
- **Path**: `/actuator/health`
- **Port**: `8082`
- **Initial delay**: `180` segundos ⚠️ IMPORTANTE
- **Timeout**: `10` segundos
- **Interval**: `60` segundos

### 5. Click en "Save" o "Deploy"

### 6. Espera 3-5 minutos mientras:
- Se descarga la imagen Docker
- Spring Boot arranca (tarda ~2 minutos)
- Los health checks pasan

## 🔍 Verificar Logs

Mientras se despliega, revisa los logs en tiempo real:
1. Click en tu servicio
2. Tab "Logs"
3. Busca estos mensajes:

**✅ Correcto:**
```
Started ClasesApplication in X seconds
Tomcat started on port(s): 8082
```

**❌ Error común:**
```
Error creating bean
Connection refused
OutOfMemoryError
```

## 🐛 Si sigue fallando:

### Opción 1: Verificar la imagen Docker
```powershell
# Ver si la imagen se construyó correctamente en GitHub Actions
# https://github.com/achaconrios43/clases/actions
```

### Opción 2: Probar localmente (si tienes Docker)
```bash
docker pull achaconrios43/clases-app:latest
docker run -p 8082:8082 -e SPRING_PROFILES_ACTIVE=production achaconrios43/clases-app:latest
```

Luego abre: http://localhost:8082/actuator/health

### Opción 3: Aumentar recursos temporalmente
- Cambiar a instancia **Small** (2 vCPU, 2GB RAM)
- Esto ayuda a descartar problemas de memoria

### Opción 4: Desactivar health checks temporalmente
- En Koyeb, deshabilita los health checks por completo
- Espera a ver si la app arranca
- Revisa los logs

## 📝 Checklist antes de desplegar:

- [ ] Imagen Docker construida en GitHub Actions (check verde)
- [ ] Instancia tipo: **Micro** o superior
- [ ] Health check inicial delay: **180 segundos**
- [ ] Puerto: **8082**
- [ ] Variables de entorno configuradas
- [ ] Base de datos TiDB accesible

## ⚡ Comandos útiles para verificar:

Ver status de GitHub Actions:
```
https://github.com/achaconrios43/clases/actions
```

Verificar que la imagen existe en Docker Hub:
```
https://hub.docker.com/r/achaconrios43/clases-app/tags
```

## 💡 Tip Final

Si después de todos estos ajustes sigue fallando, el problema puede ser:
1. **Base de datos no accesible** desde Koyeb (firewall)
2. **Puerto incorrecto** en la configuración
3. **Imagen Docker corrupta** - hacer rebuild completo

En ese caso, prueba:
1. Eliminar el servicio completamente en Koyeb
2. Crear uno nuevo desde cero con estos parámetros
3. Esperar pacientemente los 3-4 minutos de inicio
