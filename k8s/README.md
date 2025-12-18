# Kubernetes Deployment - Sistema de Control de Acceso

Este directorio contiene los manifiestos de Kubernetes para desplegar la aplicación Spring Boot en un clúster de Kubernetes.

## 📁 Archivos

- **deployment.yaml**: Define el Deployment con 2 réplicas y configuración de recursos
- **service.yaml**: Servicio LoadBalancer para exponer la aplicación
- **secrets.yaml**: Secrets para credenciales de base de datos (⚠️ NO subir a Git)
- **configmap.yaml**: ConfigMap con configuración de la aplicación
- **ingress.yaml**: Ingress para acceso externo con TLS
- **hpa.yaml**: HorizontalPodAutoscaler para autoescalado

## 🚀 Despliegue Rápido

### 1. Configurar Secrets (IMPORTANTE)
```bash
# Editar el archivo secrets.yaml con tus credenciales reales
kubectl apply -f k8s/secrets.yaml
```

### 2. Desplegar todos los recursos
```bash
# Aplicar todos los manifiestos
kubectl apply -f k8s/

# O aplicar uno por uno en orden:
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/hpa.yaml
kubectl apply -f k8s/ingress.yaml  # Si tienes ingress controller
```

### 3. Verificar el despliegue
```bash
# Ver pods
kubectl get pods -l app=clases-app

# Ver servicios
kubectl get svc clases-app-service

# Ver logs
kubectl logs -f deployment/clases-app

# Ver eventos
kubectl get events --sort-by=.metadata.creationTimestamp
```

## 📊 Monitoreo

```bash
# Ver estado del deployment
kubectl get deployment clases-app

# Ver estado del HPA
kubectl get hpa clases-app-hpa

# Describir el deployment
kubectl describe deployment clases-app

# Ver métricas de recursos
kubectl top pods -l app=clases-app
```

## 🔄 Actualización

```bash
# Actualizar imagen
kubectl set image deployment/clases-app clases-app=achaconrios43/clases-app:new-tag

# Reiniciar pods
kubectl rollout restart deployment/clases-app

# Ver estado del rollout
kubectl rollout status deployment/clases-app

# Historial de rollouts
kubectl rollout history deployment/clases-app

# Rollback a versión anterior
kubectl rollout undo deployment/clases-app
```

## 🔍 Troubleshooting

```bash
# Ver logs detallados
kubectl logs deployment/clases-app --all-containers=true

# Acceder a un pod
kubectl exec -it deployment/clases-app -- sh

# Ver eventos de error
kubectl get events --field-selector type=Warning

# Describir pod específico
kubectl describe pod <pod-name>
```

## 🌐 Acceso a la Aplicación

### Con LoadBalancer:
```bash
# Obtener IP externa
kubectl get svc clases-app-service

# La aplicación estará disponible en: http://<EXTERNAL-IP>
```

### Con Ingress:
```bash
# Configurar DNS para apuntar a tu Ingress Controller
# Acceder a: https://clases-app.example.com
```

### Port-Forward (para desarrollo local):
```bash
kubectl port-forward svc/clases-app-service 8082:80

# Acceder a: http://localhost:8082
```

## 🔐 Seguridad

### Crear Secrets de forma segura:
```bash
# Desde archivos
kubectl create secret generic clases-secrets \
  --from-literal=database-url='jdbc:mysql://...' \
  --from-literal=database-username='user' \
  --from-literal=database-password='password'

# Desde archivo .env
kubectl create secret generic clases-secrets --from-env-file=.env
```

### Usar Sealed Secrets (recomendado para producción):
```bash
# Instalar sealed-secrets controller
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.24.0/controller.yaml

# Encriptar secret
kubeseal --format=yaml < secrets.yaml > sealed-secrets.yaml
kubectl apply -f sealed-secrets.yaml
```

## 📦 Requisitos

- Kubernetes 1.24+
- kubectl configurado
- Ingress Controller (nginx recomendado) para ingress.yaml
- Metrics Server para HPA
- Cert-manager para TLS automático (opcional)

## 🎯 Variables de Entorno Importantes

Las siguientes variables se configuran en el deployment:
- `SPRING_PROFILES_ACTIVE`: Perfil de Spring Boot (production)
- `JAVA_OPTS`: Opciones de JVM
- `SPRING_DATASOURCE_URL`: URL de base de datos
- `SPRING_DATASOURCE_USERNAME`: Usuario de DB
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de DB

## 📝 Notas

1. **NO** subir `secrets.yaml` a repositorios públicos
2. Ajustar recursos según necesidades (requests/limits)
3. Configurar el dominio en `ingress.yaml`
4. Revisar y ajustar las reglas de autoescalado en `hpa.yaml`
5. Para producción, usar un sistema de gestión de secrets como Vault o Sealed Secrets
