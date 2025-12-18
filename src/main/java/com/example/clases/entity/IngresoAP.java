package com.example.clases.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "ingresoap")
public class IngresoAP {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String turno; // AM - PM
    
    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String nombreUsuario;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "fecha_termino")
    private LocalDate fechaTermino;
    
    @Column(name = "hora_termino")
    private LocalTime horaTermino;
    
    // Campos para supervisiÃ³n programada
    @Column(name = "fecha_fin_ficticia")
    private LocalDate fechaFinFicticia;
    
    @Column(name = "hora_fin_ficticia")
    private LocalTime horaFinFicticia;
    
    @Column(name = "hora_supervision_media")
    private LocalTime horaSupervisionMedia;
    
    @Column(name = "fecha_supervision_media")
    private LocalDate fechaSupervisionMedia;
    
    @Column(name = "segunda_supervision_realizada")
    private Boolean segundaSupervisionRealizada = false;
    
    @Column(name = "fecha_segunda_supervision")
    private LocalDate fechaSegundaSupervision;
    
    @Column(name = "hora_segunda_supervision")
    private LocalTime horaSegundaSupervision;
    
    @Column(name = "nombre_tecnico", nullable = false, length = 100)
    private String nombreTecnico;
    
    @Column(name = "rut_tecnico", nullable = false, length = 12)
    private String rutTecnico;
    
    @Column(name = "empresa_demandante", nullable = false, length = 100)
    private String empresaDemandante;
    
    @Column(name = "empresa_contratista", nullable = false, length = 100)
    private String empresaContratista;
    
    @Column(name = "cargo_tecnico", nullable = false, length = 100)
    private String cargoTecnico;
    
    @Column(name = "sala_remedy", nullable = false, length = 50)
    private String salaRemedy; // Salas de RED o Salas TI
    
    @Column(name = "tipo_ticket", nullable = false, length = 30)
    private String tipoTicket; // CRQ - INC - Visita Inspectiva - Ronda Rutinaria
    
    @Column(name = "numero_ticket", nullable = false, length = 50)
    private String numeroTicket;
    
    @Column(name = "aprobador", nullable = false, length = 100)
    private String aprobador; // Paulo Hernandez - Arturo ChacÃ³n - Marcelo Robles - Facilities - Rodrigo Aguilera - Operador Turno - No Autorizado
    
    @Column(name = "escolta", nullable = false, length = 50)
    private String escolta; // Operador de Turno - Guardia - Personal de Facilities
    
    @Column(name = "motivo_ingreso", nullable = false, length = 100)
    private String motivoIngreso; // Inspectiva - Actividad Rutinaria - InstalaciÃ³n - Mediciones - Sin ticket - ticket de Empresa - ticket de Otro Sitio
    
    @Column(name = "guia_despacho", length = 50)
    private String guiaDespacho;
    
    @Column(name = "sitio_ingreso", length = 100)
    private String sitioIngreso; // DC San Martin, DC Apoquindo, MC La Florida, etc.
    
    @Column(name = "sala_ingresa", nullable = false, columnDefinition = "TEXT")
    private String salaIngresa; // Ahora acepta mÃºltiples salas separadas por comas
    
    @Column(name = "rack_ingresa", length = 50)
    private String rackIngresa;
    
    @Column(name = "actividad_remedy", nullable = false, columnDefinition = "TEXT")
    private String actividadRemedy;
    
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Constructores
    public IngresoAP() {
        this.fechaRegistro = new Date();
        this.activo = true;
    }
    
    public IngresoAP(String turno, String nombreUsuario, LocalDate fechaInicio, LocalTime horaInicio, 
                     String nombreTecnico, String rutTecnico, String empresaDemandante, 
                     String empresaContratista, String cargoTecnico, String salaRemedy, 
                     String tipoTicket, String numeroTicket, String aprobador, String escolta,
                     String motivoIngreso, String guiaDespacho, String salaIngresa, 
                     String rackIngresa, String actividadRemedy) {
        this();
        this.turno = turno;
        this.nombreUsuario = nombreUsuario;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.nombreTecnico = nombreTecnico;
        this.rutTecnico = rutTecnico;
        this.empresaDemandante = empresaDemandante;
        this.empresaContratista = empresaContratista;
        this.cargoTecnico = cargoTecnico;
        this.salaRemedy = salaRemedy;
        this.tipoTicket = tipoTicket;
        this.numeroTicket = numeroTicket;
        this.aprobador = aprobador;
        this.escolta = escolta;
        this.motivoIngreso = motivoIngreso;
        this.guiaDespacho = guiaDespacho;
        this.salaIngresa = salaIngresa;
        this.rackIngresa = rackIngresa;
        this.actividadRemedy = actividadRemedy;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTurno() {
        return turno;
    }
    
    public void setTurno(String turno) {
        this.turno = turno;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    
    public LocalDate getFechaTermino() {
        return fechaTermino;
    }
    
    public void setFechaTermino(LocalDate fechaTermino) {
        this.fechaTermino = fechaTermino;
    }
    
    public LocalTime getHoraTermino() {
        return horaTermino;
    }
    
    public void setHoraTermino(LocalTime horaTermino) {
        this.horaTermino = horaTermino;
    }
    
    public String getNombreTecnico() {
        return nombreTecnico;
    }
    
    public void setNombreTecnico(String nombreTecnico) {
        this.nombreTecnico = nombreTecnico;
    }
    
    public String getRutTecnico() {
        return rutTecnico;
    }
    
    public void setRutTecnico(String rutTecnico) {
        this.rutTecnico = rutTecnico;
    }
    
    public String getEmpresaDemandante() {
        return empresaDemandante;
    }
    
    public void setEmpresaDemandante(String empresaDemandante) {
        this.empresaDemandante = empresaDemandante;
    }
    
    public String getEmpresaContratista() {
        return empresaContratista;
    }
    
    public void setEmpresaContratista(String empresaContratista) {
        this.empresaContratista = empresaContratista;
    }
    
    public String getCargoTecnico() {
        return cargoTecnico;
    }
    
    public void setCargoTecnico(String cargoTecnico) {
        this.cargoTecnico = cargoTecnico;
    }
    
    public String getSalaRemedy() {
        return salaRemedy;
    }
    
    public void setSalaRemedy(String salaRemedy) {
        this.salaRemedy = salaRemedy;
    }
    
    public String getTipoTicket() {
        return tipoTicket;
    }
    
    public void setTipoTicket(String tipoTicket) {
        this.tipoTicket = tipoTicket;
    }
    
    public String getNumeroTicket() {
        return numeroTicket;
    }
    
    public void setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }
    
    public String getAprobador() {
        return aprobador;
    }
    
    public void setAprobador(String aprobador) {
        this.aprobador = aprobador;
    }
    
    public String getEscolta() {
        return escolta;
    }
    
    public void setEscolta(String escolta) {
        this.escolta = escolta;
    }
    
    public String getMotivoIngreso() {
        return motivoIngreso;
    }
    
    public void setMotivoIngreso(String motivoIngreso) {
        this.motivoIngreso = motivoIngreso;
    }
    
    public String getGuiaDespacho() {
        return guiaDespacho;
    }
    
    public void setGuiaDespacho(String guiaDespacho) {
        this.guiaDespacho = guiaDespacho;
    }
    
    public String getSitioIngreso() {
        return sitioIngreso;
    }
    
    public void setSitioIngreso(String sitioIngreso) {
        this.sitioIngreso = sitioIngreso;
    }
    
    public String getSalaIngresa() {
        return salaIngresa;
    }
    
    public void setSalaIngresa(String salaIngresa) {
        this.salaIngresa = salaIngresa;
    }
    
    public String getRackIngresa() {
        return rackIngresa;
    }
    
    public void setRackIngresa(String rackIngresa) {
        this.rackIngresa = rackIngresa;
    }
    
    public String getActividadRemedy() {
        return actividadRemedy;
    }
    
    public void setActividadRemedy(String actividadRemedy) {
        this.actividadRemedy = actividadRemedy;
    }
    
    public Date getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDate getFechaFinFicticia() {
        return fechaFinFicticia;
    }
    
    public void setFechaFinFicticia(LocalDate fechaFinFicticia) {
        this.fechaFinFicticia = fechaFinFicticia;
    }
    
    public LocalTime getHoraFinFicticia() {
        return horaFinFicticia;
    }
    
    public void setHoraFinFicticia(LocalTime horaFinFicticia) {
        this.horaFinFicticia = horaFinFicticia;
    }
    
    public LocalTime getHoraSupervisionMedia() {
        return horaSupervisionMedia;
    }
    
    public void setHoraSupervisionMedia(LocalTime horaSupervisionMedia) {
        this.horaSupervisionMedia = horaSupervisionMedia;
    }
    
    public LocalDate getFechaSupervisionMedia() {
        return fechaSupervisionMedia;
    }
    
    public void setFechaSupervisionMedia(LocalDate fechaSupervisionMedia) {
        this.fechaSupervisionMedia = fechaSupervisionMedia;
    }
    
    public Boolean getSegundaSupervisionRealizada() {
        return segundaSupervisionRealizada;
    }
    
    public void setSegundaSupervisionRealizada(Boolean segundaSupervisionRealizada) {
        this.segundaSupervisionRealizada = segundaSupervisionRealizada;
    }
    
    public LocalDate getFechaSegundaSupervision() {
        return fechaSegundaSupervision;
    }
    
    public void setFechaSegundaSupervision(LocalDate fechaSegundaSupervision) {
        this.fechaSegundaSupervision = fechaSegundaSupervision;
    }
    
    public LocalTime getHoraSegundaSupervision() {
        return horaSegundaSupervision;
    }
    
    public void setHoraSegundaSupervision(LocalTime horaSegundaSupervision) {
        this.horaSegundaSupervision = horaSegundaSupervision;
    }
    @Column(name = "foto_tecnico", columnDefinition = "LONGTEXT")
    private String fotoTecnico;

    @Column(name = "coordenadas_gps", columnDefinition = "TEXT")
    private String coordenadasGps;

    public String getFotoTecnico() {
        return fotoTecnico;
    }

    public void setFotoTecnico(String fotoTecnico) {
        this.fotoTecnico = fotoTecnico;
    }

    public String getCoordenadasGps() {
        return coordenadasGps;
    }

    public void setCoordenadasGps(String coordenadasGps) {
        this.coordenadasGps = coordenadasGps;
    }
    
    @Override
    public String toString() {
        return "IngresoAP{" +
                "id=" + id +
                ", turno='" + turno + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", horaInicio=" + horaInicio +
                ", fechaTermino=" + fechaTermino +
                ", horaTermino=" + horaTermino +
                ", nombreTecnico='" + nombreTecnico + '\'' +
                ", rutTecnico='" + rutTecnico + '\'' +
                ", empresaDemandante='" + empresaDemandante + '\'' +
                ", empresaContratista='" + empresaContratista + '\'' +
                ", cargoTecnico='" + cargoTecnico + '\'' +
                ", salaRemedy='" + salaRemedy + '\'' +
                ", tipoTicket='" + tipoTicket + '\'' +
                ", numeroTicket='" + numeroTicket + '\'' +
                ", aprobador='" + aprobador + '\'' +
                ", escolta='" + escolta + '\'' +
                ", motivoIngreso='" + motivoIngreso + '\'' +
                ", guiaDespacho='" + guiaDespacho + '\'' +
                ", salaIngresa='" + salaIngresa + '\'' +
                ", rackIngresa='" + rackIngresa + '\'' +
                ", actividadRemedy='" + actividadRemedy + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", activo=" + activo +
                '}';
    }
}
