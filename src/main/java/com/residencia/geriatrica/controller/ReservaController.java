package com.residencia.geriatrica.controller;

import com.residencia.geriatrica.model.EstadoHabitacion;
import com.residencia.geriatrica.service.HabitacionService;
import com.residencia.geriatrica.service.ResidenteService;
import com.residencia.geriatrica.service.ReservaService;
import com.residencia.geriatrica.repository.HabitacionRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final HabitacionService habitacionService;
    private final ResidenteService residenteService;
    private final HabitacionRepository habitacionRepository;

    public ReservaController(
            ReservaService reservaService,
            HabitacionService habitacionService,
            ResidenteService residenteService,
            HabitacionRepository habitacionRepository) {

        this.reservaService = reservaService;
        this.habitacionService = habitacionService;
        this.residenteService = residenteService;
        this.habitacionRepository = habitacionRepository;
    }

    // LISTAR RESERVAS ACTIVAS
    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "reservas",
                reservaService.listarResidentesConHabitacion()
        );

        return "reservas/listar";
    }

    // MOSTRAR FORMULARIO DE RESERVA
    @GetMapping("/nueva")
    public String nueva(Model model) {

        model.addAttribute(
                "habitaciones",
                habitacionService.listarPorEstado(EstadoHabitacion.LIBRE)
                        .stream()
                        .filter(h -> h.getResidente() == null)
                        .toList()
        );

        model.addAttribute(
                "residentes",
                residenteService.listarResidentes()
                        .stream()
                        .filter(r -> !habitacionRepository
                                .existsByResidenteId(r.getId()))
                        .toList()
        );

        return "reservas/formulario";
    }

    // GUARDAR RESERVA
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Long codigo,
            @RequestParam Long residenteId,
            RedirectAttributes redirectAttributes) {

        try {
            reservaService.reservarHabitacion(codigo, residenteId);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Reserva creada correctamente"
            );

            return "redirect:/reservas";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/reservas/nueva";
        }
    }

    // CANCELAR RESERVA
    @PostMapping("/cancelar/{codigo}")
    public String cancelar(
            @PathVariable Long codigo,
            RedirectAttributes redirectAttributes) {

        try {
            reservaService.cancelarReserva(codigo);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Reserva cancelada correctamente"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/reservas";
    }
}