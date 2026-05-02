package com.example.dcim.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dcim.entity.Sitio;

public interface SitioRepository extends JpaRepository<Sitio, Long> {

    Optional<Sitio> findByNombreIgnoreCase(String nombre);

    List<Sitio> findByActivoTrueOrderByNombreAsc();
}
