package com.example.dcim.service;

import com.example.dcim.entity.IngresoAP;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio para lógica de negocio de IngresoAP
 * Contiene las operaciones de negocio para gestión de ingresos al Centro de Datos
 */
public interface IngresoAPService {
    
    // Operaciones CRUD con validación de negocio
    IngresoAP registrarIngreso(IngresoAP ingresoAP) throws Exception;
    IngresoAP registrarSalida(Long id) throws Exception;
    IngresoAP actualizarIngreso(IngresoAP ingresoAP) throws Exception;
    void eliminarIngreso(Long id) throws Exception;
    Optional<IngresoAP> obtenerIngresoPorId(Long id);
    List<IngresoAP> obtenerTodosLosIngresos();
    
    // Gestión de tickets y validaciones
    Optional<IngresoAP> obtenerIngresoPorTicket(String ticket);
    String generarTicketUnico();
    boolean validarTicket(String ticket);
    
    // Búsquedas por persona
    List<IngresoAP> obtenerIngresosPorRut(String rut);
    List<IngresoAP> obtenerIngresosPorPersona(String nombre, String rut);
    
    // Búsquedas por fecha y tiempo
    List<IngresoAP> obtenerIngresosPorFecha(LocalDate fecha);
    List<IngresoAP> obtenerIngresosPorRango(LocalDate fechaInicio, LocalDate fechaFin);
    List<IngresoAP> obtenerIngresosActivosPorFecha(LocalDate fecha);
    
    // Búsquedas por empresa y tipo de trabajo
    List<IngresoAP> obtenerIngresosPorEmpresa(String empresa);
    List<IngresoAP> obtenerIngresosPorTipoRemedy(String tipoRemedy);
    
    // Control de acceso y seguridad
    List<IngresoAP> obtenerPersonasEnEdificio();
    List<IngresoAP> obtenerIngresosActivos();
    List<IngresoAP> obtenerUltimosIngresos();
    boolean estaPersonaEnEdificio(String rut);
    boolean tieneIngresoActivo(String rut);
    Optional<IngresoAP> obtenerIngresoActivoPorRut(String rut);
    
    // Estadísticas y reportes
    Long contarIngresosPorFecha(LocalDate fecha);
    long contarTotalIngresos();
    List<IngresoAP> generarReporteDiario(LocalDate fecha);
    List<IngresoAP> generarReporteSemanal(LocalDate fechaInicio);
    
    // Validaciones de negocio
    boolean puedeRegistrarIngreso(String rut);
    boolean validarHorarioAcceso();
    void validarDatosIngreso(IngresoAP ingresoAP) throws Exception;
    
    // Métodos adicionales para CRUD
    Optional<IngresoAP> buscarPorId(Long id);
    IngresoAP guardar(IngresoAP ingreso);
    void eliminar(Long id);
    List<IngresoAP> buscarPorRutTecnico(String rut);
    List<IngresoAP> listarTodos();
    
    // Métodos para listado mejorado
    List<IngresoAP> obtenerIngresosOrdenadosPorFecha();
    List<IngresoAP> obtenerIngresosPorRangoOrdenados(LocalDate fechaInicio, LocalDate fechaFin);
    List<IngresoAP> obtenerIngresosActivosOrdenados();
    
    // Métodos para estadísticas del dashboard de cliente
    Long contarIngresosMesActual();
    Long contarTicketsUnicos(LocalDate fechaInicio, LocalDate fechaFin);
    Long contarPorTipoTicket(String tipoTicket, LocalDate fechaInicio, LocalDate fechaFin);
    Long contarPorSalaRemedy(String salaRemedy, LocalDate fechaInicio, LocalDate fechaFin);
    List<IngresoAP> obtenerRegistrosActivosRecientes(int limite);
    
    // Métodos para contar tickets únicos por tipo
    Long contarTicketsUnicosPorTipo(String tipoTicket, LocalDate fechaInicio, LocalDate fechaFin);
    
    // Métodos para estadísticas por sitio
    Long contarIngresosPorSitio(String sitio, LocalDate fechaInicio, LocalDate fechaFin);
    Long contarTicketsUnicosPorSitio(String sitio, LocalDate fechaInicio, LocalDate fechaFin);
    Long contarTicketsUnicosPorTipoYSitio(String tipoTicket, String sitio, LocalDate fechaInicio, LocalDate fechaFin);
    Long contarPorSalaRemedyYSitio(String salaRemedy, String sitio, LocalDate fechaInicio, LocalDate fechaFin);
    List<IngresoAP> obtenerRegistrosActivosRecientesPorSitio(String sitio, int limite);
}