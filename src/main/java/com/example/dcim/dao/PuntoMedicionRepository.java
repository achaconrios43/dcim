package com.example.dcim.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dcim.entity.PuntoMedicion;

public interface PuntoMedicionRepository extends JpaRepository<PuntoMedicion, Long> {

    List<PuntoMedicion> findBySalaIdOrderByNombreAsc(Long salaId);

    List<PuntoMedicion> findBySalaIdAndActivoTrueOrderByNombreAsc(Long salaId);

    long countBySalaId(Long salaId);

    List<PuntoMedicion> findBySalaSitioNombreIgnoreCaseOrderByNombreAsc(String sitio);

    long countBySalaSitioNombreIgnoreCaseAndActivoTrue(String sitio);
}
