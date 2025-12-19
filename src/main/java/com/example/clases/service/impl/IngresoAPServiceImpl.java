package com.example.clases.service.impl;

import com.example.clases.dao.IIngresoAPDao;
import com.example.clases.entity.IngresoAP;
import com.example.clases.service.IngresoAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementaci????n del servicio para l????gica de negocio de IngresoAP
 * Maneja toda la l????gica de negocio y validaciones para control de acceso al Centro de Datos
 */
@Service
public class IngresoAPServiceImpl implements IngresoAPService {

    @Autowired
    private IIngresoAPDao ingresoAPRepository;    // Horarios de acceso - DESHABILITADO para permitir acceso 24/7
    // private static final LocalTime HORA_INICIO_ACCESO = LocalTime.of(7, 0);
    // private static final LocalTime HORA_FIN_ACCESO = LocalTime.of(22, 0);
    
    @Override
    public IngresoAP registrarIngreso(IngresoAP ingresoAP) throws Exception {
        // Validaciones de negocio
        if (ingresoAP == null) {
            throw new Exception("Los datos del ingreso no pueden ser nulos");
        }
        
        validarDatosIngreso(ingresoAP);
        
        // PERMITIR M????LTIPLES INGRESOS - Los t????cnicos pueden ingresar varias veces al d????a
        // Se ha removido la restricci????n de duplicados para permitir m????ltiples registros del mismo t????cnico
        
        // Acceso 24/7 - Sin restricciones de horario
        // La validaci????n de horario se ha removido para permitir acceso las 24 horas
        
        // Generar ticket ????nico si no se proporcion????
        if (ingresoAP.getNumeroTicket() == null || ingresoAP.getNumeroTicket().trim().isEmpty()) {
            ingresoAP.setNumeroTicket(generarTicketUnico());
        }
        
        // Establecer valores por defecto
        if (ingresoAP.getFechaInicio() == null) {
            ingresoAP.setFechaInicio(LocalDate.now());
        }
        
        if (ingresoAP.getHoraInicio() == null) {
            ingresoAP.setHoraInicio(LocalTime.now());
        }
        
        if (ingresoAP.getFechaRegistro() == null) {
            ingresoAP.setFechaRegistro(new Date());
        }
        
        ingresoAP.setActivo(true);
        
        return ingresoAPRepository.save(ingresoAP);
    }
    
    @Override
    public IngresoAP registrarSalida(Long id) throws Exception {
        if (id == null) {
            throw new Exception("El ID del ingreso no puede ser nulo");
        }
        
        Optional<IngresoAP> ingresoOpt = ingresoAPRepository.findById(id);
        if (!ingresoOpt.isPresent()) {
            throw new Exception("No se encontr???? el registro de ingreso");
        }
        
        IngresoAP ingreso = ingresoOpt.get();
        
        if (ingreso.getHoraTermino() != null) {
            throw new Exception("Ya se registr???? la salida para este ingreso");
        }
        
        // Registrar salida autom????tica
        ingreso.setHoraTermino(LocalTime.now());
        ingreso.setFechaTermino(LocalDate.now());
        ingreso.setActivo(false); // Auto-inactivar al registrar salida
        
        return ingresoAPRepository.save(ingreso);
    }
    
    @Override
    public IngresoAP actualizarIngreso(IngresoAP ingresoAP) throws Exception {
        if (ingresoAP == null || ingresoAP.getId() == null) {
            throw new Exception("El ingreso y su ID no pueden ser nulos");
        }
        
        if (!ingresoAPRepository.existsById(ingresoAP.getId())) {
            throw new Exception("El registro de ingreso no existe");
        }
        
        validarDatosIngreso(ingresoAP);
        return ingresoAPRepository.save(ingresoAP);
    }
    
    @Override
    public void eliminarIngreso(Long id) throws Exception {
        if (id == null) {
            throw new Exception("El ID no puede ser nulo");
        }
        
        if (!ingresoAPRepository.existsById(id)) {
            throw new Exception("El registro de ingreso no existe");
        }
        
        ingresoAPRepository.deleteById(id);
    }
    
    @Override
    public Optional<IngresoAP> obtenerIngresoPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return ingresoAPRepository.findById(id);
    }
    
    @Override
    public List<IngresoAP> obtenerTodosLosIngresos() {
        return ingresoAPRepository.findAll();
    }
    
    @Override
    public Optional<IngresoAP> obtenerIngresoPorTicket(String ticket) {
        if (ticket == null || ticket.trim().isEmpty()) {
            return Optional.empty();
        }
        return ingresoAPRepository.findByNumeroTicket(ticket.trim());
    }
    
    @Override
    public String generarTicketUnico() {
        String ticket;
        do {
            ticket = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ingresoAPRepository.findByNumeroTicket(ticket).isPresent());
        
        return ticket;
    }
    
    @Override
    public boolean validarTicket(String ticket) {
        if (ticket == null || ticket.trim().isEmpty()) {
            return false;
        }
        return ticket.matches("TKT-[A-Z0-9]{8}");
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosPorRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return List.of();
        }
        return ingresoAPRepository.findByRutTecnico(rut.trim());
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosPorPersona(String nombre, String rut) {
        if (nombre == null || rut == null) {
            return List.of();
        }
        return ingresoAPRepository.findByNombreTecnicoAndRutTecnico(nombre.trim(), rut.trim());
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosPorFecha(LocalDate fecha) {
        if (fecha == null) {
            return List.of();
        }
        return ingresoAPRepository.findByFechaInicio(fecha);
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return List.of();
        }
        return ingresoAPRepository.findByFechaInicioBetween(fechaInicio, fechaFin);
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosActivosPorFecha(LocalDate fecha) {
        if (fecha == null) {
            return List.of();
        }
        return ingresoAPRepository.findByFechaInicioOrderByHoraInicio(fecha);
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosPorEmpresa(String empresa) {
        if (empresa == null || empresa.trim().isEmpty()) {
            return List.of();
        }
        return ingresoAPRepository.findByEmpresaDemandante(empresa.trim());
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosPorTipoRemedy(String tipoRemedy) {
        if (tipoRemedy == null || tipoRemedy.trim().isEmpty()) {
            return List.of();
        }
        return ingresoAPRepository.findByTipoTicket(tipoRemedy.trim());
    }
    
    @Override
    public List<IngresoAP> obtenerPersonasEnEdificio() {
        return ingresoAPRepository.findRegistrosSinSalida();
    }
    
    @Override
    public List<IngresoAP> obtenerIngresosActivos() {
        return ingresoAPRepository.findRegistrosSinSalida();
    }
    
    @Override
    public List<IngresoAP> obtenerUltimosIngresos() {
        return ingresoAPRepository.findUltimosRegistros();
    }
    
    @Override
    public boolean estaPersonaEnEdificio(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }
        
        return obtenerPersonasEnEdificio().stream()
                .anyMatch(ingreso -> rut.trim().equals(ingreso.getRutTecnico()));
    }
    
    /**
     * Verifica si un t????cnico tiene ingresos activos (sin fecha/hora de t????rmino)
     * @param rut RUT del t????cnico a verificar
     * @return true si tiene ingresos activos, false si no
     */
    @Override
    public boolean tieneIngresoActivo(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }
        
        return ingresoAPRepository.findRegistrosSinSalida().stream()
                .anyMatch(ingreso -> rut.trim().equals(ingreso.getRutTecnico()));
    }
    
    /**
     * Obtiene el ingreso activo de un t????cnico (sin fecha/hora de t????rmino)
     * @param rut RUT del t????cnico
     * @return Optional con el ingreso activo si existe
     */
    @Override
    public Optional<IngresoAP> obtenerIngresoActivoPorRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return ingresoAPRepository.findRegistrosSinSalida().stream()
                .filter(ingreso -> rut.trim().equals(ingreso.getRutTecnico()))
                .findFirst();
    }
    
    @Override
    public Long contarIngresosPorFecha(LocalDate fecha) {
        if (fecha == null) {
            return 0L;
        }
        return ingresoAPRepository.countByFechaInicio(fecha);
    }
    
    @Override
    public long contarTotalIngresos() {
        return ingresoAPRepository.count();
    }
    
    @Override
    public List<IngresoAP> generarReporteDiario(LocalDate fecha) {
        return obtenerIngresosActivosPorFecha(fecha);
    }
    
    @Override
    public List<IngresoAP> generarReporteSemanal(LocalDate fechaInicio) {
        LocalDate fechaFin = fechaInicio.plusDays(6);
        return obtenerIngresosPorRango(fechaInicio, fechaFin);
    }
    
    @Override
    public boolean puedeRegistrarIngreso(String rut) {
        return !estaPersonaEnEdificio(rut);
    }
    
    @Override
    public boolean validarHorarioAcceso() {
        // Acceso permitido 24/7 - Sin restricciones de horario
        return true;
    }
    
    @Override
    public void validarDatosIngreso(IngresoAP ingresoAP) throws Exception {
        if (ingresoAP.getNombreTecnico() == null || ingresoAP.getNombreTecnico().trim().isEmpty()) {
            throw new Exception("El nombre del t????cnico es obligatorio");
        }
        
        if (ingresoAP.getRutTecnico() == null || ingresoAP.getRutTecnico().trim().isEmpty()) {
            throw new Exception("El RUT del t????cnico es obligatorio");
        }
        
        if (ingresoAP.getEmpresaDemandante() == null || ingresoAP.getEmpresaDemandante().trim().isEmpty()) {
            throw new Exception("La empresa demandante es obligatoria");
        }
        
        if (ingresoAP.getTipoTicket() == null || ingresoAP.getTipoTicket().trim().isEmpty()) {
            throw new Exception("El tipo de ticket es obligatorio");
        }
        
        if (ingresoAP.getSalaIngresa() == null || ingresoAP.getSalaIngresa().trim().isEmpty()) {
            throw new Exception("La sala de ingreso es obligatoria");
        }
        
        if (ingresoAP.getActividadRemedy() == null || ingresoAP.getActividadRemedy().trim().isEmpty()) {
            throw new Exception("La actividad REMEDY es obligatoria");
        }
        
        // Validar formato de RUT (misma validaci????n que el controlador)
        String rutLimpio = ingresoAP.getRutTecnico().replaceAll("\\s+","");
        if (!rutLimpio.matches("^[\\d]{1,2}\\.?[\\d]{3}\\.?[\\d]{3}-?[\\dkK]$")) {
            throw new Exception("Por favor ingrese un RUT v????lido (formato: 12.345.678-9)");
        }
    }
    
    // M????todos adicionales para CRUD
    @Override
    public Optional<IngresoAP> buscarPorId(Long id) {
        return obtenerIngresoPorId(id);
    }
    
    @Override
    public IngresoAP guardar(IngresoAP ingreso) {
        return ingresoAPRepository.save(ingreso);
    }
    
    @Override
    public void eliminar(Long id) {
        ingresoAPRepository.deleteById(id);
    }
    
    @Override
    public List<IngresoAP> buscarPorRutTecnico(String rut) {
        return obtenerIngresosPorRut(rut);
    }
    
    @Override
    public List<IngresoAP> listarTodos() {
        return obtenerTodosLosIngresos();
    }
    
    /**
     * Obtiene todos los ingresos ordenados por fecha (m????s recientes primero)
     */
    public List<IngresoAP> obtenerIngresosOrdenadosPorFecha() {
        return ingresoAPRepository.findAllOrdenadosPorFecha();
    }
    
    /**
     * Obtiene ingresos por rango de fechas ordenados
     */
    public List<IngresoAP> obtenerIngresosPorRangoOrdenados(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return obtenerIngresosOrdenadosPorFecha();
        }
        return ingresoAPRepository.findByFechaInicioBetweenOrderByFechaDesc(fechaInicio, fechaFin);
    }
    
    /**
     * Obtiene solo ingresos activos ordenados por fecha
     */
    public List<IngresoAP> obtenerIngresosActivosOrdenados() {
        return ingresoAPRepository.findActivosOrdenadosPorFecha();
    }
    
    /**
     * Cuenta total de ingresos del mes actual
     */
    @Override
    public Long contarIngresosMesActual() {
        LocalDate now = LocalDate.now();
        LocalDate primerDiaMes = now.withDayOfMonth(1);
        LocalDate ultimoDiaMes = now.withDayOfMonth(now.lengthOfMonth());
        return ingresoAPRepository.countByFechaInicioBetween(primerDiaMes, ultimoDiaMes);
    }
    
    /**
     * Cuenta tickets ????nicos (no repetidos) en un rango de fechas
     */
    @Override
    public Long contarTicketsUnicos(LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countDistinctNumeroTicketByFechaInicioBetween(fechaInicio, fechaFin);
    }
    
    /**
     * Cuenta ingresos por tipo de ticket en un rango de fechas
     */
    @Override
    public Long contarPorTipoTicket(String tipoTicket, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countByTipoTicketAndFechaInicioBetween(tipoTicket, fechaInicio, fechaFin);
    }
    
    /**
     * Cuenta ingresos por sala REMEDY en un rango de fechas
     */
    @Override
    public Long contarPorSalaRemedy(String salaRemedy, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countBySalaRemedyAndFechaInicioBetween(salaRemedy, fechaInicio, fechaFin);
    }
    
    /**
     * Obtiene los registros activos m????s recientes limitados
     */
    @Override
    public List<IngresoAP> obtenerRegistrosActivosRecientes(int limite) {
        List<IngresoAP> todos = ingresoAPRepository.findTop10ByActivoTrueOrderByFechaInicioDescHoraInicioDesc();
        if (todos.size() > limite) {
            return todos.subList(0, limite);
        }
        return todos;
    }
    
    @Override
    public Long contarTicketsUnicosPorTipo(String tipoTicket, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countDistinctNumeroTicketByTipoTicketAndFechaInicioBetween(tipoTicket, fechaInicio, fechaFin);
    }
    
    // Implementaci????n de m????todos para filtrar por sitio
    @Override
    public Long contarIngresosPorSitio(String sitio, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countBySitioIngresoAndFechaInicioBetween(sitio, fechaInicio, fechaFin);
    }
    
    @Override
    public Long contarTicketsUnicosPorSitio(String sitio, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countDistinctNumeroTicketBySitioIngresoAndFechaInicioBetween(sitio, fechaInicio, fechaFin);
    }
    
    @Override
    public Long contarTicketsUnicosPorTipoYSitio(String tipoTicket, String sitio, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countDistinctNumeroTicketByTipoTicketAndSitioIngresoAndFechaInicioBetween(tipoTicket, sitio, fechaInicio, fechaFin);
    }
    
    @Override
    public Long contarPorSalaRemedyYSitio(String salaRemedy, String sitio, LocalDate fechaInicio, LocalDate fechaFin) {
        return ingresoAPRepository.countBySalaRemedyAndSitioIngresoAndFechaInicioBetween(salaRemedy, sitio, fechaInicio, fechaFin);
    }
    
    @Override
    public List<IngresoAP> obtenerRegistrosActivosRecientesPorSitio(String sitio, int limite) {
        List<IngresoAP> todos = ingresoAPRepository.findByActivoTrueAndSitioIngresoOrderByFechaInicioDescHoraInicioDesc(sitio);
        if (todos.size() > limite) {
            return todos.subList(0, limite);
        }
        return todos;
    }
}

