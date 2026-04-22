package com.example.dcim.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dcim.dao.IGestionAccesoDao;
import com.example.dcim.entity.GestionAcceso;
import com.example.dcim.service.GestionAccesoService;

/**
 * Implementación del servicio de Gestión de Accesos
 * Maneja la lógica de negocio para tickets de acceso y aprobaciones
 */
@Service
public class GestionAccesoServiceImpl implements GestionAccesoService {

    @Autowired
    private IGestionAccesoDao gestionAccesoDao;

    // ==================== OPERACIONES CRUD BÁSICAS ====================
    
    /** Obtiene todas las gestiones de acceso registradas en el sistema */
    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarTodas() {
        return gestionAccesoDao.findAll();
    }

    /** Busca una gestión específica por su ID */
    @Override
    @Transactional(readOnly = true)
    public Optional<GestionAcceso> buscarPorId(Long id) {
        return gestionAccesoDao.findById(id);
    }

    /** Guarda o actualiza una gestión de acceso en la base de datos */
    @Override
    @Transactional
    public GestionAcceso guardar(GestionAcceso gestionAcceso) {
        return gestionAccesoDao.save(gestionAcceso);
    }

    /** Elimina una gestión de acceso por su ID */
    @Override
    @Transactional
    public void eliminar(Long id) {
        gestionAccesoDao.deleteById(id);
    }

    // ==================== CONSULTAS POR FILTROS ====================
    
    /** Lista todas las gestiones de un sitio específico (ej: DC SAN MARTIN) */
    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorSitio(String sitio) {
        return gestionAccesoDao.findBySitio(sitio);
    }

    /** Lista gestiones de una fecha específica en un sitio */
    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorFechaYSitio(LocalDate fecha, String sitio) {
        return gestionAccesoDao.findByFechaRegistroAndSitio(fecha, sitio);
    }

    /** Lista gestiones por estado de aprobación y sitio (Aprobada, Pendiente, Rechazada) */
    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorEstadoYSitio(String estadoAprobacion, String sitio) {
        return gestionAccesoDao.findByEstadoAprobacionAndSitio(estadoAprobacion, sitio);
    }
    
    /** Busca gestiones por número de ticket específico */
    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorNumeroTicket(String numeroTicket) {
        return gestionAccesoDao.findByNumeroTicket(numeroTicket);
    }

    // ==================== CONTADORES Y ESTADÍSTICAS ====================
    
    /** Cuenta el total de gestiones registradas en una fecha específica */
    @Override
    @Transactional(readOnly = true)
    public Long contarGestionesDelDia(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarGestionesDelDia(fecha, sitio);
    }

    /** Cuenta gestiones cuya fecha de inicio NO corresponde al día de registro */
    @Override
    @Transactional(readOnly = true)
    public Long contarGestionesNoLlegaronEnDia(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarGestionesNoLlegaronEnDia(fecha, sitio);
    }

    /** Cuenta gestiones activas (vigentes) para una fecha y sitio */
    @Override
    @Transactional(readOnly = true)
    public Long contarGestionesVigentes(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarGestionesVigentes(fecha, sitio);
    }

    /** Cuenta tickets únicos del día (sin duplicados) */
    @Override
    @Transactional(readOnly = true)
    public Long contarTicketsUnicosDelDia(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarTicketsUnicosDelDia(fecha, sitio);
    }

    /** Lista todas las gestiones vigentes para una fecha en un sitio */
    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarGestionesVigentes(LocalDate fecha, String sitio) {
        return gestionAccesoDao.findGestionesVigentesBySitio(fecha, sitio);
    }

    // ==================== ESTADÍSTICAS POR ESTADO ====================
    
    /** Cuenta tickets con estado específico (Aprobada, Pendiente) en una fecha */
    @Override
    @Transactional(readOnly = true)
    public Long contarTicketsPorEstadoYFecha(String estadoAprobacion, LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarTicketsPorEstadoYFecha(estadoAprobacion, fecha, sitio);
    }

    /** Cuenta tickets con estado específico (sin filtro de fecha) */
    @Override
    @Transactional(readOnly = true)
    public Long contarTicketsPorEstado(String estadoAprobacion, String sitio) {
        return gestionAccesoDao.contarTicketsPorEstado(estadoAprobacion, sitio);
    }

    /** Cuenta tickets rechazados por NXT en una fecha específica */
    @Override
    @Transactional(readOnly = true)
    public Long contarTicketsRechazadosHoy(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarTicketsRechazadosHoy(fecha, sitio);
    }

    /** Cuenta tickets devueltos al cliente por falta de datos en una fecha */
    @Override
    @Transactional(readOnly = true)
    public Long contarTicketsDevueltosHoy(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarTicketsDevueltosHoy(fecha, sitio);
    }

    /** Cuenta tickets pendientes de cierre (gestión no realizada o ticket abierto) */
    @Override
    @Transactional(readOnly = true)
    public Long contarTicketsPendientesCierre(String sitio) {
        return gestionAccesoDao.contarTicketsPendientesCierre(sitio);
    }
}
