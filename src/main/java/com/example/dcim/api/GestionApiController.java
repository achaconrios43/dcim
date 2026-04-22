package com.example.dcim.api;

import com.example.dcim.entity.GestionAcceso;
import com.example.dcim.service.GestionAccesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API REST para Gestión de Accesos desde aplicación móvil
 * 
 * Endpoints CRUD completos para GestionAcceso
 * Ruta base: /api/gestiones
 */
@RestController
@RequestMapping("/api/gestiones")
@CrossOrigin(origins = "*")
public class GestionApiController {

    @Autowired
    private GestionAccesoService gestionAccesoService;

    /**
     * GET /api/gestiones
     * Listar todas las gestiones o filtrar por sitio
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarGestiones(
            @RequestParam(required = false) String sitio,
            @RequestParam(required = false) String estado) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<GestionAcceso> gestiones;
            
            if (sitio != null && !sitio.trim().isEmpty() && estado != null && !estado.trim().isEmpty()) {
                gestiones = gestionAccesoService.listarPorEstadoYSitio(estado, sitio);
            } else if (sitio != null && !sitio.trim().isEmpty()) {
                gestiones = gestionAccesoService.listarPorSitio(sitio);
            } else {
                gestiones = gestionAccesoService.listarTodas();
            }
            
            response.put("success", true);
            response.put("count", gestiones.size());
            response.put("data", gestiones);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener gestiones: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/gestiones/{id}
     * Obtener una gestión específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerGestion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<GestionAcceso> gestion = gestionAccesoService.buscarPorId(id);
            
            if (gestion.isEmpty()) {
                response.put("success", false);
                response.put("message", "Gestión no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("data", gestion.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener gestión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/gestiones
     * Crear una nueva gestión desde app móvil
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearGestion(@RequestBody GestionAcceso gestion) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar campos requeridos
            if (gestion.getSitio() == null || gestion.getSitio().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El sitio es requerido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (gestion.getNumeroTicket() == null || gestion.getNumeroTicket().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El número de ticket es requerido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Establecer valores por defecto si no vienen
            if (gestion.getFechaRegistro() == null) {
                gestion.setFechaRegistro(LocalDate.now());
            }
            if (gestion.getHoraRegistro() == null) {
                gestion.setHoraRegistro(LocalTime.now());
            }
            if (gestion.getEstadoAprobacion() == null || gestion.getEstadoAprobacion().trim().isEmpty()) {
                gestion.setEstadoAprobacion("Pendiente");
            }
            if (gestion.getGestionRealizada() == null) {
                gestion.setGestionRealizada(false);
            }
            if (gestion.getTicketCerrado() == null) {
                gestion.setTicketCerrado(false);
            }
            
            // Guardar gestión
            GestionAcceso nuevaGestion = gestionAccesoService.guardar(gestion);
            
            response.put("success", true);
            response.put("message", "Gestión creada exitosamente");
            response.put("data", nuevaGestion);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear gestión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/gestiones/{id}
     * Actualizar una gestión existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarGestion(
            @PathVariable Long id,
            @RequestBody GestionAcceso gestion) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar que la gestión existe
            Optional<GestionAcceso> existente = gestionAccesoService.buscarPorId(id);
            if (existente.isEmpty()) {
                response.put("success", false);
                response.put("message", "Gestión no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Actualizar
            gestion.setId(id);
            GestionAcceso actualizada = gestionAccesoService.guardar(gestion);
            
            response.put("success", true);
            response.put("message", "Gestión actualizada exitosamente");
            response.put("data", actualizada);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar gestión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PATCH /api/gestiones/{id}/estado
     * Actualizar solo el estado de una gestión
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> estadoData) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<GestionAcceso> existente = gestionAccesoService.buscarPorId(id);
            if (existente.isEmpty()) {
                response.put("success", false);
                response.put("message", "Gestión no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            GestionAcceso gestion = existente.get();
            String nuevoEstado = estadoData.get("estado");
            
            if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                gestion.setEstadoAprobacion(nuevoEstado);
                gestionAccesoService.guardar(gestion);
                
                response.put("success", true);
                response.put("message", "Estado actualizado exitosamente");
                response.put("data", gestion);
            } else {
                response.put("success", false);
                response.put("message", "Estado no válido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/gestiones/{id}
     * Eliminar una gestión
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarGestion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<GestionAcceso> existente = gestionAccesoService.buscarPorId(id);
            if (existente.isEmpty()) {
                response.put("success", false);
                response.put("message", "Gestión no encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            gestionAccesoService.eliminar(id);
            
            response.put("success", true);
            response.put("message", "Gestión eliminada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar gestión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/gestiones/estadisticas
     * Obtener estadísticas de gestiones
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas(
            @RequestParam String sitio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();
            
            Map<String, Object> estadisticas = new HashMap<>();
            
            // Contadores
            Long gestionesDelDia = gestionAccesoService.contarGestionesDelDia(fechaConsulta, sitio);
            Long ticketsAprobados = gestionAccesoService.contarTicketsPorEstadoYFecha("Aprobada", fechaConsulta, sitio);
            Long ticketsPendientes = gestionAccesoService.contarTicketsPorEstado("Pendiente", sitio);
            Long ticketsRechazados = gestionAccesoService.contarTicketsRechazadosHoy(fechaConsulta, sitio);
            Long ticketsDevueltos = gestionAccesoService.contarTicketsDevueltosHoy(fechaConsulta, sitio);
            Long pendientesCierre = gestionAccesoService.contarTicketsPendientesCierre(sitio);
            
            estadisticas.put("gestionesDelDia", gestionesDelDia != null ? gestionesDelDia : 0);
            estadisticas.put("ticketsAprobados", ticketsAprobados != null ? ticketsAprobados : 0);
            estadisticas.put("ticketsPendientes", ticketsPendientes != null ? ticketsPendientes : 0);
            estadisticas.put("ticketsRechazados", ticketsRechazados != null ? ticketsRechazados : 0);
            estadisticas.put("ticketsDevueltos", ticketsDevueltos != null ? ticketsDevueltos : 0);
            estadisticas.put("pendientesCierre", pendientesCierre != null ? pendientesCierre : 0);
            estadisticas.put("fecha", fechaConsulta);
            
            response.put("success", true);
            response.put("data", estadisticas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
