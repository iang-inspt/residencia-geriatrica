package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.Enfermero;
import com.residencia.geriatrica.repository.EnfermeroRepository;
import com.residencia.geriatrica.repository.HabitacionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnfermeroService {

    private final EnfermeroRepository enfermeroRepository;
    private final HabitacionRepository habitacionRepository;

    public EnfermeroService(
            EnfermeroRepository enfermeroRepository,
            HabitacionRepository habitacionRepository) {

        this.enfermeroRepository = enfermeroRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Transactional
    public Enfermero agregarEnfermero(
            Integer dni,
            String nombre,
            String apellido) {

        if (enfermeroRepository.existsByDni(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe un enfermero con ese DNI");
        }

        Enfermero enfermero = new Enfermero(dni, nombre, apellido);

        return enfermeroRepository.save(enfermero);
    }

    @Transactional(readOnly = true)
    public List<Enfermero> listarEnfermeros() {
        return enfermeroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Enfermero buscarPorId(Long id) {
        return enfermeroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un enfermero con ese ID"));
    }

    @Transactional
    public Enfermero modificarEnfermero(
            Long id,
            Integer dni,
            String nombre,
            String apellido) {

        Enfermero enfermero = buscarPorId(id);

        if (!enfermero.getDni().equals(dni)
                && enfermeroRepository.existsByDni(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe otro enfermero con ese DNI");
        }

        enfermero.setDni(dni);
        enfermero.setNombre(nombre);
        enfermero.setApellido(apellido);

        return enfermeroRepository.save(enfermero);
    }

    @Transactional
    public void eliminarEnfermero(Long id) {

        Enfermero enfermero = buscarPorId(id);

        if (habitacionRepository.existsByEnfermeroId(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar un enfermero con habitaciones asignadas");
        }

        enfermeroRepository.delete(enfermero);
    }
}