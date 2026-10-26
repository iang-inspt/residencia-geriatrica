package com.residencia.geriatrica.controller;

import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.service.AsignacionService;
import com.residencia.geriatrica.service.EnfermeroService;
import com.residencia.geriatrica.service.HabitacionService;
import com.residencia.geriatrica.service.ResidenteService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final HabitacionService habitacionService;
    private final EnfermeroService enfermeroService;
    private final ResidenteService residenteService;

    public AsignacionController(
            AsignacionService asignacionService,
            HabitacionService habitacionService,
            EnfermeroService enfermeroService,
            ResidenteService residenteService) {

        this.asignacionService = asignacionService;
        this.habitacionService = habitacionService;
        this.enfermeroService = enfermeroService;
        this.residenteService = residenteService;
    }

    // LISTAR ASIGNACIONES
    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "habitaciones",
                habitacionService.listarHabitaciones()
        );

        return "asignaciones/listar";
    }

    // FORMULARIO PARA ASIGNAR ENFERMERO
    @GetMapping("/nueva")
    public String nueva(Model model) {

        model.addAttribute(
                "habitaciones",
                habitacionService.listarHabitaciones()
                        .stream()
                        .filter(h -> h.getEnfermero() == null)
                        .toList()
        );

        model.addAttribute(
                "enfermeros",
                enfermeroService.listarEnfermeros()
        );

        model.addAttribute("edicion", false);

        return "asignaciones/formulario";
    }

    // GUARDAR ASIGNACION
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Long codigo,
            @RequestParam Long enfermeroId,
            RedirectAttributes redirectAttributes) {

        try {
            asignacionService.asignarEnfermero(codigo, enfermeroId);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Enfermero asignado correctamente"
            );

            return "redirect:/asignaciones";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/asignaciones/nueva";
        }
    }

    // FORMULARIO PARA CAMBIAR ENFERMERO
    @GetMapping("/cambiar/{codigo}")
    public String cambiar(
            @PathVariable Long codigo,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Habitacion habitacion
                    = habitacionService.buscarPorCodigo(codigo);

            if (habitacion.getEnfermero() == null) {
                throw new IllegalStateException(
                        "La habitación no tiene enfermero asignado");
            }

            model.addAttribute("habitacion", habitacion);

            model.addAttribute(
                    "enfermeros",
                    enfermeroService.listarEnfermeros()
            );

            model.addAttribute("edicion", true);

            return "asignaciones/formulario";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/asignaciones";
        }
    }

    // ACTUALIZAR ENFERMERO ASIGNADO
    @PostMapping("/actualizar/{codigo}")
    public String actualizar(
            @PathVariable Long codigo,
            @RequestParam Long enfermeroId,
            RedirectAttributes redirectAttributes) {

        try {
            asignacionService.cambiarEnfermero(codigo, enfermeroId);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Enfermero modificado correctamente"
            );

            return "redirect:/asignaciones";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/asignaciones/cambiar/" + codigo;
        }
    }

    // DESASIGNAR ENFERMERO
    @PostMapping("/eliminar/{codigo}")
    public String eliminar(
            @PathVariable Long codigo,
            RedirectAttributes redirectAttributes) {

        try {
            asignacionService.desasignarEnfermero(codigo);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Enfermero desasignado correctamente"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/asignaciones";
    }

    // MOSTRAR PANTALLA DE CONSULTAS
    @GetMapping("/consultas")
    public String consultas(Model model) {

        model.addAttribute(
                "enfermeros",
                enfermeroService.listarEnfermeros()
        );

        model.addAttribute(
                "residentes",
                residenteService.listarResidentes()
        );

        return "asignaciones/consultas";
    }

    // CONSULTAR RESIDENTES ATENDIDOS POR UN ENFERMERO
    @GetMapping("/consultas/enfermero/{id}")
    public String residentesPorEnfermero(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute(
                    "enfermero",
                    enfermeroService.buscarPorId(id)
            );

            model.addAttribute(
                    "residentesAtendidos",
                    asignacionService.listarResidentesPorEnfermero(id)
            );

            model.addAttribute(
                    "enfermeros",
                    enfermeroService.listarEnfermeros()
            );

            model.addAttribute(
                    "residentes",
                    residenteService.listarResidentes()
            );

            return "asignaciones/consultas";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/asignaciones/consultas";
        }
    }

    // CONSULTAR ENFERMERO ASIGNADO A UN RESIDENTE
    @GetMapping("/consultas/residente/{id}")
    public String enfermeroPorResidente(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute(
                    "residenteConsultado",
                    residenteService.buscarPorId(id)
            );

            model.addAttribute(
                    "enfermeroResponsable",
                    asignacionService.buscarEnfermeroPorResidente(id)
            );

            model.addAttribute(
                    "enfermeros",
                    enfermeroService.listarEnfermeros()
            );

            model.addAttribute(
                    "residentes",
                    residenteService.listarResidentes()
            );

            return "asignaciones/consultas";

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/asignaciones/consultas";
        }
    }

    // RECIBIR CONSULTA DE RESIDENTES POR ENFERMERO
    @GetMapping("/consultas/enfermero")
    public String consultarResidentesPorEnfermero(
            @RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return residentesPorEnfermero(id, model, redirectAttributes);
    }

// RECIBIR CONSULTA DE ENFERMERO POR RESIDENTE
    @GetMapping("/consultas/residente")
    public String consultarEnfermeroPorResidente(
            @RequestParam Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return enfermeroPorResidente(id, model, redirectAttributes);
    }
}
