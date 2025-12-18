package com.example.clases.entity;

import jakarta.persistence.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "ingresoap")
public class IngresoAP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("turno")
    @Column(nullable = false, length = 10)
    private String turno;

    @JsonProperty("nombre_usuario")
    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String nombreUsuario;

    @JsonProperty("fecha_inicio")
    @Column(name = "fecha_inicio", nullable = false)
    private String fechaInicio;

    @JsonProperty("hora_inicio")
    @Column(name = "hora_inicio", nullable = false)
    private String horaInicio;

    @JsonProperty("fecha_termino")
    @Column(name = "fecha_termino")
    private String fechaTermino;

    @JsonProperty("hora_termino")
    @Column(name = "hora_termino")
    private String horaTermino;

    @JsonProperty("fecha_fin_ficticia")
    @Column(name = "fecha_fin_ficticia")
    private String fechaFinFicticia;

    @JsonProperty("hora_fin_ficticia")
    @Column(name = "hora_fin_ficticia")
    private String horaFinFicticia;

    @JsonProperty("hora_supervision_media")
    @Column(name = "hora_supervision_media")
    private String horaSupervisionMedia;

    @JsonProperty("fecha_supervision_media")
    @Column(name = "fecha_supervision_media")
    private String fechaSupervisionMedia;

    @JsonProperty("segunda_supervision_realizada")
    @Column(name = "segunda_supervision_realizada")
    private Boolean segundaSupervisionRealizada = false;

    @JsonProperty("fecha_segunda_supervision")
    @Column(name = "fecha_segunda_supervision")
    private String fechaSegundaSupervision;

    @JsonProperty("hora_segunda_supervision")
    @Column(name = "hora_segunda_supervision")
    private String horaSegundaSupervision;

    @JsonProperty("nombre_tecnico")
    @Column(name = "nombre_tecnico", nullable = false, length = 100)
    private String nombreTecnico;

    @JsonProperty("rut_tecnico")
    @Column(name = "rut_tecnico", nullable = false, length = 12)
    private String rutTecnico;

    @JsonProperty("empresa_demandante")
    @Column(name = "empresa_demandante", nullable = false, length = 100)
    private String empresaDemandante;

    @JsonProperty("empresa_contratista")
    @Column(name = "empresa_contratista", nullable = false, length = 100)
    private String empresaContratista;

    @JsonProperty("cargo_tecnico")
    @Column(name = "cargo_tecnico", nullable = false, length = 100)
    private String cargoTecnico;

    @JsonProperty("sala_remedy")
    @Column(name = "sala_remedy", nullable = false, length = 50)
    private String salaRemedy;

    @JsonProperty("tipo_ticket")
    @Column(name = "tipo_ticket", nullable = false, length = 30)
    private String tipoTicket;

    @JsonProperty("numero_ticket")
    @Column(name = "numero_ticket", nullable = false, length = 50)
    private String numeroTicket;

    @JsonProperty("aprobador")
    @Column(name = "aprobador", nullable = false, length = 100)
    private String aprobador;

    @JsonProperty("escolta")
    @Column(name = "escolta", nullable = false, length = 50)
    private String escolta;

    @JsonProperty("motivo_ingreso")
    @Column(name = "motivo_ingreso", nullable = false, length = 100)
    private String motivoIngreso;

    @JsonProperty("guia_despacho")
    @Column(name = "guia_despacho", length = 50)
    private String guiaDespacho;

    @JsonProperty("sitio_ingreso")
    @Column(name = "sitio_ingreso", length = 100)
    private String sitioIngreso;

    @JsonProperty("sala_ingresa")
    @Column(name = "sala_ingresa", nullable = false, columnDefinition = "TEXT")
    private String salaIngresa;

    @JsonProperty("rack_ingresa")
    @Column(name = "rack_ingresa", length = 50)
    private String rackIngresa;

    @JsonProperty("actividad_remedy")
    @Column(name = "actividad_remedy", nullable = false, columnDefinition = "TEXT")
    private String actividadRemedy;

    @JsonProperty("fecha_registro")
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @JsonProperty("activo")
    @Column(nullable = false)
    private Boolean activo = true;

    @JsonProperty("foto_tecnico")
    @Column(name = "foto_tecnico", columnDefinition = "LONGTEXT")
    private String fotoTecnico;

    @JsonProperty("coordenadas_gps")
    @Column(name = "coordenadas_gps", columnDefinition = "TEXT")
    private String coordenadasGps;

    // Constructor vacío
    public IngresoAP() {
        this.fechaRegistro = new Date();
        this.activo = true;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getFechaTermino() { return fechaTermino; }
    public void setFechaTermino(String fechaTermino) { this.fechaTermino = fechaTermino; }

    public String getHoraTermino() { return horaTermino; }
    public void setHoraTermino(String horaTermino) { this.horaTermino = horaTermino; }

    public String getFechaFinFicticia() { return fechaFinFicticia; }
    public void setFechaFinFicticia(String fechaFinFicticia) { this.fechaFinFicticia = fechaFinFicticia; }

    public String getHoraFinFicticia() { return horaFinFicticia; }
    public void setHoraFinFicticia(String horaFinFicticia) { this.horaFinFicticia = horaFinFicticia; }

    public String getHoraSupervisionMedia() { return horaSupervisionMedia; }
    public void setHoraSupervisionMedia(String horaSupervisionMedia) { this.horaSupervisionMedia = horaSupervisionMedia; }

    public String getFechaSupervisionMedia() { return fechaSupervisionMedia; }
    public void setFechaSupervisionMedia(String fechaSupervisionMedia) { this.fechaSupervisionMedia = fechaSupervisionMedia; }

    public Boolean getSegundaSupervisionRealizada() { return segundaSupervisionRealizada; }
    public void setSegundaSupervisionRealizada(Boolean segundaSupervisionRealizada) { this.segundaSupervisionRealizada = segundaSupervisionRealizada; }

    public String getFechaSegundaSupervision() { return fechaSegundaSupervision; }
    public void setFechaSegundaSupervision(String fechaSegundaSupervision) { this.fechaSegundaSupervision = fechaSegundaSupervision; }

    public String getHoraSegundaSupervision() { return horaSegundaSupervision; }
    public void setHoraSegundaSupervision(String horaSegundaSupervision) { this.horaSegundaSupervision = horaSegundaSupervision; }

    public String getNombreTecnico() { return nombreTecnico; }
    public void setNombreTecnico(String nombreTecnico) { this.nombreTecnico = nombreTecnico; }

    public String getRutTecnico() { return rutTecnico; }
    public void setRutTecnico(String rutTecnico) { this.rutTecnico = rutTecnico; }

    public String getEmpresaDemandante() { return empresaDemandante; }
    public void setEmpresaDemandante(String empresaDemandante) { this.empresaDemandante = empresaDemandante; }

    public String getEmpresaContratista() { return empresaContratista; }
    public void setEmpresaContratista(String empresaContratista) { this.empresaContratista = empresaContratista; }

    public String getCargoTecnico() { return cargoTecnico; }
    public void setCargoTecnico(String cargoTecnico) { this.cargoTecnico = cargoTecnico; }

    public String getSalaRemedy() { return salaRemedy; }
    public void setSalaRemedy(String salaRemedy) { this.salaRemedy = salaRemedy; }

    public String getTipoTicket() { return tipoTicket; }
    public void setTipoTicket(String tipoTicket) { this.tipoTicket = tipoTicket; }

    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }

    public String getAprobador() { return aprobador; }
    public void setAprobador(String aprobador) { this.aprobador = aprobador; }

    public String getEscolta() { return escolta; }
    public void setEscolta(String escolta) { this.escolta = escolta; }

    public String getMotivoIngreso() { return motivoIngreso; }
    public void setMotivoIngreso(String motivoIngreso) { this.motivoIngreso = motivoIngreso; }

    public String getGuiaDespacho() { return guiaDespacho; }
    public void setGuiaDespacho(String guiaDespacho) { this.guiaDespacho = guiaDespacho; }

    public String getSitioIngreso() { return sitioIngreso; }
    public void setSitioIngreso(String sitioIngreso) { this.sitioIngreso = sitioIngreso; }

    public String getSalaIngresa() { return salaIngresa; }
    public void setSalaIngresa(String salaIngresa) { this.salaIngresa = salaIngresa; }

    public String getRackIngresa() { return rackIngresa; }
    public void setRackIngresa(String rackIngresa) { this.rackIngresa = rackIngresa; }

    public String getActividadRemedy() { return actividadRemedy; }
    public void setActividadRemedy(String actividadRemedy) { this.actividadRemedy = actividadRemedy; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getFotoTecnico() { return fotoTecnico; }
    public void setFotoTecnico(String fotoTecnico) { this.fotoTecnico = fotoTecnico; }

    public String getCoordenadasGps() { return coordenadasGps; }
    public void setCoordenadasGps(String coordenadasGps) { this.coordenadasGps = coordenadasGps; }
}
