package com.example.dcim.dao;

import com.example.dcim.entity.GestionAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IGestionAccesoDao extends JpaRepository<GestionAcceso, Long> {

    // Buscar gestiones por sitio
    List<GestionAcceso> findBySitio(String sitio);

    // Buscar gestiones por fecha de registro y sitio
    List<GestionAcceso> findByFechaRegistroAndSitio(LocalDate fechaRegistro, String sitio);

    // Contar gestiones del día por sitio
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE g.fechaRegistro = :fecha AND g.sitio = :sitio")
    Long contarGestionesDelDia(@Param("fecha") LocalDate fecha, @Param("sitio") String sitio);

    // Contar gestiones vigentes por fecha de actividad (entre fecha inicio y fecha término)
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE g.sitio = :sitio AND :fecha BETWEEN g.fechaInicioActividad AND g.fechaTerminoActividad")
    Long contarGestionesVigentes(@Param("fecha") LocalDate fecha, @Param("sitio") String sitio);

    // Contar gestiones que no llegaron en el día (fecha inicio actividad posterior a fecha registro)
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE g.sitio = :sitio AND g.fechaRegistro = :fechaRegistro AND g.fechaInicioActividad > :fechaRegistro")
    Long contarGestionesNoLlegaronEnDia(@Param("fechaRegistro") LocalDate fechaRegistro, @Param("sitio") String sitio);

    // Buscar gestiones por estado de aprobación
    List<GestionAcceso> findByEstadoAprobacionAndSitio(String estadoAprobacion, String sitio);

    // Buscar gestiones por número de ticket
    List<GestionAcceso> findByNumeroTicket(String numeroTicket);

    // Buscar gestiones activas (vigentes) ordenadas por fecha de registro descendente
    @Query("SELECT g FROM GestionAcceso g WHERE g.sitio = :sitio AND :fecha BETWEEN g.fechaInicioActividad AND g.fechaTerminoActividad ORDER BY g.fechaRegistro DESC")
    List<GestionAcceso> findGestionesVigentesBySitio(@Param("fecha") LocalDate fecha, @Param("sitio") String sitio);

    // Contar tickets únicos gestionados en el día
    @Query("SELECT COUNT(DISTINCT g.numeroTicket) FROM GestionAcceso g WHERE g.fechaRegistro = :fecha AND g.sitio = :sitio")
    Long contarTicketsUnicosDelDia(@Param("fecha") LocalDate fecha, @Param("sitio") String sitio);

    // Contar tickets por estado de aprobación y fecha
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE g.estadoAprobacion = :estadoAprobacion AND g.fechaRegistro = :fecha AND g.sitio = :sitio")
    Long contarTicketsPorEstadoYFecha(@Param("estadoAprobacion") String estadoAprobacion, @Param("fecha") LocalDate fecha, @Param("sitio") String sitio);

    // Contar tickets por estado de aprobación
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE g.estadoAprobacion = :estadoAprobacion AND g.sitio = :sitio")
    Long contarTicketsPorEstado(@Param("estadoAprobacion") String estadoAprobacion, @Param("sitio") String sitio);

    // Contar tickets rechazados hoy
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE g.estadoAprobacion LIKE '%Rechazada%' AND g.fechaRegistro = :fecha AND g.sitio = :sitio")
    Long contarTicketsRechazadosHoy(@Param("fecha") LocalDate fecha, @Param("sitio") String sitio);

    // Contar tickets devueltos hoy
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE g.estadoAprobacion LIKE '%Devuelta%' AND g.fechaRegistro = :fecha AND g.sitio = :sitio")
    Long contarTicketsDevueltosHoy(@Param("fecha") LocalDate fecha, @Param("sitio") String sitio);

    // Contar tickets pendientes de cierre
    @Query("SELECT COUNT(g) FROM GestionAcceso g WHERE (g.gestionRealizada = false OR g.ticketCerrado = false) AND g.sitio = :sitio")
    Long contarTicketsPendientesCierre(@Param("sitio") String sitio);
}
