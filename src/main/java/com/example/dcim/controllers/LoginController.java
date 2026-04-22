package com.example.dcim.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam(name = "username", required = false) String username,
                                @RequestParam(name = "password", required = false) String password,
                                Model model) {
        // Procesamiento simple para pruebas; reemplaza con autenticación real si se desea
        System.out.println("[LoginController] Usuario: " + username);
        System.out.println("[LoginController] Contraseña: " + password);
        model.addAttribute("mensaje", "Login recibido para " + (username != null ? username : "(sin usuario)"));
        return "login";
    }
}
