package com.example.dcim.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dcim.entity.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    List<Sala> findBySitioIdOrderByNombreAsc(Long sitioId);

    List<Sala> findBySitioNombreIgnoreCaseOrderByNombreAsc(String sitio);

    Optional<Sala> findBySitioIdAndNombreIgnoreCase(Long sitioId, String nombre);

    List<Sala> findByActivoTrueOrderByNombreAsc();

    List<Sala> findByActivoTrueAndSitioIdOrderByNombreAsc(Long sitioId);

    @Query("SELECT COUNT(p) FROM PlanoSala p WHERE p.sala.id = :salaId")
    long countPlanosBySalaId(@Param("salaId") Long salaId);

    @Query("SELECT COUNT(pm) FROM PuntoMedicion pm WHERE pm.sala.id = :salaId")
    long countPuntosMedicionBySalaId(@Param("salaId") Long salaId);
}
