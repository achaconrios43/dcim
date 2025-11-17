package com.example.clases.dao;

import com.example.clases.entity.IngresoAP;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para operaciones de acceso a datos de IngresoAP
 * Patrón DAO tradicional para gestión de ingresos al Centro de Datos
 */
public interface IIngresoAPDao {
    
    // Operaciones CRUD básicas
    IngresoAP save(IngresoAP ingresoAP);
    Optional<IngresoAP> findById(Long id);
    List<IngresoAP> findAll();
    void delete(IngresoAP ingresoAP);
    void deleteById(Long id);
    boolean existsById(Long id);
    
    // Búsquedas por identificadores
    List<IngresoAP> findByRutTecnico(String rutTecnico);
    Optional<IngresoAP> findByNumeroTicket(String numeroTicket);
    List<IngresoAP> findByNombreTecnicoAndRutTecnico(String nombreTecnico, String rutTecnico);
    
    // Búsquedas por fecha y tiempo
    List<IngresoAP> findByFechaInicio(LocalDate fechaInicio);
    List<IngresoAP> findByFechaInicioAndFechaTermino(LocalDate fechaInicio, LocalDate fechaTermino);
    List<IngresoAP> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<IngresoAP> findActivosByFechaInicioOrderByHoraInicio(LocalDate fechaInicio);
    
    // Búsquedas por empresa y tipo
    List<IngresoAP> findByEmpresaDemandante(String empresaDemandante);
    List<IngresoAP> findByEmpresaContratista(String empresaContratista);
    List<IngresoAP> findByTipoTicket(String tipoTicket);
    List<IngresoAP> findBySalaRemedy(String salaRemedy);
    
    // Búsquedas por estado
    List<IngresoAP> findByActivoTrue();
    List<IngresoAP> findRegistrosSinSalida();
    List<IngresoAP> findUltimosRegistros();
    
    // Operaciones de conteo
    Long countByFechaInicio(LocalDate fechaInicio);
    long count();
}