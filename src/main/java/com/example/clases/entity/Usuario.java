package com.example.clases.entity;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique = true, nullable = false, length = 12)
  private String rut;
  
  @Column(nullable = false, length = 100)
  private String nombre;
  
  @Column(nullable = false, length = 100)
  private String apellido;
  
  @Column(unique = true, nullable = false, length = 150)
  private String email;
  
  @Column(nullable = false)
  private String password;
  
  @Column(nullable = false, length = 20)
  private String rol;
  
  @Column(name = "creat_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date creatAt;
  
  @Column(name = "update_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateAt;

    // No-arg constructor
    public Usuario() {
    }

    public Usuario(String rut, String nombre, String apellido, String email, String password, String rol) {
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.creatAt = new Date();
        this.updateAt = new Date();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
        this.updateAt = new Date();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.updateAt = new Date();
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
        this.updateAt = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updateAt = new Date();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.updateAt = new Date();
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
        this.updateAt = new Date();
    }

    public Date getCreatAt() {
        return creatAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}


