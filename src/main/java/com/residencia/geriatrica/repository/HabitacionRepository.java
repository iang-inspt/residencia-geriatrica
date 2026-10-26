package com.residencia.geriatrica.repository;

import com.residencia.geriatrica.model.EstadoHabitacion;
import com.residencia.geriatrica.model.Habitacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    Optional<Habitacion> findByResidenteId(Long residenteId);

    List<Habitacion> findByEnfermeroId(Long enfermeroId);

    boolean existsByResidenteId(Long residenteId);

    boolean existsByEnfermeroId(Long enfermeroId);

    List<Habitacion> findByEstado(EstadoHabitacion estado);
    
    List<Habitacion> findByResidenteIsNotNull();
}