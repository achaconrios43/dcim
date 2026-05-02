package com.example.dcim.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.dcim.entity.Usuario;

@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

    private Usuario usuario;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(
            "12345678-9",
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "password123",
            "USER"
        );
        usuario.setId(1L);
        
        usuario2 = new Usuario(
            "98765432-1",
            "María",
            "García",
            "maria@example.com",
            "pass456",
            "ADMIN"
        );
        usuario2.setId(2L);
    }

    @Test
    @DisplayName("Validar usuario creado correctamente")
    void testUsuarioCreado() {
        assertNotNull(usuario);
        assertEquals("12345678-9", usuario.getRut());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("juan.perez@example.com", usuario.getEmail());
    }

    @Test
    @DisplayName("Validar email contiene @")
    void testEmailValido() {
        assertTrue(usuario.getEmail().contains("@"));
        assertTrue(usuario.getEmail().contains("."));
    }

    @Test
    @DisplayName("Validar RUT tiene formato correcto")
    void testRutFormato() {
        assertTrue(usuario.getRut().contains("-"));
        assertTrue(usuario.getRut().length() >= 9); // Mínimo XX-XXX-XXX
    }

    @Test
    @DisplayName("Validar rol es USER o ADMIN")
    void testRolValido() {
        assertTrue(usuario.getRol().equals("USER") || usuario.getRol().equals("ADMIN"));
        assertTrue(usuario2.getRol().equals("ADMIN"));
    }

    @Test
    @DisplayName("Validar nombre no vacío")
    void testNombreNoVacio() {
        assertNotNull(usuario.getNombre());
        assertFalse(usuario.getNombre().isEmpty());
        assertEquals("Juan", usuario.getNombre());
    }

    @Test
    @DisplayName("Validar apellido no vacío")
    void testApellidoNoVacio() {
        assertNotNull(usuario.getApellido());
        assertFalse(usuario.getApellido().isEmpty());
        assertEquals("Pérez", usuario.getApellido());
    }

    @Test
    @DisplayName("Validar campos requeridos no nulos")
    void testCamposRequeridos() {
        assertNotNull(usuario.getNombre());
        assertNotNull(usuario.getApellido());
        assertNotNull(usuario.getEmail());
        assertNotNull(usuario.getRut());
        assertNotNull(usuario.getPassword());
        assertNotNull(usuario.getRol());
    }

    @Test
    @DisplayName("Validar setter y getter de ID")
    void testIdSetterGetter() {
        usuario.setId(100L);
        assertEquals(100L, usuario.getId());
    }

    @Test
    @DisplayName("Validar setter y getter de rol")
    void testRolSetterGetter() {
        usuario.setRol("ADMIN");
        assertEquals("ADMIN", usuario.getRol());
    }

    @Test
    @DisplayName("Comparar dos usuarios diferentes")
    void testUsuariosDiferentes() {
        assertNotEquals(usuario.getId(), usuario2.getId());
        assertNotEquals(usuario.getEmail(), usuario2.getEmail());
        assertNotEquals(usuario.getRut(), usuario2.getRut());
    }

    @Test
    @DisplayName("Validar constructor vacío")
    void testConstructorVacio() {
        Usuario usuarioVacio = new Usuario();
        assertNull(usuarioVacio.getId());
    }
}
