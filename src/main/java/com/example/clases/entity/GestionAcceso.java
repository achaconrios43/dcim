package com.example.clases.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "gestion_acceso")
public class GestionAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "hora_registro")
    private LocalTime horaRegistro;

    @Column(name = "usuario_ingresa")
    private String usuarioIngresa;

    @Column(name = "numero_ticket")
    private String numeroTicket;

    @Column(name = "fecha_inicio_actividad")
    private LocalDate fechaInicioActividad;

    @Column(name = "hora_inicio_actividad")
    private LocalTime horaInicioActividad;

    @Column(name = "nombre_actividad", columnDefinition = "TEXT")
    private String nombreActividad;

    @Column(name = "fecha_termino_actividad")
    private LocalDate fechaTerminoActividad;

    @Column(name = "hora_termino_actividad")
    private LocalTime horaTerminoActividad;

    @Column(name = "aprobadores_pendientes", columnDefinition = "TEXT")
    private String aprobadoresPendientes;

    @Column(name = "gestion_realizada")
    private Boolean gestionRealizada;

    @Column(name = "estado_aprobacion")
    private String estadoAprobacion; // "Aprobada", "Rechazada por NXT", "Devuelta al cliente por falta de Datos", "Pendiente"

    @Column(name = "sitio")
    private String sitio; // DC San Martin, DC Apoquindo, MC La Florida, etc.

    // Constructores
    public GestionAcceso() {
    }

    public GestionAcceso(LocalDate fechaRegistro, LocalTime horaRegistro, String usuarioIngresa, 
                        String numeroTicket, LocalDate fechaInicioActividad, LocalTime horaInicioActividad,
                        String nombreActividad, LocalDate fechaTerminoActividad, LocalTime horaTerminoActividad,
                        String aprobadoresPendientes, Boolean gestionRealizada, String estadoAprobacion, String sitio) {
        this.fechaRegistro = fechaRegistro;
        this.horaRegistro = horaRegistro;
        this.usuarioIngresa = usuarioIngresa;
        this.numeroTicket = numeroTicket;
        this.fechaInicioActividad = fechaInicioActividad;
        this.horaInicioActividad = horaInicioActividad;
        this.nombreActividad = nombreActividad;
        this.fechaTerminoActividad = fechaTerminoActividad;
        this.horaTerminoActividad = horaTerminoActividad;
        this.aprobadoresPendientes = aprobadoresPendientes;
        this.gestionRealizada = gestionRealizada;
        this.estadoAprobacion = estadoAprobacion;
        this.sitio = sitio;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalTime getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(LocalTime horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public String getUsuarioIngresa() {
        return usuarioIngresa;
    }

    public void setUsuarioIngresa(String usuarioIngresa) {
        this.usuarioIngresa = usuarioIngresa;
    }

    public String getNumeroTicket() {
        return numeroTicket;
    }

    public void setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }

    public LocalDate getFechaInicioActividad() {
        return fechaInicioActividad;
    }

    public void setFechaInicioActividad(LocalDate fechaInicioActividad) {
        this.fechaInicioActividad = fechaInicioActividad;
    }

    public LocalTime getHoraInicioActividad() {
        return horaInicioActividad;
    }

    public void setHoraInicioActividad(LocalTime horaInicioActividad) {
        this.horaInicioActividad = horaInicioActividad;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public LocalDate getFechaTerminoActividad() {
        return fechaTerminoActividad;
    }

    public void setFechaTerminoActividad(LocalDate fechaTerminoActividad) {
        this.fechaTerminoActividad = fechaTerminoActividad;
    }

    public LocalTime getHoraTerminoActividad() {
        return horaTerminoActividad;
    }

    public void setHoraTerminoActividad(LocalTime horaTerminoActividad) {
        this.horaTerminoActividad = horaTerminoActividad;
    }

    public String getAprobadoresPendientes() {
        return aprobadoresPendientes;
    }

    public void setAprobadoresPendientes(String aprobadoresPendientes) {
        this.aprobadoresPendientes = aprobadoresPendientes;
    }

    public Boolean getGestionRealizada() {
        return gestionRealizada;
    }

    public void setGestionRealizada(Boolean gestionRealizada) {
        this.gestionRealizada = gestionRealizada;
    }

    public String getEstadoAprobacion() {
        return estadoAprobacion;
    }

    public void setEstadoAprobacion(String estadoAprobacion) {
        this.estadoAprobacion = estadoAprobacion;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    @Override
    public String toString() {
        return "GestionAcceso{" +
                "id=" + id +
                ", fechaRegistro=" + fechaRegistro +
                ", horaRegistro=" + horaRegistro +
                ", usuarioIngresa='" + usuarioIngresa + '\'' +
                ", numeroTicket='" + numeroTicket + '\'' +
                ", fechaInicioActividad=" + fechaInicioActividad +
                ", horaInicioActividad=" + horaInicioActividad +
                ", nombreActividad='" + nombreActividad + '\'' +
                ", fechaTerminoActividad=" + fechaTerminoActividad +
                ", horaTerminoActividad=" + horaTerminoActividad +
                ", aprobadoresPendientes='" + aprobadoresPendientes + '\'' +
                ", gestionRealizada=" + gestionRealizada +
                ", estadoAprobacion='" + estadoAprobacion + '\'' +
                ", sitio='" + sitio + '\'' +
                '}';
    }
}
