# Informe tecnico para reunion - Base de datos DCIM

## 1) Objetivo

Levantar un bosquejo claro del modelo de datos del proyecto DCIM para presentacion ejecutiva y tecnica, incluyendo:

- Todas las tablas detectadas en el dump actual de base de datos.
- Relaciones entre tablas (FK explicitas y relaciones de negocio).
- Modelo ampliado observado en el codigo del proyecto (modulos de temperaturas y plano de sala).
- Diagrama visual tipo ERD y diagrama de estructura del sistema.

## 2) Fuentes revisadas

- `dcimdb_export.sql`
- `DATABASE-SCHEMA.md`
- `sql/schema.sql` (schema consolidado — reemplaza los scripts de migración anteriores)
- Entidades JPA en `src/main/java/com/example/dcim/entity/`

## 3) Esquema fisico actual (dump SQL)

Tablas identificadas en la base de datos exportada:

1. `usuario`
2. `tipo_usuario`
3. `usuario_tipo_usuario`
4. `gestion_acceso`
5. `ingresoap`
6. `inventario`
7. `usuario_gestion_rel`
8. `usuario_ingresoap_rel`
9. `usuario_inventario_rel`

Relaciones FK explicitas en el dump:

1. `usuario_tipo_usuario.usuario_id` -> `usuario.id`
2. `usuario_tipo_usuario.tipo_usuario_id` -> `tipo_usuario.id`
3. `usuario_gestion_rel.usuario_id` -> `usuario.id`
4. `usuario_gestion_rel.gestion_acceso_id` -> `gestion_acceso.id`
5. `usuario_ingresoap_rel.usuario_id` -> `usuario.id`
6. `usuario_ingresoap_rel.ingresoap_id` -> `ingresoap.id`
7. `usuario_inventario_rel.usuario_id` -> `usuario.id`
8. `usuario_inventario_rel.inventario_id` -> `inventario.id`

## 4) Modelo ampliado del proyecto (codigo Java/JPA)

Ademas del esquema fisico del dump, el codigo modela tablas adicionales:

1. `sitio`
2. `sala`
3. `punto_medicion`
4. `medicion_temperatura`
5. `plano_sala`
6. `plano_sala_elemento`

Y extiende entidades existentes con referencias:

1. `gestion_acceso.sitio_id`, `gestion_acceso.sala_id`
2. `ingresoap.sitio_id`, `ingresoap.sala_id`
3. `inventario.sitio_id`, `inventario.sala_id`

Relaciones funcionales ampliadas:

1. `sitio` 1:N `sala`
2. `sala` 1:N `punto_medicion`
3. `punto_medicion` 1:N `medicion_temperatura`
4. `sala` 1:N `plano_sala`
5. `plano_sala` 1:N `plano_sala_elemento`
6. `sitio`/`sala` 1:N `gestion_acceso`, `ingresoap`, `inventario` (modelo logico en entidades)

## 5) Hallazgos clave para presentacion

1. El dump actual tiene un nucleo de 9 tablas con integridad referencial bien definida para relaciones de usuario.
2. El modelo de aplicacion ya incorpora dominio avanzado (temperaturas y planos), pero esas tablas no aparecen en el dump exportado revisado.
3. Hay coexistencia de campos de texto y referencias FK para ubicacion (ejemplo: `sitio` textual y `sitio_id` referencial en entidades), lo que sugiere una fase de transicion de modelo.
4. El modulo de inventario presenta madurez de indices y unicidad (`numero_serie`, `tag`).

## 6) Riesgos y recomendaciones

1. Alinear el esquema fisico con el modelo JPA antes de una presentacion tecnica final, para evitar brecha entre "lo que corre en codigo" y "lo que existe en DB".
2. Definir estrategia unica para ubicacion:
   - Opcion A: mantener solo referencias FK (`sitio_id`, `sala_id`).
   - Opcion B: mantener ambos y documentar reglas de sincronizacion.
3. Versionar cambios de esquema con migraciones (Flyway/Liquibase) para trazabilidad en reuniones de gobierno tecnico.
4. Generar un export de DB actualizado posterior a crear tablas de temperaturas/plano para tener evidencia unica y consistente.

## 7) Entregables visuales creados

1. ERD del esquema fisico actual:
   - `docs/diagramas/erd-dcim-actual.mmd`
2. ERD ampliado del modelo del proyecto:
   - `docs/diagramas/erd-dcim-aplicacion.mmd`
3. Diagrama de estructura del proyecto (similar a lamina de Figma para reunion):
   - `docs/diagramas/estructura-proyecto-dcim.mmd`

## 8) Uso en reunion

1. Mostrar primero `erd-dcim-actual.mmd` como "estado actual".
2. Mostrar luego `erd-dcim-aplicacion.mmd` como "roadmap del modelo objetivo".
3. Cerrar con `estructura-proyecto-dcim.mmd` para explicar como UI, controladores, servicios, repositorios y entidades se conectan con MySQL.

## 9) Mensaje ejecutivo sugerido (1 minuto)

"El sistema DCIM ya tiene una base estable con 9 tablas en produccion para usuarios, accesos, ingresos e inventario, con relaciones referenciales claras. El codigo ademas incorpora un modelo ampliado para temperaturas y planos de sala, lo que posiciona al proyecto para trazabilidad ambiental por punto de medicion y visualizacion espacial. La recomendacion inmediata es cerrar la brecha entre modelo logico y esquema fisico mediante migraciones controladas y un nuevo export consolidado de base de datos."