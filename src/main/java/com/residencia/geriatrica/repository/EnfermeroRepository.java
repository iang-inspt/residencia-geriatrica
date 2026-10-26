package com.residencia.geriatrica.repository;

import com.residencia.geriatrica.model.Enfermero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnfermeroRepository
        extends JpaRepository<Enfermero, Long> {

    Optional<Enfermero> findByDni(Integer dni);

    boolean existsByDni(Integer dni);
}