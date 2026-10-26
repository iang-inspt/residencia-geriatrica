package com.residencia.geriatrica.controller;

import com.residencia.geriatrica.model.EstadoHabitacion;
import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.service.HabitacionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    // LISTAR HABITACIONES
    @GetMapping
    public String listar(
            @RequestParam(required = false) EstadoHabitacion estado,
            Model model) {

        if (estado == null) {
            model.addAttribute("habitaciones",
                    habitacionService.listarHabitaciones());
        } else {
            model.addAttribute("habitaciones",
                    habitacionService.listarPorEstado(estado));
        }

        model.addAttribute("estadoSeleccionado", estado);

        return "habitaciones/listar";
    }

    // AGREGAR HABITACION
    @PostMapping("/guardar")
    public String guardar(RedirectAttributes redirectAttributes) {

        Habitacion habitacion = habitacionService.agregarHabitacion();

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Habitación " + habitacion.getCodigo()
                + " creada correctamente");

        return "redirect:/habitaciones";
    }

    // CONSULTAR DETALLE DE HABITACION
    @GetMapping("/detalle/{codigo}")
    public String detalle(
            @PathVariable Long codigo,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Habitacion habitacion =
                    habitacionService.buscarPorCodigo(codigo);

            model.addAttribute("habitacion", habitacion);

            return "habitaciones/detalle";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/habitaciones";
        }
    }

    // ELIMINAR HABITACION
    @PostMapping("/eliminar/{codigo}")
    public String eliminar(
            @PathVariable Long codigo,
            RedirectAttributes redirectAttributes) {

        try {
            habitacionService.eliminarHabitacion(codigo);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Habitación eliminada correctamente");

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/habitaciones";
    }
}