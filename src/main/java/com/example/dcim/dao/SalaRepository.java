package com.example.dcim.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dcim.entity.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    List<Sala> findBySitioIdOrderByNombreAsc(Long sitioId);

    List<Sala> findBySitioNombreIgnoreCaseOrderByNombreAsc(String sitio);

    Optional<Sala> findBySitioIdAndNombreIgnoreCase(Long sitioId, String nombre);
}
