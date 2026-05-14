/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sitio` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_modificacion` datetime(6) DEFAULT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsagkmllonihihkcfbotjpmt1m` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
INSERT INTO `sitio` (`id`, `activo`, `descripcion`, `fecha_creacion`, `fecha_modificacion`, `nombre`) VALUES (2,_binary '','Data Center','2026-05-13 04:32:28.113825','2026-05-13 04:32:28.113825','DC San Martin'),(3,_binary '','','2026-05-13 23:10:47.621609','2026-05-13 23:10:47.621609','DC Apoquindo');
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sala` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_modificacion` datetime(6) DEFAULT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sitio_id` bigint NOT NULL,
  `tipo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg9s7pau743a1sxggqkwi6hrl` (`sitio_id`,`nombre`),
  CONSTRAINT `FK1c5fvgwtqwvgkom9kurf3lw7w` FOREIGN KEY (`sitio_id`) REFERENCES `sitio` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
INSERT INTO `sala` (`id`, `activo`, `descripcion`, `fecha_creacion`, `fecha_modificacion`, `nombre`, `sitio_id`, `tipo`) VALUES (2,_binary '','','2026-05-13 05:05:15.737882','2026-05-13 05:05:15.737882','CPD',2,'Sala TI'),(3,_binary '','','2026-05-13 23:11:30.405338','2026-05-13 23:11:30.405338','Mainframe',3,'Sala TI');
