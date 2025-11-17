package com.example.clases.service;

import com.example.clases.entity.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio para lógica de negocio de Usuario
 * Contiene las operaciones de negocio para gestión de usuarios del Centro de Datos
 */
public interface UsuarioService {
    
    // Operaciones CRUD con validación de negocio
    Usuario crearUsuario(Usuario usuario) throws Exception;
    Usuario actualizarUsuario(Usuario usuario) throws Exception;
    void eliminarUsuario(Long id) throws Exception;
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    List<Usuario> obtenerTodosLosUsuarios();
    
    // Autenticación y validación
    Optional<Usuario> autenticarUsuario(String emailOrName, String password);
    boolean validarCredenciales(String emailOrName, String password);
    boolean validarEmail(String email);
    boolean validarRut(String rut);
    
    // Búsquedas específicas
    Optional<Usuario> obtenerUsuarioPorEmail(String email);
    Optional<Usuario> obtenerUsuarioPorRut(String rut);
    List<Usuario> obtenerUsuariosPorRol(String rol);
    List<Usuario> obtenerUsuariosPorUbicacion(String ubicacion);
    
    // Validaciones de existencia
    boolean existeEmail(String email);
    boolean existeRut(String rut);
    
    // Operaciones de conteo y estadísticas
    long contarUsuarios();
    long contarUsuariosPorRol(String rol);
    
    // Gestión de roles y permisos
    boolean tienePermisoAdmin(Usuario usuario);
    List<Usuario> obtenerAdministradores();
    List<Usuario> obtenerTecnicos();
}