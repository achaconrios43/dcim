# Guion de Exposicion - 3 minutos

## 0:00 - 0:30 | Apertura
"Hoy revisamos el estado del modelo de datos de DCIM. El objetivo fue validar tablas, relaciones y dejar una vista clara para toma de decisiones de continuidad y escalamiento del sistema."

## 0:30 - 1:15 | Estado actual de la base
"El esquema fisico actual contiene 9 tablas principales. Tenemos un nucleo estable para usuarios, gestion de accesos, ingresos de tecnicos e inventario. Las relaciones criticas estan implementadas con claves foraneas en tablas puente, lo que asegura integridad referencial en las asociaciones de usuario."

"Adicionalmente, inventario ya cuenta con controles de unicidad e indices, lo que favorece calidad de dato y performance de consulta."

## 1:15 - 2:00 | Capacidad del modelo ampliado
"En paralelo, el codigo del proyecto ya incorpora un modelo mas avanzado: jerarquia de ubicacion por sitio y sala, puntos de medicion de temperatura, historico de mediciones, y planos de sala con elementos."

"Esto habilita trazabilidad operativa y ambiental, desde ubicacion fisica hasta comportamiento termico por punto."

## 2:00 - 2:35 | Brechas detectadas
"La principal brecha es de sincronizacion: parte del modelo ampliado existe en entidades de aplicacion, pero no aparece en el dump SQL revisado. Tambien hay coexistencia de campos textuales y referencias FK para ubicacion, lo que puede generar inconsistencia si no se normaliza."

## 2:35 - 3:00 | Cierre y decisiones propuestas
"Proponemos cuatro acciones inmediatas:"

1. "Versionar y ejecutar migraciones para alinear base fisica y modelo de aplicacion."
2. "Estandarizar ubicacion sobre claves foraneas."
3. "Emitir un nuevo export consolidado como evidencia oficial."
4. "Publicar diccionario de datos por modulo."

"Con esto, DCIM queda mejor preparado para crecimiento, auditoria y analitica confiable."