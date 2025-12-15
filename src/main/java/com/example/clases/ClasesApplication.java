package com.example.clases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.clases.dao.IUsuarioDao;
import com.example.clases.entity.Usuario;

/**
 * Clase principal de la aplicación Spring Boot - Sistema de Gestión de Accesos
 * 
 * @author Arturo Chacón
 * @version 2.0
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.clases.dao")
public class ClasesApplication {

	@Autowired
	private IUsuarioDao usuarioDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ClasesApplication.class, args);
	}

	/**
	 * Inicializador de datos
	 * Crea usuario admin por defecto si no existe
	 * DESHABILITADO - Usando import.sql en su lugar
	 */
	/*
	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			// Verificar si ya existe un usuario admin
			if (usuarioDao.findByEmail("admin@clases.com").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setRut("11111111-1");
				admin.setNombre("Administrador");
				admin.setApellido("Sistema");
				admin.setEmail("admin@clases.com");
				admin.setPassword(passwordEncoder.encode("Admin123!"));
				admin.setRol("ADMIN");
				
				usuarioDao.save(admin);
				
				System.out.println("\n========================================");
				System.out.println("✅ USUARIO ADMIN CREADO AUTOMÁTICAMENTE");
				System.out.println("========================================");
				System.out.println("Email: admin@clases.com");
				System.out.println("Password: Admin123!");
				System.out.println("Rol: ADMIN");
				System.out.println("========================================");
				System.out.println("⚠️  CAMBIAR PASSWORD EN PRODUCCIÓN");
				System.out.println("========================================\n");
			}
		};
	}
	*/
}
