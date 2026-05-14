package com.example.dcim.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dcim.entity.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    // Búsquedas por campos únicos
    Optional<Inventario> findByNumeroSerie(String numeroSerie);
    Optional<Inventario> findByTag(String tag);
    
    // Búsquedas por otros campos
    List<Inventario> findByCliente(String cliente);
    List<Inventario> findByEstado(String estado);
    List<Inventario> findBySala(String sala);
    List<Inventario> findByMarca(String marca);
    long countBySala(String sala);
    List<Inventario> findByTipo(String tipo);
    
    // Búsquedas complejas
    @Query("SELECT i FROM Inventario i WHERE " +
           "LOWER(i.numeroSerie) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.tag) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.sitio) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.cliente) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.estado) LIKE LOWER(CONCAT('%', :search, '%'))" +
           " ORDER BY i.id ASC")
    List<Inventario> buscar(@Param("search") String search);
    
    // Ordenado por id (orden de inserción)
    List<Inventario> findAllByOrderByIdAsc();

    // Validar duplicados
    long countByNumeroSerie(String numeroSerie);
    long countByTag(String tag);

    // Layout Vertical: sitios y salas distintos con racks
    @Query("SELECT DISTINCT i.sitio FROM Inventario i WHERE i.sitio IS NOT NULL AND i.sitio <> '' AND i.sitio <> 'N/A' ORDER BY i.sitio ASC")
    List<String> findDistinctSitios();

    @Query("SELECT DISTINCT i.sala FROM Inventario i WHERE (:sitio IS NULL OR :sitio = '' OR i.sitio = :sitio) AND i.sala IS NOT NULL AND i.sala <> '' AND i.sala <> 'N/A' ORDER BY i.sala ASC")
    List<String> findDistinctSalasBySitio(@Param("sitio") String sitio);

    @Query("SELECT DISTINCT i.nombreRack FROM Inventario i WHERE (:sitio IS NULL OR :sitio = '' OR i.sitio = :sitio) AND (:sala IS NULL OR :sala = '' OR i.sala = :sala) AND i.nombreRack IS NOT NULL AND i.nombreRack <> '' AND i.nombreRack <> 'N/A' ORDER BY i.nombreRack ASC")
    List<String> findDistinctRacksBySitioAndSala(@Param("sitio") String sitio, @Param("sala") String sala);

    @Query("SELECT i FROM Inventario i WHERE (:sitio IS NULL OR :sitio = '' OR i.sitio = :sitio) AND (:sala IS NULL OR :sala = '' OR i.sala = :sala) AND (:rack IS NULL OR :rack = '' OR i.nombreRack = :rack) ORDER BY i.nombreRack ASC, i.ubicacionUr ASC")
    List<Inventario> findByFiltroLayout(@Param("sitio") String sitio, @Param("sala") String sala, @Param("rack") String rack);
}
