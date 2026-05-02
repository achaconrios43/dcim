-- Agrega la columna sitio a la tabla inventario si no existe
SET @col_exists = (
	SELECT COUNT(*)
	FROM information_schema.COLUMNS
	WHERE TABLE_SCHEMA = DATABASE()
	  AND TABLE_NAME = 'inventario'
	  AND COLUMN_NAME = 'sitio'
);

SET @sql = IF(
	@col_exists = 0,
	'ALTER TABLE inventario ADD COLUMN sitio VARCHAR(100) AFTER sala',
	'SELECT "La columna sitio ya existe"'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
