package com.example.dcim.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.dcim.dao.IUsuarioDao;
import com.example.dcim.entity.Usuario;

@Controller
public class MainController {

    @Autowired
    private IUsuarioDao usuarioDao;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Usuario usuario = usuarioDao.findByEmail(email).orElse(null);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
            }
        }
        return "dashboard";
    }
}