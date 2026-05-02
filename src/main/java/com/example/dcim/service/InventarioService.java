package com.example.dcim.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dcim.dao.InventarioRepository;
import com.example.dcim.entity.Inventario;

@Service
@Transactional
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private TemperaturaService temperaturaService;

    // CRUD básico
    public Inventario crear(Inventario inventario) throws Exception {
        // Validar duplicados por número de serie
        if (inventario.getNumeroSerie() != null && 
            inventarioRepository.countByNumeroSerie(inventario.getNumeroSerie()) > 0) {
            throw new Exception("Ya existe un inventario con el número de serie: " + inventario.getNumeroSerie());
        }

        // Validar duplicados por tag
        if (inventario.getTag() != null && 
            inventarioRepository.countByTag(inventario.getTag()) > 0) {
            throw new Exception("Ya existe un inventario con el tag: " + inventario.getTag());
        }

        temperaturaService.vincularInventarioConUbicacion(inventario);

        return inventarioRepository.save(inventario);
    }

    public Inventario actualizar(Inventario inventario) throws Exception {
        if (!inventarioRepository.existsById(inventario.getId())) {
            throw new Exception("Inventario no encontrado");
        }
        temperaturaService.vincularInventarioConUbicacion(inventario);
        return inventarioRepository.save(inventario);
    }

    public void eliminar(Long id) throws Exception {
        if (!inventarioRepository.existsById(id)) {
            throw new Exception("Inventario no encontrado");
        }
        inventarioRepository.deleteById(id);
    }

    public Optional<Inventario> obtenerPorId(Long id) {
        return inventarioRepository.findById(id);
    }

    public List<Inventario> obtenerTodos() {
        return inventarioRepository.findAll();
    }

    // Búsquedas específicas
    public Optional<Inventario> obtenerPorNumeroSerie(String numeroSerie) {
        return inventarioRepository.findByNumeroSerie(numeroSerie);
    }

    public Optional<Inventario> obtenerPorTag(String tag) {
        return inventarioRepository.findByTag(tag);
    }

    public List<Inventario> obtenerPorCliente(String cliente) {
        return inventarioRepository.findByCliente(cliente);
    }

    public List<Inventario> obtenerPorEstado(String estado) {
        return inventarioRepository.findByEstado(estado);
    }

    public List<Inventario> obtenerPorSala(String sala) {
        return inventarioRepository.findBySala(sala);
    }

    // Búsqueda general
    public List<Inventario> buscar(String search) {
        return inventarioRepository.buscar(search);
    }

    // Importar múltiples sin duplicados
    public void importarLote(List<Inventario> items) throws Exception {
        for (Inventario item : items) {
            try {
                // Si existe, actualizar; si no, crear
                Optional<Inventario> existente = inventarioRepository.findByNumeroSerie(item.getNumeroSerie());
                if (existente.isPresent()) {
                    // Actualizar solo campos específicos
                    Inventario inv = existente.get();
                    inv.setEstado(item.getEstado());
                    inv.setCliente(item.getCliente());
                    inv.setHotname(item.getHotname());
                    inv.setDireccionIp(item.getDireccionIp());
                    inv.setSitio(item.getSitio());
                    inv.setObservaciones(item.getObservaciones());
                    inventarioRepository.save(inv);
                } else {
                    inventarioRepository.save(item);
                }
            } catch (Exception e) {
                // Continuar con el siguiente, registrar el error
                System.err.println("Error importando item: " + item.getNumeroSerie() + " - " + e.getMessage());
            }
        }
    }

    // Validar si existe por número de serie
    public boolean existePorNumeroSerie(String numeroSerie) {
        return inventarioRepository.countByNumeroSerie(numeroSerie) > 0;
    }

    // Validar si existe por tag
    public boolean existePorTag(String tag) {
        return inventarioRepository.countByTag(tag) > 0;
    }

    // Contar total
    public long contar() {
        return inventarioRepository.count();
    }
}
