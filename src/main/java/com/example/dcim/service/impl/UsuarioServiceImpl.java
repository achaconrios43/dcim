package com.example.dcim.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.entity.Usuario;
import com.example.dcim.service.ImportSqlService;
import com.example.dcim.service.UsuarioService;

/**
 * Implementación del servicio para lógica de negocio de Usuario
 * Maneja toda la lógica de negocio y validaciones para gestión de usuarios
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private IUsuarioDao usuarioRepository;
    
    @Autowired
    private ImportSqlService importSqlService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Patrón para validar email
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN_COMPILED = Pattern.compile(EMAIL_PATTERN);
    
    // Patrón para validar RUT chileno (con y sin puntos)
    private static final String RUT_PATTERN_WITH_DOTS = "^[0-9]{1,2}\\.[0-9]{3}\\.[0-9]{3}-[0-9Kk]$";
    private static final String RUT_PATTERN_WITHOUT_DOTS = "^[0-9]{7,8}-[0-9Kk]$";
    private static final Pattern RUT_PATTERN_WITH_DOTS_COMPILED = Pattern.compile(RUT_PATTERN_WITH_DOTS);
    private static final Pattern RUT_PATTERN_WITHOUT_DOTS_COMPILED = Pattern.compile(RUT_PATTERN_WITHOUT_DOTS);
    
    @Override
    public Usuario crearUsuario(Usuario usuario) throws Exception {
        // Validaciones de negocio
        if (usuario == null) {
            throw new Exception("El usuario no puede ser nulo");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new Exception("El email es obligatorio");
        }
        
        if (usuario.getRut() == null || usuario.getRut().trim().isEmpty()) {
            throw new Exception("El RUT es obligatorio");
        }
        
        if (!validarEmail(usuario.getEmail())) {
            throw new Exception("El formato del email no es válido");
        }
        
        if (!validarRut(usuario.getRut())) {
            throw new Exception("El formato del RUT no es válido");
        }
        
        if (existeEmail(usuario.getEmail())) {
            throw new Exception("Ya existe un usuario con este email");
        }
        
        if (existeRut(usuario.getRut())) {
            throw new Exception("Ya existe un usuario con este RUT");
        }
        
        // Encriptar la contraseña antes de guardar
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            String passwordEncriptado = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(passwordEncriptado);
        }
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Sincronizar con import.sql
        importSqlService.updateImportSqlAfterUserOperation();
        
        return usuarioGuardado;
    }
    
    @Override
    public Usuario actualizarUsuario(Usuario usuario) throws Exception {
        if (usuario == null || usuario.getId() == null) {
            throw new Exception("El usuario y su ID no pueden ser nulos");
        }
        
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getId());
        if (!usuarioExistente.isPresent()) {
            throw new Exception("El usuario no existe");
        }
        
        // Validar email solo si cambió
        Usuario existente = usuarioExistente.get();
        if (!existente.getEmail().equals(usuario.getEmail()) && existeEmail(usuario.getEmail())) {
            throw new Exception("Ya existe otro usuario con este email");
        }
        
        // Validar RUT solo si cambió
        if (!existente.getRut().equals(usuario.getRut()) && existeRut(usuario.getRut())) {
            throw new Exception("Ya existe otro usuario con este RUT");
        }
        
        // Encriptar la contraseña solo si cambió
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            // Si la contraseña no está encriptada (no comienza con $2a$ de BCrypt)
            if (!usuario.getPassword().startsWith("$2a$")) {
                String passwordEncriptado = passwordEncoder.encode(usuario.getPassword());
                usuario.setPassword(passwordEncriptado);
            }
        } else {
            // Si no se envió contraseña, mantener la existente
            usuario.setPassword(existente.getPassword());
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        // Sincronizar con import.sql
        importSqlService.updateImportSqlAfterUserOperation();
        
        return usuarioActualizado;
    }
    
    @Override
    public void eliminarUsuario(Long id) throws Exception {
        if (id == null) {
            throw new Exception("El ID no puede ser nulo");
        }
        
        if (!usuarioRepository.existsById(id)) {
            throw new Exception("El usuario no existe");
        }
        
        usuarioRepository.deleteById(id);
        
        // Sincronizar con import.sql
        importSqlService.updateImportSqlAfterUserOperation();
    }
    
    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return usuarioRepository.findById(id);
    }
    
    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Autentica un usuario verificando sus credenciales en la base de datos
     * 
     * NOTA: Este método ya NO es necesario con Spring Security activo.
     * Spring Security usa CustomUserDetailsService y PasswordEncoder automáticamente.
     * Se mantiene por compatibilidad pero NO debe usarse para login.
     * 
     * Proceso de autenticación con BCrypt:
     * 1. Valida que email/nombre y password no sean nulos
     * 2. Busca el usuario en BD por email O nombre
     * 3. Compara la contraseña ingresada con la encriptada usando BCrypt
     * 4. Registra logs detallados de cada paso para debugging
     * 
     * @param emailOrName Email o nombre del usuario para login
     * @param password Contraseña en texto plano a verificar
     * @return Optional con el usuario si autenticación exitosa, vacío si falla
     */
    @Override
    public Optional<Usuario> autenticarUsuario(String emailOrName, String password) {
        // Log: inicio del proceso de autenticación
        System.out.println("[UsuarioService] Intentando autenticar usuario: " + emailOrName);
        
        // Validación 1: verificar que las credenciales no sean nulas
        if (emailOrName == null || password == null) {
            System.out.println("[UsuarioService] Email o password es null");
            return Optional.empty();
        }
        
        // Búsqueda: consultar usuario en BD por email O nombre
        Optional<Usuario> usuario = usuarioRepository.findByEmailOrNombre(emailOrName);
        
        // Validación 2: verificar si el usuario existe
        if (usuario.isEmpty()) {
            System.out.println("[UsuarioService] Usuario NO encontrado en BD: " + emailOrName);
            return Optional.empty();
        }
        
        // Log: mostrar datos del usuario encontrado
        System.out.println("[UsuarioService] Usuario encontrado: " + usuario.get().getEmail());
        System.out.println("[UsuarioService] Verificando password con BCrypt...");
        
        // Validación 3: comparar contraseñas usando BCrypt
        boolean passwordCoincide = passwordEncoder.matches(password, usuario.get().getPassword());
        System.out.println("[UsuarioService] Passwords coinciden: " + passwordCoincide);
        
        if (passwordCoincide) {
            System.out.println("[UsuarioService] ✓ Autenticación exitosa");
            return usuario;  // Retorna el usuario autenticado
        }
        
        // Autenticación fallida: contraseña incorrecta
        System.out.println("[UsuarioService] ✗ Password incorrecto");
        return Optional.empty();
    }
    
    @Override
    public boolean validarCredenciales(String emailOrName, String password) {
        return autenticarUsuario(emailOrName, password).isPresent();
    }
    
    @Override
    public boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN_COMPILED.matcher(email.trim()).matches();
    }
    
    @Override
    public boolean validarRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }
        
        String rutLimpio = rut.trim();
        
        // Verificar formato (con o sin puntos)
        boolean formatoValido = RUT_PATTERN_WITH_DOTS_COMPILED.matcher(rutLimpio).matches() || 
                               RUT_PATTERN_WITHOUT_DOTS_COMPILED.matcher(rutLimpio).matches();
        
        if (!formatoValido) {
            return false;
        }
        
        // Normalizar RUT (quitar puntos)
        String rutNormalizado = normalizarRut(rutLimpio);
        
        // Validar dígito verificador
        return validateDigitoVerificador(rutNormalizado);
    }
    
    /**
     * Normaliza el RUT quitando puntos y convirtiendo a mayúsculas
     */
    private String normalizarRut(String rut) {
        return rut.replace(".", "").toUpperCase();
    }
    
    /**
     * Valida el dígito verificador del RUT chileno
     */
    private boolean validateDigitoVerificador(String rut) {
        try {
            String[] parts = rut.split("-");
            if (parts.length != 2) {
                return false;
            }
            
            int numero = Integer.parseInt(parts[0]);
            String digitoVerificador = parts[1];
            
            // Calcular dígito verificador
            int suma = 0;
            int multiplicador = 2;
            
            while (numero > 0) {
                suma += (numero % 10) * multiplicador;
                numero /= 10;
                multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
            }
            
            int resto = suma % 11;
            int digitoCalculado = 11 - resto;
            
            String digitoEsperado = switch (digitoCalculado) {
                case 11 -> "0";
                case 10 -> "K";
                default -> String.valueOf(digitoCalculado);
            };
            
            return digitoVerificador.equals(digitoEsperado);
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(email.trim());
    }
    
    @Override
    public Optional<Usuario> obtenerUsuarioPorRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioRepository.findByRut(rut.trim());
    }
    
    @Override
    public List<Usuario> obtenerUsuariosPorRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            return List.of();
        }
        return usuarioRepository.findByRolOrderByNombre(rol.trim());
    }
    
    @Override
    public boolean existeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByEmail(email.trim());
    }
    
    @Override
    public boolean existeRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByRut(rut.trim());
    }
    
    @Override
    public long contarUsuarios() {
        return usuarioRepository.count();
    }
    
    @Override
    public long contarUsuariosPorRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            return 0;
        }
        return usuarioRepository.findByRol(rol.trim()).size();
    }
    
    @Override
    public boolean tienePermisoAdmin(Usuario usuario) {
        return usuario != null && "ADMIN".equalsIgnoreCase(usuario.getRol());
    }
    
    @Override
    public List<Usuario> obtenerAdministradores() {
        return usuarioRepository.findByRolOrderByNombre("ADMIN");
    }
    
    @Override
    public List<Usuario> obtenerTecnicos() {
        return usuarioRepository.findByRolOrderByNombre("USER");
    }
}
