package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.Residente;
import com.residencia.geriatrica.repository.HabitacionRepository;
import com.residencia.geriatrica.repository.ResidenteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResidenteService {

    private final ResidenteRepository residenteRepository;
    private final HabitacionRepository habitacionRepository;

    public ResidenteService(
            ResidenteRepository residenteRepository,
            HabitacionRepository habitacionRepository) {

        this.residenteRepository = residenteRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Transactional
    public Residente agregarResidente(
            Integer dni,
            String nombre,
            String apellido) {

        if (residenteRepository.existsByDni(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe un residente con ese DNI");
        }

        Residente residente = new Residente(dni, nombre, apellido);

        return residenteRepository.save(residente);
    }

    @Transactional(readOnly = true)
    public List<Residente> listarResidentes() {
        return residenteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Residente buscarPorId(Long id) {
        return residenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe un residente con ese ID"));
    }

    @Transactional
    public Residente modificarResidente(
            Long id,
            Integer dni,
            String nombre,
            String apellido) {

        Residente residente = buscarPorId(id);

        if (!residente.getDni().equals(dni)
                && residenteRepository.existsByDni(dni)) {

            throw new IllegalArgumentException(
                    "Ya existe otro residente con ese DNI");
        }

        residente.setDni(dni);
        residente.setNombre(nombre);
        residente.setApellido(apellido);

        return residenteRepository.save(residente);
    }

    @Transactional
    public void eliminarResidente(Long id) {

        Residente residente = buscarPorId(id);

        if (habitacionRepository.existsByResidenteId(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar un residente con habitación asignada"
            );
        }

        residenteRepository.delete(residente);
    }
}
