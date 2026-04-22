package com.example.dcim.api;

import com.example.dcim.entity.IngresoAP;
import com.example.dcim.service.IngresoAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingresos")
@CrossOrigin(origins = "*")
public class IngresoApiController {

    @Autowired
    private IngresoAPService ingresoAPService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<IngresoAP> ingresos = ingresoAPService.listarTodos();
            response.put("success", true);
            response.put("count", ingresos.size());
            response.put("data", ingresos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<IngresoAP> ingreso = ingresoAPService.buscarPorId(id);
            if (ingreso.isEmpty()) {
                response.put("success", false);
                response.put("message", "No encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("success", true);
            response.put("data", ingreso.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody IngresoAP ingreso) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (ingreso.getFechaRegistro() == null) { ingreso.setFechaRegistro(new Date());
            }
            IngresoAP nuevo = ingresoAPService.guardar(ingreso);
            response.put("success", true);
            response.put("message", "Ingreso creado");
            response.put("data", nuevo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody IngresoAP ingreso) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<IngresoAP> existente = ingresoAPService.buscarPorId(id);
            if (existente.isEmpty()) {
                response.put("success", false);
                response.put("message", "No encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ingreso.setId(id);
            IngresoAP actualizado = ingresoAPService.guardar(ingreso);
            response.put("success", true);
            response.put("message", "Ingreso actualizado");
            response.put("data", actualizado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<IngresoAP> existente = ingresoAPService.buscarPorId(id);
            if (existente.isEmpty()) {
                response.put("success", false);
                response.put("message", "No encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            ingresoAPService.eliminar(id);
            response.put("success", true);
            response.put("message", "Ingreso eliminado");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
