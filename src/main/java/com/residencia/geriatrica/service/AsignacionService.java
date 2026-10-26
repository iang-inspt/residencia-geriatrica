package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.Enfermero;
import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.model.Residente;

import com.residencia.geriatrica.repository.EnfermeroRepository;
import com.residencia.geriatrica.repository.HabitacionRepository;
import com.residencia.geriatrica.repository.ResidenteRepository;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignacionService {

    private final HabitacionRepository habitacionRepository;
    private final EnfermeroRepository enfermeroRepository;
    private final ResidenteRepository residenteRepository;

    public AsignacionService(
            HabitacionRepository habitacionRepository,
            EnfermeroRepository enfermeroRepository,
            ResidenteRepository residenteRepository) {

        this.habitacionRepository = habitacionRepository;
        this.enfermeroRepository = enfermeroRepository;
        this.residenteRepository = residenteRepository;
    }

    // ASIGNAR ENFERMERO A UNA HABITACION
    @Transactional
    public Habitacion asignarEnfermero(Long codigo, Long enfermeroId) {

        Habitacion habitacion = habitacionRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La habitación no existe"));

        Enfermero enfermero = enfermeroRepository.findById(enfermeroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El enfermero no existe"));

        if (habitacion.getEnfermero() != null) {
            throw new IllegalStateException(
                    "La habitación ya tiene un enfermero asignado");
        }

        habitacion.setEnfermero(enfermero);

        return habitacionRepository.save(habitacion);
    }

    // DESASIGNAR ENFERMERO DE UNA HABITACION
    @Transactional
    public void desasignarEnfermero(Long codigo) {

        Habitacion habitacion = habitacionRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La habitación no existe"));

        if (habitacion.getEnfermero() == null) {
            throw new IllegalStateException(
                    "La habitación no tiene enfermero asignado");
        }

        habitacion.setEnfermero(null);

        habitacionRepository.save(habitacion);
    }

    // CAMBIAR EL ENFERMERO ASIGNADO
    @Transactional
    public Habitacion cambiarEnfermero(Long codigo, Long nuevoEnfermeroId) {

        Habitacion habitacion = habitacionRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La habitación no existe"));

        if (habitacion.getEnfermero() == null) {
            throw new IllegalStateException(
                    "La habitación no tiene enfermero asignado");
        }

        Enfermero nuevoEnfermero = enfermeroRepository.findById(nuevoEnfermeroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El enfermero no existe"));

        if (Objects.equals(
                habitacion.getEnfermero().getId(),
                nuevoEnfermeroId)) {

            throw new IllegalStateException(
                    "El enfermero ya está asignado a esta habitación");
        }

        habitacion.setEnfermero(nuevoEnfermero);

        return habitacionRepository.save(habitacion);
    }

    // LISTAR HABITACIONES DE UN ENFERMERO
    @Transactional(readOnly = true)
    public List<Habitacion> listarHabitacionesPorEnfermero(Long enfermeroId) {

        if (!enfermeroRepository.existsById(enfermeroId)) {
            throw new IllegalArgumentException(
                    "El enfermero no existe");
        }

        return habitacionRepository.findByEnfermeroId(enfermeroId);
    }

    // LISTAR RESIDENTES ATENDIDOS POR UN ENFERMERO
    @Transactional(readOnly = true)
    public List<Residente> listarResidentesPorEnfermero(Long enfermeroId) {

        List<Habitacion> habitaciones =
                listarHabitacionesPorEnfermero(enfermeroId);

        return habitaciones.stream()
                .map(Habitacion::getResidente)
                .filter(Objects::nonNull)
                .toList();
    }

    // OBTENER ENFERMERO ASIGNADO A UN RESIDENTE
    @Transactional(readOnly = true)
    public Enfermero buscarEnfermeroPorResidente(Long residenteId) {

        if (!residenteRepository.existsById(residenteId)) {
            throw new IllegalArgumentException(
                    "El residente no existe");
        }

        Habitacion habitacion =
                habitacionRepository.findByResidenteId(residenteId)
                        .orElseThrow(() -> new IllegalStateException(
                                "El residente no tiene habitación asignada"));

        if (habitacion.getEnfermero() == null) {
            throw new IllegalStateException(
                    "La habitación no tiene enfermero asignado");
        }

        return habitacion.getEnfermero();
    }
}