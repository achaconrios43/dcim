package com.example.clases.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
@RequestMapping("/user")
public class UserControlles {

    // in-memory storage for demo purposes
    private static final List<Map<String,String>> USUARIOS = new ArrayList<>();

    static {
        // seed with 20 users if empty
        for (int i = 1; i <= 20; i++){
            Map<String,String> u = new HashMap<>();
            u.put("nombre", "Nombre" + i);
            u.put("apellido", "Apellido" + i);
            u.put("email", "email" + i + "@mail.com");
            u.put("password", "password" + i);
            u.put("rut", "rut" + (i % 10));
            u.put("ubicacion", "Ubicacion" + i);
            u.put("rol", (i % 5 == 0) ? "ADMIN" : "USER");
            USUARIOS.add(u);
        }
    }

    @GetMapping("/profile")
    public String profile(){
        return "user/profile";
    }
    @GetMapping("/settings")
    public String settings(){
        return "user/settings";
    }

    @GetMapping("/create")
    public String createUser(){
        return "user/create";
    }

    @PostMapping("/create")
    public String handleCreate(@RequestParam(name="nombre", required=false) String nombre,
                               @RequestParam(name="apellido", required=false) String apellido,
                               @RequestParam(name="email", required=false) String email,
                               @RequestParam(name="password", required=false) String password,
                               @RequestParam(name="rut", required=false) String rut,
                               @RequestParam(name="ubicacion", required=false) String ubicacion,
                               @RequestParam(name="rol", required=false) String rol,
                               Model model){
        // simple validation
        if (nombre == null || nombre.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Nombre");
            return "user/create";
        }
        if (apellido == null || apellido.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Apellido");
            return "user/create";
        }
        if (email == null || email.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Email");
            return "user/create";
        }
        if (password == null || password.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Contraseña");
            return "user/create";
        }
        if (rut == null || rut.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: RUT");
            return "user/create";
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Ubicación");
            return "user/create";
        }
        if (rol == null || rol.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Rol");
            return "user/create";
        }

        Map<String,String> u = new HashMap<>();
        u.put("nombre", nombre);
        u.put("apellido", apellido);
        u.put("email", email);
        u.put("password", password);
        u.put("rut", rut);
        u.put("ubicacion", ubicacion);
        u.put("rol", rol);
        USUARIOS.add(u);

        return "redirect:/user/list";
    }

    @GetMapping("/read")
    public String readUser(){
        return "user/read";
    }

    @GetMapping("/list")
    public String listUser(Model model){
        model.addAttribute("usuarios", USUARIOS);
        return "user/list";
    }

    // GET /user/update?idx={n} -> show update form populated
    @GetMapping("/update")
    public String updateUser(@RequestParam(name = "idx", required = false) Integer idx, Model model){
        if (idx == null || idx < 0 || idx >= USUARIOS.size()){
            return "redirect:/user/list";
        }
        Map<String,String> u = USUARIOS.get(idx);
        model.addAttribute("idx", idx);
        model.addAttribute("usuario", u);
        return "user/update";
    }

    // POST /user/update -> apply changes
    @PostMapping("/update")
    public String handleUpdate(@RequestParam(name = "idx", required = true) Integer idx,
                               @RequestParam(name="nombre", required=false) String nombre,
                               @RequestParam(name="apellido", required=false) String apellido,
                               @RequestParam(name="email", required=false) String email,
                               @RequestParam(name="password", required=false) String password,
                               @RequestParam(name="rut", required=false) String rut,
                               @RequestParam(name="ubicacion", required=false) String ubicacion,
                               @RequestParam(name="rol", required=false) String rol,
                               Model model){
        if (idx == null || idx < 0 || idx >= USUARIOS.size()){
            return "redirect:/user/list";
        }
        // validate similar to create
        if (nombre == null || nombre.trim().isEmpty()){ model.addAttribute("errorMessage","Falta el campo: Nombre"); model.addAttribute("idx", idx); model.addAttribute("usuario", USUARIOS.get(idx)); return "user/update"; }
        if (apellido == null || apellido.trim().isEmpty()){ model.addAttribute("errorMessage","Falta el campo: Apellido"); model.addAttribute("idx", idx); model.addAttribute("usuario", USUARIOS.get(idx)); return "user/update"; }
        if (email == null || email.trim().isEmpty()){ model.addAttribute("errorMessage","Falta el campo: Email"); model.addAttribute("idx", idx); model.addAttribute("usuario", USUARIOS.get(idx)); return "user/update"; }
        if (password == null || password.trim().isEmpty()){ model.addAttribute("errorMessage","Falta el campo: Contraseña"); model.addAttribute("idx", idx); model.addAttribute("usuario", USUARIOS.get(idx)); return "user/update"; }
        if (rut == null || rut.trim().isEmpty()){ model.addAttribute("errorMessage","Falta el campo: RUT"); model.addAttribute("idx", idx); model.addAttribute("usuario", USUARIOS.get(idx)); return "user/update"; }
        if (ubicacion == null || ubicacion.trim().isEmpty()){ model.addAttribute("errorMessage","Falta el campo: Ubicación"); model.addAttribute("idx", idx); model.addAttribute("usuario", USUARIOS.get(idx)); return "user/update"; }
        if (rol == null || rol.trim().isEmpty()){ model.addAttribute("errorMessage","Falta el campo: Rol"); model.addAttribute("idx", idx); model.addAttribute("usuario", USUARIOS.get(idx)); return "user/update"; }

        Map<String,String> u = USUARIOS.get(idx);
        u.put("nombre", nombre);
        u.put("apellido", apellido);
        u.put("email", email);
        u.put("password", password);
        u.put("rut", rut);
        u.put("ubicacion", ubicacion);
        u.put("rol", rol);

        return "redirect:/user/list";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam(name = "idx", required = false) Integer idx, Model model){
        if (idx == null || idx < 0 || idx >= USUARIOS.size()){
            return "redirect:/user/list";
        }
        Map<String,String> u = USUARIOS.get(idx);
        model.addAttribute("idx", idx);
        model.addAttribute("usuario", u);
        return "user/delete";
    }

    @PostMapping("/delete")
    public String handleDelete(@RequestParam(name = "idx", required = false) Integer idx){
        if (idx != null && idx >= 0 && idx < USUARIOS.size()){
            USUARIOS.remove((int) idx);
        }
        return "redirect:/user/list";
    }

    @GetMapping("/exists")
    @ResponseBody
    public ResponseEntity<Boolean> emailExists(@RequestParam(name = "email", required = true) String email){
        if (email == null || email.trim().isEmpty()) return ResponseEntity.ok(false);
        String e = email.trim().toLowerCase();
        boolean exists = USUARIOS.stream().anyMatch(u -> {
            String ue = u.get("email");
            return ue != null && ue.trim().toLowerCase().equals(e);
        });
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/validate")
    @ResponseBody
    public Map<String, Object> validateUser(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "password", required = false) String password,
            @RequestParam(name = "ubicacion", required = false) String ubicacion) {

        Map<String, Object> resp = new HashMap<>();
        resp.put("redirect", false);

        if (email == null || password == null) {
            return resp;
        }

        // Asume que tienes una lista en memoria llamada 'usuarios' con getters: getEmail(), getPassword(), getNombre(), getRut(), getUbicacion()
        // Ajusta el nombre de la colección/propiedades si tu modelo es distinto.
        synchronized (USUARIOS) {
            for (Object o : USUARIOS) {
                // si usas POJO Usuario, castear y usar getters; si usas Map, obtener por claves
                if (o instanceof com.example.clases.entity.Usuario) {
                    com.example.clases.entity.Usuario u = (com.example.clases.entity.Usuario) o;
                    String uEmail = u.getEmail() != null ? u.getEmail() : "";
                    String uPass = u.getPassword() != null ? u.getPassword() : "";
                    // comparar email/username y password (case-insensitive en email)
                    if ((email.equalsIgnoreCase(uEmail) || email.equalsIgnoreCase(u.getNombre())) && password.equals(uPass)) {
                        // match -> redirigir a ingresoap con nombre y rut
                        String nombreEnc = URLEncoder.encode(u.getNombre(), StandardCharsets.UTF_8);
                        String rutEnc = URLEncoder.encode(u.getRut() == null ? "" : u.getRut(), StandardCharsets.UTF_8);
                        String url = "/ingresoap?nombre=" + nombreEnc + "&rut=" + rutEnc;
                        resp.put("redirect", true);
                        resp.put("url", url);
                        return resp;
                    }
                } else if (o instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String,String> m = (Map<String,String>) o;
                    String uEmail = m.getOrDefault("email","");
                    String uPass = m.getOrDefault("password","");
                    String uNombre = m.getOrDefault("nombre","");
                    String uRut = m.getOrDefault("rut","");
                    if ((email.equalsIgnoreCase(uEmail) || email.equalsIgnoreCase(uNombre)) && password.equals(uPass)) {
                        String nombreEnc = URLEncoder.encode(uNombre, StandardCharsets.UTF_8);
                        String rutEnc = URLEncoder.encode(uRut, StandardCharsets.UTF_8);
                        String url = "/ingresoap?nombre=" + nombreEnc + "&rut=" + rutEnc;
                        resp.put("redirect", true);
                        resp.put("url", url);
                        return resp;
                    }
                }
            }
        }

        return resp;
    }

}
