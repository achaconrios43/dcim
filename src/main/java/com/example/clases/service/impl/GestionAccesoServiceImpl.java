package com.example.clases.service.impl;

import com.example.clases.dao.IGestionAccesoDao;
import com.example.clases.entity.GestionAcceso;
import com.example.clases.service.GestionAccesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GestionAccesoServiceImpl implements GestionAccesoService {

    @Autowired
    private IGestionAccesoDao gestionAccesoDao;

    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarTodas() {
        return gestionAccesoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GestionAcceso> buscarPorId(Long id) {
        return gestionAccesoDao.findById(id);
    }

    @Override
    @Transactional
    public GestionAcceso guardar(GestionAcceso gestionAcceso) {
        return gestionAccesoDao.save(gestionAcceso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        gestionAccesoDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorSitio(String sitio) {
        return gestionAccesoDao.findBySitio(sitio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorFechaYSitio(LocalDate fecha, String sitio) {
        return gestionAccesoDao.findByFechaRegistroAndSitio(fecha, sitio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorEstadoYSitio(String estadoAprobacion, String sitio) {
        return gestionAccesoDao.findByEstadoAprobacionAndSitio(estadoAprobacion, sitio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarPorNumeroTicket(String numeroTicket) {
        return gestionAccesoDao.findByNumeroTicket(numeroTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarGestionesDelDia(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarGestionesDelDia(fecha, sitio);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarGestionesNoLlegaronEnDia(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarGestionesNoLlegaronEnDia(fecha, sitio);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarGestionesVigentes(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarGestionesVigentes(fecha, sitio);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarTicketsUnicosDelDia(LocalDate fecha, String sitio) {
        return gestionAccesoDao.contarTicketsUnicosDelDia(fecha, sitio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GestionAcceso> listarGestionesVigentes(LocalDate fecha, String sitio) {
        return gestionAccesoDao.findGestionesVigentesBySitio(fecha, sitio);
    }
}
