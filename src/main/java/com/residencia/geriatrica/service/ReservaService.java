package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.EstadoHabitacion;
import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.model.Residente;
import com.residencia.geriatrica.repository.HabitacionRepository;
import com.residencia.geriatrica.repository.ResidenteRepository;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    private final HabitacionRepository habitacionRepository;
    private final ResidenteRepository residenteRepository;

    public ReservaService(
            HabitacionRepository habitacionRepository,
            ResidenteRepository residenteRepository) {

        this.habitacionRepository = habitacionRepository;
        this.residenteRepository = residenteRepository;
    }

    // RESERVAR HABITACION
    @Transactional
    public Habitacion reservarHabitacion(Long codigo, Long residenteId) {

        Habitacion habitacion = habitacionRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La habitación no existe"));

        Residente residente = residenteRepository.findById(residenteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El residente no existe"));

        if (habitacion.getEstado() != EstadoHabitacion.LIBRE
                || habitacion.getResidente() != null) {
            throw new IllegalStateException(
                    "La habitación ya se encuentra ocupada");
        }

        if (habitacionRepository.existsByResidenteId(residenteId)) {
            throw new IllegalStateException(
                    "El residente ya tiene una habitación asignada");
        }

        habitacion.setResidente(residente);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);

        return habitacionRepository.save(habitacion);
    }

    // CANCELAR RESERVA
    @Transactional
    public void cancelarReserva(Long codigo) {

        Habitacion habitacion = habitacionRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La habitación no existe"));

        if (habitacion.getResidente() == null) {
            throw new IllegalStateException(
                    "La habitación no tiene una reserva activa");
        }

        habitacion.setResidente(null);
        habitacion.setEstado(EstadoHabitacion.LIBRE);

        habitacionRepository.save(habitacion);
    }

    // CONSULTAR HABITACION DE UN RESIDENTE
    @Transactional(readOnly = true)
    public Habitacion buscarHabitacionPorResidente(Long residenteId) {

        return habitacionRepository.findByResidenteId(residenteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El residente no tiene habitación asignada"));
    }

    // LISTAR HABITACIONES CON RESIDENTES
    @Transactional(readOnly = true)
    public List<Habitacion> listarResidentesConHabitacion() {

        return habitacionRepository.findByResidenteIsNotNull();
    }
}