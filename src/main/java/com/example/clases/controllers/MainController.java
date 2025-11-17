package com.example.clases.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/inventario")
    public String inventario(Model model) {
        model.addAttribute("pageTitle", "Inventario de Racks y Equipos");
        model.addAttribute("message", "Funcionalidad de inventario de racks, networks y servers en desarrollo");
        return "coming-soon";
    }

    @GetMapping("/temperatura")
    public String temperatura(Model model) {
        model.addAttribute("pageTitle", "Temperatura de Pasillos");
        model.addAttribute("message", "Funcionalidad de monitoreo de temperatura de pasillos del Data Center en desarrollo");
        return "coming-soon";
    }

    @GetMapping("/diseno")
    public String diseno(Model model) {
        model.addAttribute("pageTitle", "Layout de Salas Técnicas");
        model.addAttribute("message", "Funcionalidad de visualización de layouts y planos de salas técnicas en desarrollo");
        return "coming-soon";
    }

    @GetMapping("/estadisticas")
    public String estadisticas(Model model) {
        model.addAttribute("pageTitle", "Estadísticas del Data Center");
        model.addAttribute("message", "Funcionalidad de estadísticas y reportes del Data Center en desarrollo");
        return "coming-soon";
    }
}