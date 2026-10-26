package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.EstadoHabitacion;
import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.repository.HabitacionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionService(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    // CREAR HABITACION
    @Transactional
    public Habitacion agregarHabitacion() {

        Habitacion habitacion = new Habitacion();

        habitacion.setEstado(EstadoHabitacion.LIBRE);

        return habitacionRepository.save(habitacion);
    }

    // LISTAR TODAS LAS HABITACIONES
    @Transactional(readOnly = true)
    public List<Habitacion> listarHabitaciones() {

        return habitacionRepository.findAll();
    }

    // BUSCAR HABITACION POR CODIGO
    @Transactional(readOnly = true)
    public Habitacion buscarPorCodigo(Long codigo) {

        return habitacionRepository.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una habitación con ese código"
                ));
    }

    // LISTAR HABITACIONES POR ESTADO
    @Transactional(readOnly = true)
    public List<Habitacion> listarPorEstado(EstadoHabitacion estado) {

        return habitacionRepository.findByEstado(estado);
    }

    // ELIMINAR HABITACION
    @Transactional
    public void eliminarHabitacion(Long codigo) {

        Habitacion habitacion = buscarPorCodigo(codigo);

        if (habitacion.getResidente() != null) {
            throw new IllegalStateException(
                    "No se puede eliminar una habitación ocupada"
            );
        }

        if (habitacion.getEnfermero() != null) {
            throw new IllegalStateException(
                    "No se puede eliminar una habitación con enfermero asignado"
            );
        }

        habitacionRepository.delete(habitacion);
    }
}