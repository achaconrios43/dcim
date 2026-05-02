package com.example.dcim.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dcim.entity.Inventario;
import com.example.dcim.service.InventarioService;
import com.example.dcim.service.TemperaturaService;
import com.example.dcim.utils.ExcelImportUtil;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private TemperaturaService temperaturaService;

    // Listar todos
    @GetMapping
    public String listar(Model model) {
        List<Inventario> items = inventarioService.obtenerTodos();
        model.addAttribute("inventarios", items);
        model.addAttribute("total", items.size());
        return "inventario";
    }

    // Buscar
    @GetMapping("/buscar")
    public String buscar(@RequestParam("search") String search, Model model) {
        List<Inventario> items = inventarioService.buscar(search);
        model.addAttribute("inventarios", items);
        model.addAttribute("search", search);
        model.addAttribute("total", items.size());
        return "inventario";
    }

    // Mostrar formulario crear
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("inventario", new Inventario());
        model.addAttribute("sitiosCatalogo", temperaturaService.listarSitiosActivos());
        model.addAttribute("salasCatalogo", temperaturaService.listarTodasLasSalas());
        return "inventario-form";
    }

    // Guardar nuevo
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Inventario inventario, RedirectAttributes redirectAttributes) {
        try {
            inventarioService.crear(inventario);
            redirectAttributes.addFlashAttribute("success", "Inventario creado correctamente");
            return "redirect:/inventario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/inventario/crear";
        }
    }

    // Mostrar formulario editar
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Inventario> inventario = inventarioService.obtenerPorId(id);
        if (inventario.isPresent()) {
            model.addAttribute("inventario", inventario.get());
            model.addAttribute("editar", true);
            model.addAttribute("sitiosCatalogo", temperaturaService.listarSitiosActivos());
            model.addAttribute("salasCatalogo", temperaturaService.listarTodasLasSalas());
            return "inventario-form";
        }
        redirectAttributes.addFlashAttribute("error", "Inventario no encontrado");
        return "redirect:/inventario";
    }

    // Actualizar
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Inventario inventario, RedirectAttributes redirectAttributes) {
        try {
            inventario.setId(id);
            inventarioService.actualizar(inventario);
            redirectAttributes.addFlashAttribute("success", "Inventario actualizado correctamente");
            return "redirect:/inventario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/inventario/editar/" + id;
        }
    }

    // Ver detalles
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Inventario> inventario = inventarioService.obtenerPorId(id);
        if (inventario.isPresent()) {
            model.addAttribute("inventario", inventario.get());
            return "inventario-view";
        }
        redirectAttributes.addFlashAttribute("error", "Inventario no encontrado");
        return "redirect:/inventario";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Inventario eliminado correctamente");
            return "redirect:/inventario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/inventario";
        }
    }

    // Filtrar por estado
    @GetMapping("/filtrar/estado/{estado}")
    public String filtrarPorEstado(@PathVariable String estado, Model model) {
        List<Inventario> items = inventarioService.obtenerPorEstado(estado);
        model.addAttribute("inventarios", items);
        model.addAttribute("filtro", "Estado: " + estado);
        model.addAttribute("total", items.size());
        return "inventario";
    }

    // Filtrar por cliente
    @GetMapping("/filtrar/cliente/{cliente}")
    public String filtrarPorCliente(@PathVariable String cliente, Model model) {
        List<Inventario> items = inventarioService.obtenerPorCliente(cliente);
        model.addAttribute("inventarios", items);
        model.addAttribute("filtro", "Cliente: " + cliente);
        model.addAttribute("total", items.size());
        return "inventario";
    }

    // Filtrar por sala
    @GetMapping("/filtrar/sala/{sala}")
    public String filtrarPorSala(@PathVariable String sala, Model model) {
        List<Inventario> items = inventarioService.obtenerPorSala(sala);
        model.addAttribute("inventarios", items);
        model.addAttribute("filtro", "Sala: " + sala);
        model.addAttribute("total", items.size());
        return "inventario";
    }

    // Mostrar formulario importar Excel
    @GetMapping("/importar")
    public String mostrarFormularioImportar() {
        return "inventario-import";
    }

    // Importar desde Excel
    @PostMapping("/importar")
    public String importarExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor selecciona un archivo");
                return "redirect:/inventario/importar";
            }

            // Validar que sea Excel
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                redirectAttributes.addFlashAttribute("error", "El archivo debe ser Excel (.xlsx o .xls)");
                return "redirect:/inventario/importar";
            }

            // Importar datos del Excel
            List<Inventario> items = ExcelImportUtil.importarExcel(file);
            
            // Importar sin duplicados
            inventarioService.importarLote(items);

            redirectAttributes.addFlashAttribute("success", "Importados " + items.size() + " registros correctamente (duplicados actualizados)");
            return "redirect:/inventario";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el archivo: " + e.getMessage());
            return "redirect:/inventario/importar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/inventario/importar";
        }
    }
}
