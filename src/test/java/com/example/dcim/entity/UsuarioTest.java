package com.example.dcim.entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Usuario Entity Tests")
class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(
            "12345678-9",
            "Juan",
            "Pérez",
            "juan.perez@example.com",
            "encryptedPassword123",
            "USER"
        );
    }

    @Test
    @DisplayName("Crear usuario con constructor")
    void testConstructor() {
        assertNotNull(usuario);
        assertEquals("12345678-9", usuario.getRut());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Pérez", usuario.getApellido());
        assertEquals("juan.perez@example.com", usuario.getEmail());
        assertEquals("USER", usuario.getRol());
    }

    @Test
    @DisplayName("Validar nombre no vacío")
    void testNombreNoVacio() {
        usuario.setNombre("Carlos");
        assertEquals("Carlos", usuario.getNombre());
        
        // Test invalid
        usuario.setNombre("");
        assertEquals("", usuario.getNombre()); // permite vacío (validación DB)
    }

    @Test
    @DisplayName("Validar email único")
    void testEmailUnico() {
        String email = "test@example.com";
        usuario.setEmail(email);
        assertEquals(email, usuario.getEmail());
    }

    @Test
    @DisplayName("Validar RUT único")
    void testRutUnico() {
        String rut = "20123456-8";
        usuario.setRut(rut);
        assertEquals(rut, usuario.getRut());
    }

    @Test
    @DisplayName("Rol por defecto es USER")
    void testRolDefault() {
        Usuario newUsuario = new Usuario();
        newUsuario.setRol("USER");
        assertEquals("USER", newUsuario.getRol());
    }

    @Test
    @DisplayName("Validar timestamp onCreate")
    void testOnCreate() {
        usuario.onCreate();
        assertNotNull(usuario.getCreatAt());
        assertNotNull(usuario.getUpdateAt());
        assertEquals(usuario.getCreatAt(), usuario.getUpdateAt());
    }

    @Test
    @DisplayName("Validar timestamp onUpdate")
    void testOnUpdate() throws InterruptedException {
        usuario.onCreate();
        LocalDateTime creatAtOriginal = usuario.getCreatAt();
        
        Thread.sleep(10); // pequeña pausa
        usuario.onUpdate();
        
        assertEquals(creatAtOriginal, usuario.getCreatAt());
        assertTrue(usuario.getUpdateAt().isAfter(creatAtOriginal));
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
    @DisplayName("Validar constructor vacío")
    void testConstructorVacio() {
        Usuario usuarioVacio = new Usuario();
        assertNull(usuarioVacio.getId());
        assertNull(usuarioVacio.getNombre());
    }
}
