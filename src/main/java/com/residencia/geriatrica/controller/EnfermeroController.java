package com.residencia.geriatrica.controller;

import com.residencia.geriatrica.model.Enfermero;
import com.residencia.geriatrica.service.EnfermeroService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enfermeros")
public class EnfermeroController {

    private final EnfermeroService enfermeroService;

    public EnfermeroController(EnfermeroService enfermeroService) {
        this.enfermeroService = enfermeroService;
    }

    // LISTAR ENFERMEROS
    @GetMapping
    public String listar(Model model) {

        model.addAttribute("enfermeros",
                enfermeroService.listarEnfermeros());

        return "enfermeros/listar";
    }

    // FORMULARIO DE ALTA
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("enfermero", new Enfermero());
        model.addAttribute("edicion", false);

        return "enfermeros/formulario";
    }

    // GUARDAR NUEVO ENFERMERO
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Integer dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            RedirectAttributes redirectAttributes) {

        try {
            enfermeroService.agregarEnfermero(dni, nombre, apellido);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Enfermero agregado correctamente");

            return "redirect:/enfermeros";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/enfermeros/nuevo";
        }
    }

    // FORMULARIO DE EDICION
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Enfermero enfermero = enfermeroService.buscarPorId(id);

            model.addAttribute("enfermero", enfermero);
            model.addAttribute("edicion", true);

            return "enfermeros/formulario";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/enfermeros";
        }
    }

    // ACTUALIZAR ENFERMERO
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam Integer dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            RedirectAttributes redirectAttributes) {

        try {
            enfermeroService.modificarEnfermero(
                    id, dni, nombre, apellido);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Enfermero modificado correctamente");

            return "redirect:/enfermeros";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/enfermeros/editar/" + id;
        }
    }

    // ELIMINAR ENFERMERO
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            enfermeroService.eliminarEnfermero(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Enfermero eliminado correctamente");

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/enfermeros";
    }
}
