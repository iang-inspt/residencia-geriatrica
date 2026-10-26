package com.residencia.geriatrica.repository;

import com.residencia.geriatrica.model.Residente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidenteRepository
        extends JpaRepository<Residente, Long> {

    Optional<Residente> findByDni(Integer dni);

    boolean existsByDni(Integer dni);
}