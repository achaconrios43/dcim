package com.example.dcim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.dcim.entity.GestionAcceso;
import com.example.dcim.entity.IngresoAP;

/**
 * DTO que combina un IngresoAP con su GestionAcceso asociada (si existe)
 * y calcula el estado de alerta de horario para el dashboard diario.
 *
 * Tipos de alerta:
 *  - ACTIVIDAD : hay GestionAcceso con fechas de programación de actividad
 *  - REMEDY    : hay GestionAcceso con horarios de Remedy
 *  - FICTICIO  : no hay GestionAcceso → se usa hora_fin_ficticia del IngresoAP
 *
 * Estado:
 *  - OK       : dentro del horario
 *  - PROXIMO  : quedan ≤ 30 minutos para vencer
 *  - VENCIDO  : ya pasó la hora límite
 */
public class RegistroActivoDto {

    // === Datos del IngresoAP ===
    private Long id;
    private String numeroTicket;
    private String nombreTecnico;
    private String empresaContratista;
    private String salaIngresa;
    private LocalTime horaInicio;
    private String tipoTicket;
    private LocalDate fechaInicio;

    // === Datos de programación de actividad (GestionAcceso) ===
    private LocalDate fechaInicioActividad;
    private LocalTime horaInicioActividad;
    private LocalDate fechaTerminoActividad;
    private LocalTime horaTerminoActividad;

    // === Datos de horario Remedy (GestionAcceso) ===
    private LocalDate fechaInicioRemedy;
    private LocalTime horaInicioRemedy;
    private LocalDate fechaFinRemedy;
    private LocalTime horaFinRemedy;

    // === Horario ficticio (IngresoAP cuando no hay GestionAcceso) ===
    private LocalDate fechaFinFicticia;
    private LocalTime horaFinFicticia;

    // === Tipo y estado calculados ===
    private boolean tieneGestion;
    private String tipoAlerta;   // "ACTIVIDAD" | "REMEDY" | "FICTICIO"
    private String estadoAlerta; // "OK" | "PROXIMO" | "VENCIDO"
    private LocalDateTime limiteHorario; // fecha+hora límite para mostrar en la tabla

    // ---------------------------------------------------------------

    public static RegistroActivoDto of(IngresoAP ingreso, GestionAcceso gestion) {
        RegistroActivoDto dto = new RegistroActivoDto();
        dto.id                  = ingreso.getId();
        dto.numeroTicket        = ingreso.getNumeroTicket();
        dto.nombreTecnico       = ingreso.getNombreTecnico();
        dto.empresaContratista  = ingreso.getEmpresaContratista();
        dto.salaIngresa         = ingreso.getSalaIngresa();
        dto.horaInicio          = ingreso.getHoraInicio();
        dto.tipoTicket          = ingreso.getTipoTicket();
        dto.fechaInicio         = ingreso.getFechaInicio();
        dto.fechaFinFicticia    = ingreso.getFechaFinFicticia();
        dto.horaFinFicticia     = ingreso.getHoraFinFicticia();

        LocalDateTime ahora = LocalDateTime.now();

        if (gestion != null) {
            dto.tieneGestion = true;
            dto.fechaInicioActividad  = gestion.getFechaInicioActividad();
            dto.horaInicioActividad   = gestion.getHoraInicioActividad();
            dto.fechaTerminoActividad = gestion.getFechaTerminoActividad();
            dto.horaTerminoActividad  = gestion.getHoraTerminoActividad();
            dto.fechaInicioRemedy     = gestion.getFechaInicioRemedy();
            dto.horaInicioRemedy      = gestion.getHoraInicioRemedy();
            dto.fechaFinRemedy        = gestion.getFechaFinRemedy();
            dto.horaFinRemedy         = gestion.getHoraFinRemedy();

            // Preferir alerta de actividad si tiene fecha/hora término
            if (dto.fechaTerminoActividad != null && dto.horaTerminoActividad != null) {
                dto.tipoAlerta = "ACTIVIDAD";
                dto.limiteHorario = LocalDateTime.of(dto.fechaTerminoActividad, dto.horaTerminoActividad);
            } else if (dto.fechaFinRemedy != null && dto.horaFinRemedy != null) {
                dto.tipoAlerta = "REMEDY";
                dto.limiteHorario = LocalDateTime.of(dto.fechaFinRemedy, dto.horaFinRemedy);
            } else {
                // GestionAcceso sin fechas → caer a ficticio si existe
                dto.tipoAlerta = ficticioONull(dto);
                dto.limiteHorario = buildLimiteFicticio(dto);
            }
        } else {
            dto.tieneGestion = false;
            dto.tipoAlerta = ficticioONull(dto);
            dto.limiteHorario = buildLimiteFicticio(dto);
        }

        dto.estadoAlerta = calcularEstado(dto.limiteHorario, ahora);
        return dto;
    }

    private static String ficticioONull(RegistroActivoDto dto) {
        return (dto.fechaFinFicticia != null && dto.horaFinFicticia != null) ? "FICTICIO" : "SIN_LIMITE";
    }

    private static LocalDateTime buildLimiteFicticio(RegistroActivoDto dto) {
        if (dto.fechaFinFicticia != null && dto.horaFinFicticia != null) {
            return LocalDateTime.of(dto.fechaFinFicticia, dto.horaFinFicticia);
        }
        return null;
    }

    private static String calcularEstado(LocalDateTime limite, LocalDateTime ahora) {
        if (limite == null) return "SIN_LIMITE";
        if (ahora.isAfter(limite)) return "VENCIDO";
        if (ahora.isAfter(limite.minusMinutes(30))) return "PROXIMO";
        return "OK";
    }

    // ==================== Getters ====================
    public Long getId() { return id; }
    public String getNumeroTicket() { return numeroTicket; }
    public String getNombreTecnico() { return nombreTecnico; }
    public String getEmpresaContratista() { return empresaContratista; }
    public String getSalaIngresa() { return salaIngresa; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public String getTipoTicket() { return tipoTicket; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaInicioActividad() { return fechaInicioActividad; }
    public LocalTime getHoraInicioActividad() { return horaInicioActividad; }
    public LocalDate getFechaTerminoActividad() { return fechaTerminoActividad; }
    public LocalTime getHoraTerminoActividad() { return horaTerminoActividad; }
    public LocalDate getFechaInicioRemedy() { return fechaInicioRemedy; }
    public LocalTime getHoraInicioRemedy() { return horaInicioRemedy; }
    public LocalDate getFechaFinRemedy() { return fechaFinRemedy; }
    public LocalTime getHoraFinRemedy() { return horaFinRemedy; }
    public LocalDate getFechaFinFicticia() { return fechaFinFicticia; }
    public LocalTime getHoraFinFicticia() { return horaFinFicticia; }
    public boolean isTieneGestion() { return tieneGestion; }
    public String getTipoAlerta() { return tipoAlerta; }
    public String getEstadoAlerta() { return estadoAlerta; }
    public LocalDateTime getLimiteHorario() { return limiteHorario; }
}
