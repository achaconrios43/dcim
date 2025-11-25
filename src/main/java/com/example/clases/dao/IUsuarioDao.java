package com.example.clases.dao;

import com.example.clases.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de datos para Usuario usando Spring Data JPA
 */
@Repository
public interface IUsuarioDao extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByRut(String rut);
    List<Usuario> findByRol(String rol);
    List<Usuario> findByRolOrderByNombre(String rol);
    List<Usuario> findByUbicacion(String ubicacion);
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
    
    @Query("SELECT u FROM Usuario u WHERE u.email = :emailOrName OR u.nombre = :emailOrName")
    Optional<Usuario> findByEmailOrNombre(@Param("emailOrName") String emailOrName);
}
