package com.example.dcim.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.entity.Usuario;

/**
 * Servicio personalizado para cargar detalles de usuario en Spring Security
 * 
 * Esta clase implementa UserDetailsService de Spring Security para:
 * - Buscar usuarios en nuestra base de datos
 * - Convertir nuestro Usuario a UserDetails (formato de Spring Security)
 * - Asignar roles y permisos
 * 
 * Spring Security usa esta clase automáticamente durante el proceso de autenticación
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUsuarioDao usuarioDao;

    /**
     * Carga un usuario por su email (username en el contexto de Spring Security)
     * 
     * Este método es llamado automáticamente por Spring Security cuando:
     * 1. Un usuario intenta hacer login
     * 2. Se necesita verificar permisos en una petición protegida
     * 
     * @param email El email del usuario (usado como username)
     * @return UserDetails con información del usuario y sus roles
     * @throws UsernameNotFoundException si el usuario no existe en la BD
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // Buscar usuario por email en la base de datos
        Usuario usuario = usuarioDao.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado con email: " + email
            ));

        // Convertir el rol del usuario a formato de Spring Security
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Agregar el rol del usuario (ADMIN o USER)
        // Spring Security requiere el prefijo "ROLE_" antes del nombre del rol
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));

        // Construir y retornar el objeto UserDetails
        // Spring Security usa esta información para:
        // - Verificar la contraseña (compara con BCrypt)
        // - Autorizar acceso a endpoints según roles
        // - Mantener la sesión del usuario
        return User.builder()
            .username(usuario.getEmail())  // Email como identificador único
            .password(usuario.getPassword())  // Contraseña encriptada con BCrypt
            .authorities(authorities)  // Lista de roles (ROLE_ADMIN o ROLE_USER)
            .accountExpired(false)  // La cuenta no está expirada
            .accountLocked(false)  // La cuenta no está bloqueada
            .credentialsExpired(false)  // Las credenciales no están expiradas
            .disabled(false)  // El usuario está habilitado
            .build();
    }

    /**
     * Método auxiliar para obtener el Usuario completo de la BD
     * Útil cuando necesitamos información adicional del usuario
     * que no está en UserDetails (como nombre, apellido, ubicación)
     * 
     * @param email Email del usuario
     * @return Usuario completo de la base de datos
     */
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioDao.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado: " + email
            ));
    }
}
