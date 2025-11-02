package com.example.clases.entity;
import java.util.Date;



public class Usuario {
  private String rut;
  private String nombre;
  private String apellido;
  private String email;
  private String password;
  private String ubicacion;
  private String rol;
  private Date creatAt;
  private Date updateAt;

    // No-arg constructor
    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String password, String ubicacion, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.ubicacion = ubicacion;
        this.rol = rol;
       
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
    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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
                ", ubicacion='" + ubicacion + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}


