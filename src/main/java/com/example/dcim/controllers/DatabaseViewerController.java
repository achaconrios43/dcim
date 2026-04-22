package com.example.dcim.controllers;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.dcim.dao.IGestionAccesoDao;
import com.example.dcim.dao.IIngresoAPDao;
import com.example.dcim.dao.IUsuarioDao;

@Controller
public class DatabaseViewerController {

    private final IUsuarioDao usuarioDao;
    private final IIngresoAPDao ingresoAPDao;
    private final IGestionAccesoDao gestionAccesoDao;

    public DatabaseViewerController(IUsuarioDao usuarioDao, IIngresoAPDao ingresoAPDao, IGestionAccesoDao gestionAccesoDao) {
        this.usuarioDao = usuarioDao;
        this.ingresoAPDao = ingresoAPDao;
        this.gestionAccesoDao = gestionAccesoDao;
    }

    @GetMapping("/database-viewer")
    public String databaseViewer(Model model) {
        model.addAttribute("usuarioCount", usuarioDao.count());
        model.addAttribute("ingresoCount", ingresoAPDao.count());
        model.addAttribute("gestionCount", gestionAccesoDao.count());

        model.addAttribute("usuarios", usuarioDao.findAll(Sort.by(Sort.Direction.ASC, "id")).stream().limit(25).toList());
        model.addAttribute("ingresos", ingresoAPDao.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().limit(25).toList());
        model.addAttribute("gestiones", gestionAccesoDao.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().limit(25).toList());

        return "database-viewer";
    }
}