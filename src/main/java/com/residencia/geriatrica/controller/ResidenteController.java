package com.residencia.geriatrica.controller;

import com.residencia.geriatrica.model.Residente;
import com.residencia.geriatrica.service.ResidenteService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/residentes")
public class ResidenteController {

    private final ResidenteService residenteService;

    public ResidenteController(ResidenteService residenteService) {
        this.residenteService = residenteService;
    }

    // LISTAR RESIDENTES
    @GetMapping
    public String listar(Model model) {

        model.addAttribute("residentes",
                residenteService.listarResidentes());

        return "residentes/listar";
    }

    // MOSTRAR FORMULARIO DE ALTA
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("residente", new Residente());
        model.addAttribute("edicion", false);

        return "residentes/formulario";
    }

    // GUARDAR NUEVO RESIDENTE
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Integer dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            RedirectAttributes redirectAttributes) {

        try {
            residenteService.agregarResidente(dni, nombre, apellido);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Residente agregado correctamente");

            return "redirect:/residentes";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/residentes/nuevo";
        }
    }

    // MOSTRAR FORMULARIO DE EDICION
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Residente residente = residenteService.buscarPorId(id);

            model.addAttribute("residente", residente);
            model.addAttribute("edicion", true);

            return "residentes/formulario";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/residentes";
        }
    }

    // ACTUALIZAR RESIDENTE
    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam Integer dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            RedirectAttributes redirectAttributes) {

        try {
            residenteService.modificarResidente(
                    id, dni, nombre, apellido);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Residente modificado correctamente");

            return "redirect:/residentes";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/residentes/editar/" + id;
        }
    }

    // ELIMINAR RESIDENTE
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            residenteService.eliminarResidente(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Residente eliminado correctamente");

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/residentes";
    }
}