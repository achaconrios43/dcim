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

@Controller
public class IngresoController {

    private static final List<Map<String,String>> INGRESOS = new ArrayList<>();

    @GetMapping("/ingresoap")
    public String ingresoForm(@RequestParam(name="nombre", required=false) String nombre,
                              @RequestParam(name="rut", required=false) String rut,
                              Model model){
        // if params provided, prefill the form
        if (nombre != null) model.addAttribute("nombre", nombre);
        if (rut != null) model.addAttribute("rut", rut);
        return "ingresoap";
    }

    @PostMapping("/ingresoap")
    public String handleIngreso(@RequestParam(name="nombre", required=false) String nombre,
                                @RequestParam(name="rut", required=false) String rut,
                                @RequestParam(name="fecha", required=false) String fecha,
                                @RequestParam(name="hora", required=false) String hora,
                                @RequestParam(name="empresa", required=false) String empresa,
                                @RequestParam(name="contratista", required=false) String contratista,
                                @RequestParam(name="tipoRemedy", required=false) String tipoRemedy,
                                @RequestParam(name="ticket", required=false) String ticket,
                                @RequestParam(name="aprobador", required=false) String aprobador,
                                @RequestParam(name="motivo", required=false) String motivo,
                                Model model){
        // server-side validation
        if (nombre == null || nombre.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Nombre");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (rut == null || rut.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: RUT");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        // basic RUT pattern validation (accepts formats like 12.345.678-9 or 12345678-9)
        String rutClean = rut.replaceAll("\\s+","");
        if (!rutClean.matches("^\\d{1,3}(\\.\\d{3})*-?[\\dkK]$")){
            model.addAttribute("errorMessage", "RUT con formato inválido");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (fecha == null || fecha.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Fecha");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (hora == null || hora.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Hora");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (empresa == null || empresa.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Empresa");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (contratista == null || contratista.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Contratista");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (tipoRemedy == null || tipoRemedy.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Tipo de REMEDY");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (ticket == null || ticket.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Número de ticket");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (aprobador == null || aprobador.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Aprobador");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }
        if (motivo == null || motivo.trim().isEmpty()){
            model.addAttribute("errorMessage", "Falta el campo: Motivo");
            addValuesToModel(model, nombre,rut,fecha,hora,empresa,contratista,tipoRemedy,ticket,aprobador,motivo);
            return "ingresoap";
        }

        Map<String,String> ingreso = new HashMap<>();
        ingreso.put("nombre", nombre);
        ingreso.put("rut", rut);
        ingreso.put("fecha", fecha);
        ingreso.put("hora", hora);
        ingreso.put("empresa", empresa);
        ingreso.put("contratista", contratista);
        ingreso.put("tipoRemedy", tipoRemedy);
        ingreso.put("ticket", ticket);
        ingreso.put("aprobador", aprobador);
        ingreso.put("motivo", motivo);
        INGRESOS.add(ingreso);

        model.addAttribute("successMessage", "Ingreso registrado correctamente");
        return "ingresoap";
    }

    private void addValuesToModel(Model model, String nombre, String rut, String fecha, String hora, String empresa, String contratista, String tipoRemedy, String ticket, String aprobador, String motivo){
        model.addAttribute("nombre", nombre);
        model.addAttribute("rut", rut);
        model.addAttribute("fecha", fecha);
        model.addAttribute("hora", hora);
        model.addAttribute("empresa", empresa);
        model.addAttribute("contratista", contratista);
        model.addAttribute("tipoRemedy", tipoRemedy);
        model.addAttribute("ticket", ticket);
        model.addAttribute("aprobador", aprobador);
        model.addAttribute("motivo", motivo);
    }
}
