package com.example.clases.service.impl;

import com.example.clases.entity.IngresoAP;
import com.example.clases.service.IngresoAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio interno para acceso a datos de IngresoAP
 */
@Repository
interface IngresoAPDataRepository extends JpaRepository<IngresoAP, Long> {
    Optional<IngresoAP> findByNumeroTicket(String numeroTicket);
    List<IngresoAP> findByRutTecnico(String rutTecnico);
    List<IngresoAP> findByNombreTecnicoAndRutTecnico(String nombreTecnico, String rutTecnico);
    List<IngresoAP> findByFechaInicio(LocalDate fechaInicio);
    List<IngresoAP> findByFechaInicioAndFechaTermino(LocalDate fechaInicio, LocalDate fechaTermino);
    List<IngresoAP> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<IngresoAP> findByEmpresaDemandante(String empresaDemandante);
    List<IngresoAP> findByEmpresaContratista(String empresaContratista);
    List<IngresoAP> findByTipoTicket(String tipoTicket);
    List<IngresoAP> findBySalaRemedy(String salaRemedy);
    Long countByFechaInicio(LocalDate fechaInicio);
    
    @Query("SELECT i FROM IngresoAP i WHERE i.fechaInicio = :fechaInicio ORDER BY i.horaInicio")
    List<IngresoAP> findByFechaInicioOrderByHoraInicio(@Param("fechaInicio") LocalDate fechaInicio);
    
    @Query("SELECT i FROM IngresoAP i WHERE i.horaTermino IS NULL OR i.fechaTermino IS NULL")
    List<IngresoAP> findRegistrosSinSalida();
    
    @Query("SELECT i FROM IngresoAP i ORDER BY i.fechaInicio DESC, i.horaInicio DESC")
    List<IngresoAP> findUltimosRegistros();
    
    @Query("SELECT i FROM IngresoAP i WHERE i.fechaInicio BETWEEN :fechaInicio AND :fechaFin ORDER BY i.fechaInicio DESC, i.horaInicio DESC")
    List<IngresoAP> findByFechaInicioBetweenOrderByFechaDesc(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    @Query("SELECT i FROM IngresoAP i WHERE i.activo = true ORDER BY i.fechaInicio DESC, i.horaInicio DESC")
    List<IngresoAP> findActivosOrdenadosPorFecha();
    
    @Query("SELECT i FROM IngresoAP i ORDER BY i.fechaInicio DESC, i.horaInicio DESC")
    List<IngresoAP> findAllOrdenadosPorFecha();
}

/**
 * Implementación del servicio para lógica de negocio de IngresoAP
 * Maneja toda la lógica de negocio y validaciones para control de acceso al Centro de Datos
 */
@Service
public class IngresoAPServiceImpl implements IngresoAPService {
    
    @Autowired
    private IngresoAPDataRepository ingresoAPRepository;
    
    // Horarios de acceso - DESHABILITADO para permitir acceso 24/7
    // private static final LocalTime HORA_INICIO_ACCESO = LocalTime.of(7, 0);
    // private static final LocalTime HORA_FIN_ACCESO = LocalTime.of(22, 0);
    
    @Override
    public IngresoAP registrarIngreso(IngresoAP ingresoAP) throws Exception {
        // Validaciones de negocio
        if (ingresoAP == null) {
            throw new Exception("Los datos del ingreso no pueden ser nulos");
        }
        
        validarDatosIngreso(ingresoAP);
        
        // PERMITIR MÚLTIPLES INGRESOS - Los técnicos pueden ingresar varias veces al día
        // Se ha removido la restricción de duplicados para permitir múltiples registros del mismo técnico
        
        // Acceso 24/7 - Sin restricciones de horario
        // La validación de horario se ha removido para permitir acceso las 24 horas
        
        // Generar ticket único si no se proporcionó
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
            throw new Exception("No se encontró el registro de ingreso");
        }
        
        IngresoAP ingreso = ingresoOpt.get();
        
        if (ingreso.getHoraTermino() != null) {
            throw new Exception("Ya se registró la salida para este ingreso");
        }
        
        // Registrar salida automática
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
     * Verifica si un técnico tiene ingresos activos (sin fecha/hora de término)
     * @param rut RUT del técnico a verificar
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
     * Obtiene el ingreso activo de un técnico (sin fecha/hora de término)
     * @param rut RUT del técnico
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
            throw new Exception("El nombre del técnico es obligatorio");
        }
        
        if (ingresoAP.getRutTecnico() == null || ingresoAP.getRutTecnico().trim().isEmpty()) {
            throw new Exception("El RUT del técnico es obligatorio");
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
        
        // Validar formato de RUT (misma validación que el controlador)
        String rutLimpio = ingresoAP.getRutTecnico().replaceAll("\\s+","");
        if (!rutLimpio.matches("^[\\d]{1,2}\\.?[\\d]{3}\\.?[\\d]{3}-?[\\dkK]$")) {
            throw new Exception("Por favor ingrese un RUT válido (formato: 12.345.678-9)");
        }
    }
    
    // Métodos adicionales para CRUD
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
     * Obtiene todos los ingresos ordenados por fecha (más recientes primero)
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
}
