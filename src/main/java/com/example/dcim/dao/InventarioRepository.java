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
    List<Inventario> findByTipo(String tipo);
    
    // Búsquedas complejas
    @Query("SELECT i FROM Inventario i WHERE " +
           "LOWER(i.numeroSerie) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.tag) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.sitio) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.cliente) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.estado) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Inventario> buscar(@Param("search") String search);
    
    // Validar duplicados
    long countByNumeroSerie(String numeroSerie);
    long countByTag(String tag);
}
