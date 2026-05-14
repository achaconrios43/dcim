/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gestion_acceso` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aprobadores_pendientes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `comentario_final` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `comentario_inicio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `comentario_intermedio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `enviado_a_procesos` bit(1) DEFAULT NULL,
  `estado_aprobacion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_cierre_gestion` date DEFAULT NULL,
  `fecha_envio_procesos` date DEFAULT NULL,
  `fecha_fin_remedy` date DEFAULT NULL,
  `fecha_inicio_actividad` date DEFAULT NULL,
  `fecha_inicio_remedy` date DEFAULT NULL,
  `fecha_registro` date DEFAULT NULL,
  `fecha_respuesta_cliente` date DEFAULT NULL,
  `fecha_termino_actividad` date DEFAULT NULL,
  `gestion_realizada` bit(1) DEFAULT NULL,
  `hora_cierre_gestion` time(6) DEFAULT NULL,
  `hora_envio_procesos` time(6) DEFAULT NULL,
  `hora_fin_remedy` time(6) DEFAULT NULL,
  `hora_inicio_actividad` time(6) DEFAULT NULL,
  `hora_inicio_remedy` time(6) DEFAULT NULL,
  `hora_registro` time(6) DEFAULT NULL,
  `hora_respuesta_cliente` time(6) DEFAULT NULL,
  `hora_termino_actividad` time(6) DEFAULT NULL,
  `nombre_actividad` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `numero_ticket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `respuesta_cliente` bit(1) DEFAULT NULL,
  `sitio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ticket_cerrado` bit(1) DEFAULT NULL,
  `usuario_ingresa` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sala_id` bigint DEFAULT NULL,
  `sitio_id` bigint DEFAULT NULL,
  `usuario_registra_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoc7h830yddsahlsaetudmcgt6` (`sala_id`),
  KEY `FK87r7aahguxn420ahh3dmjacqa` (`sitio_id`),
  KEY `idx_gestion_usuario_registra` (`usuario_registra_id`),
  CONSTRAINT `FK87r7aahguxn420ahh3dmjacqa` FOREIGN KEY (`sitio_id`) REFERENCES `sitio` (`id`),
  CONSTRAINT `fk_gestion_usuario_registra` FOREIGN KEY (`usuario_registra_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FKoc7h830yddsahlsaetudmcgt6` FOREIGN KEY (`sala_id`) REFERENCES `sala` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingresoap` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actividad_remedy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` bit(1) NOT NULL,
  `aprobador` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cargo_tecnico` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `coordenadas_gps` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `empresa_contratista` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `empresa_demandante` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `escolta` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_fin_ficticia` date DEFAULT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_registro` datetime(6) DEFAULT NULL,
  `fecha_segunda_supervision` date DEFAULT NULL,
  `fecha_supervision_media` date DEFAULT NULL,
  `fecha_termino` date DEFAULT NULL,
  `foto_tecnico` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `guia_despacho` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hora_fin_ficticia` time(6) DEFAULT NULL,
  `hora_inicio` time(6) NOT NULL,
  `hora_segunda_supervision` time(6) DEFAULT NULL,
  `hora_supervision_media` time(6) DEFAULT NULL,
  `hora_termino` time(6) DEFAULT NULL,
  `motivo_ingreso` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_tecnico` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_usuario` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_ticket` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rack_ingresa` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rut_tecnico` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sala_ingresa` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sala_remedy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `segunda_supervision_realizada` bit(1) DEFAULT NULL,
  `sitio_ingreso` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_ticket` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `turno` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `sala_id` bigint DEFAULT NULL,
  `sitio_id` bigint DEFAULT NULL,
  `usuario_registra_id` bigint DEFAULT NULL,
  `aprobador_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7a249avj0upwv5j4r6sc9o73f` (`sala_id`),
  KEY `FKpueyqk38kh0ew0tuoqg0wq43p` (`sitio_id`),
  KEY `idx_ingreso_usuario_registra` (`usuario_registra_id`),
  KEY `idx_ingreso_aprobador_id` (`aprobador_id`),
  CONSTRAINT `FK7a249avj0upwv5j4r6sc9o73f` FOREIGN KEY (`sala_id`) REFERENCES `sala` (`id`),
  CONSTRAINT `fk_ingreso_aprobador` FOREIGN KEY (`aprobador_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_ingreso_usuario_registra` FOREIGN KEY (`usuario_registra_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FKpueyqk38kh0ew0tuoqg0wq43p` FOREIGN KEY (`sitio_id`) REFERENCES `sitio` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
INSERT INTO `ingresoap` (`id`, `actividad_remedy`, `activo`, `aprobador`, `cargo_tecnico`, `coordenadas_gps`, `empresa_contratista`, `empresa_demandante`, `escolta`, `fecha_fin_ficticia`, `fecha_inicio`, `fecha_registro`, `fecha_segunda_supervision`, `fecha_supervision_media`, `fecha_termino`, `foto_tecnico`, `guia_despacho`, `hora_fin_ficticia`, `hora_inicio`, `hora_segunda_supervision`, `hora_supervision_media`, `hora_termino`, `motivo_ingreso`, `nombre_tecnico`, `nombre_usuario`, `numero_ticket`, `rack_ingresa`, `rut_tecnico`, `sala_ingresa`, `sala_remedy`, `segunda_supervision_realizada`, `sitio_ingreso`, `tipo_ticket`, `turno`, `sala_id`, `sitio_id`, `usuario_registra_id`, `aprobador_id`) VALUES (8,'Revision Operativa',_binary '\0','Operador Turno','Tecnico en Redes',NULL,'Zener','Telefonica','Operador de Turno',NULL,'2026-05-13','2026-05-13 06:56:13.431000','2026-05-13',NULL,'2026-05-13',NULL,'',NULL,'01:05:00.000000','02:50:00.000000',NULL,'02:56:00.000000','Inspectiva','judtih linco','Arturo Chac├│n R├¡os','Visita Inspectiva','rack 4J','18.052.030-9','CPD','Salas TI',_binary '','DC San Martin','Visita Inspectiva','AM',NULL,NULL,NULL,NULL),(9,'revision de temepraturas',_binary '\0','Arturo Chac├│n','T├®cnico ',NULL,'inelcom','telefonca','Guardia',NULL,'2026-05-13','2026-05-13 20:34:51.347000','2026-05-13','2026-05-13','2026-05-13',NULL,'',NULL,'03:00:00.000000','16:34:00.000000','04:00:00.000000','16:34:00.000000','Actividad Rutinaria','judith linco','Arturo Chac├│n R├¡os','N/A','rj','180520309','CPD','Salas TI',_binary '',NULL,'Visita Inspectiva','AM',NULL,NULL,NULL,NULL);
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sala` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `marca` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `modelo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_serie` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cliente` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `coordenadas` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_rack` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ubicacion_ur` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ur_utilizada` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `capacidad_ur_rack` int DEFAULT NULL,
  `numero_temporal` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hotname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_alarma` date DEFAULT NULL,
  `alarma_hardware` bit(1) DEFAULT NULL,
  `alarma_ventilador` bit(1) DEFAULT NULL,
  `alarma_fuente_poder` bit(1) DEFAULT NULL,
  `alarma_hdd` bit(1) DEFAULT NULL,
  `comentarios_alarma` text COLLATE utf8mb4_unicode_ci,
  `ticket_relacion` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `flujo_aire` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `peso_equipo_kg` decimal(8,2) DEFAULT NULL,
  `fuentes_poder` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipos_enchufe` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observacion_tipo_enchufe` text COLLATE utf8mb4_unicode_ci,
  `potencia_consumo_watts` decimal(10,2) DEFAULT NULL,
  `direccion_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sitio` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sala_id` bigint DEFAULT NULL,
  `sitio_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_inventario_numero_serie` (((case when ((`numero_serie` = _utf8mb4'0') or (`numero_serie` is null)) then NULL else `numero_serie` end))),
  UNIQUE KEY `ux_inventario_tag` (((case when ((`tag` = _utf8mb4'0') or (`tag` is null)) then NULL else `tag` end))),
  KEY `idx_numero_serie` (`numero_serie`),
  KEY `idx_tag` (`tag`),
  KEY `idx_cliente` (`cliente`),
  KEY `idx_estado` (`estado`),
  KEY `idx_sala` (`sala`),
  KEY `FK8os6wgkalifwi52foew6goxku` (`sala_id`),
  KEY `FKokevhptdfk0el82gfl2ho8efa` (`sitio_id`),
  CONSTRAINT `FK8os6wgkalifwi52foew6goxku` FOREIGN KEY (`sala_id`) REFERENCES `sala` (`id`),
  CONSTRAINT `FKokevhptdfk0el82gfl2ho8efa` FOREIGN KEY (`sitio_id`) REFERENCES `sitio` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
INSERT INTO `inventario` (`id`, `sala`, `tipo`, `marca`, `modelo`, `numero_serie`, `tag`, `cliente`, `coordenadas`, `nombre_rack`, `ubicacion_ur`, `ur_utilizada`, `capacidad_ur_rack`, `numero_temporal`, `hotname`, `estado`, `fecha_alarma`, `alarma_hardware`, `alarma_ventilador`, `alarma_fuente_poder`, `alarma_hdd`, `comentarios_alarma`, `ticket_relacion`, `observaciones`, `flujo_aire`, `peso_equipo_kg`, `fuentes_poder`, `tipos_enchufe`, `observacion_tipo_enchufe`, `potencia_consumo_watts`, `direccion_ip`, `fecha_creacion`, `fecha_modificacion`, `sitio`, `sala_id`, `sitio_id`) VALUES (1,'CPD','RACK','IBM','9306-420','23-A2791','TE107525','TELEFONICA','S4','9','','',42,'','','Operativo',NULL,NULL,NULL,NULL,NULL,'','','','',NULL,'','','',16280.00,'','2026-05-14 04:46:15','2026-05-14 04:46:15','DC San Martin',2,2),(2,'CPD','UR OCUPADA','N/A','N/A','N/A','N/A','N/A','S4','9','42','1',NULL,'N/A','N/A','UR Ocupada',NULL,NULL,NULL,NULL,NULL,'','','UR OCUPADA NO SE PUEDEN INSTALAR EQUIPOS PORQUE PASAN CABLES EN SU INTERIOR','N/A',NULL,'N/A','N/A','N/A',NULL,'N/A','2026-05-14 04:50:34','2026-05-14 04:50:34','DC San Martin',2,2),(3,'CPD','PATCH PANNEL','PANDUIT','','0','0','','S4','9','41','1',NULL,'','','Operativo',NULL,NULL,NULL,NULL,NULL,'','','','',2.00,'','','',NULL,'','2026-05-14 04:55:28','2026-05-14 04:55:28','DC San Martin',2,2);
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apellido` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `creat_at` datetime(6) DEFAULT NULL,
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rut` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `update_at` datetime(6) DEFAULT NULL,
  `modulos_permitidos` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK5171l57faosmj8myawaucatdw` (`email`),
  UNIQUE KEY `UKjx61a01wwidax9iafoa3xj22i` (`rut`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
INSERT INTO `usuario` (`id`, `apellido`, `creat_at`, `email`, `nombre`, `password`, `rol`, `rut`, `update_at`, `modulos_permitidos`) VALUES (4,'Chac├│n R├¡os','2026-05-01 19:25:57.000000','achaconrios@gmail.com','Arturo','$2a$10$0DfDlXv6hrXU.4MDUa7Reu6j36ISK8QzgRPDtazOmOBE6eKfDyZWq','ADMIN','15.441.473-8','2026-05-01 19:25:57.000000',NULL);
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_gestion_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `gestion_acceso_id` bigint NOT NULL,
  `tipo_relacion` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_gestion_rel` (`usuario_id`,`gestion_acceso_id`,`tipo_relacion`),
  KEY `fk_ugr_gestion` (`gestion_acceso_id`),
  CONSTRAINT `fk_ugr_gestion` FOREIGN KEY (`gestion_acceso_id`) REFERENCES `gestion_acceso` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ugr_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_ingresoap_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `ingresoap_id` bigint NOT NULL,
  `tipo_relacion` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_ingresoap_rel` (`usuario_id`,`ingresoap_id`,`tipo_relacion`),
  KEY `fk_uir_ingreso` (`ingresoap_id`),
  CONSTRAINT `fk_uir_ingreso` FOREIGN KEY (`ingresoap_id`) REFERENCES `ingresoap` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_uir_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_inventario_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `inventario_id` bigint NOT NULL,
  `tipo_relacion` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_inventario_rel` (`usuario_id`,`inventario_id`,`tipo_relacion`),
  KEY `fk_uivr_inventario` (`inventario_id`),
  CONSTRAINT `fk_uivr_inventario` FOREIGN KEY (`inventario_id`) REFERENCES `inventario` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_uivr_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_tipo_usuario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `tipo_usuario_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_tipo` (`usuario_id`,`tipo_usuario_id`),
  KEY `fk_usuario_tipo_tipo` (`tipo_usuario_id`),
  CONSTRAINT `fk_usuario_tipo_tipo` FOREIGN KEY (`tipo_usuario_id`) REFERENCES `tipo_usuario` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_usuario_tipo_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_usuario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tipo_usuario_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
