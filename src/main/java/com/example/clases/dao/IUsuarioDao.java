package com.example.clases.dao;

import com.example.clases.entity.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz DAO para operaciones de acceso a datos de Usuario
 * Patrón DAO tradicional para gestión de usuarios del Centro de Datos
 */
public interface IUsuarioDao {
    
    // Operaciones CRUD básicas
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll();
    void delete(Usuario usuario);
    void deleteById(Long id);
    boolean existsById(Long id);
    
    // Búsquedas específicas
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByRut(String rut);
    Optional<Usuario> findByEmailOrNombre(String emailOrName);
    
    // Búsquedas por criterios
    List<Usuario> findByRol(String rol);
    List<Usuario> findByUbicacion(String ubicacion);
    List<Usuario> findByRolOrderByNombre(String rol);
    
    // Validaciones de existencia
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
    
    // Operaciones de conteo
    long count();
}