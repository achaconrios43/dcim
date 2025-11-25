package com.example.clases.service;

import com.example.clases.entity.GestionAcceso;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GestionAccesoService {

    // CRUD básico
    List<GestionAcceso> listarTodas();
    Optional<GestionAcceso> buscarPorId(Long id);
    GestionAcceso guardar(GestionAcceso gestionAcceso);
    void eliminar(Long id);

    // Búsquedas específicas
    List<GestionAcceso> listarPorSitio(String sitio);
    List<GestionAcceso> listarPorFechaYSitio(LocalDate fecha, String sitio);
    List<GestionAcceso> listarPorEstadoYSitio(String estadoAprobacion, String sitio);
    List<GestionAcceso> listarPorNumeroTicket(String numeroTicket);

    // Estadísticas para dashboard
    Long contarGestionesDelDia(LocalDate fecha, String sitio);
    Long contarGestionesNoLlegaronEnDia(LocalDate fecha, String sitio);
    Long contarGestionesVigentes(LocalDate fecha, String sitio);
    Long contarTicketsUnicosDelDia(LocalDate fecha, String sitio);
    List<GestionAcceso> listarGestionesVigentes(LocalDate fecha, String sitio);
}
