package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.Residente;
import com.residencia.geriatrica.repository.ResidenteRepository;
import com.residencia.geriatrica.repository.HabitacionRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResidenteServiceTest {

    @Mock
    private ResidenteRepository residenteRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private ResidenteService residenteService;

    private Residente residente;

    @BeforeEach
    void setUp() {
        residente = new Residente(
                12345678,
                "Juan",
                "Perez"
        );
    }

    @Test
    void agregarResidenteCorrectamente() {

        when(residenteRepository.existsByDni(12345678))
                .thenReturn(false);

        when(residenteRepository.save(any(Residente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Residente resultado = residenteService.agregarResidente(
                12345678,
                "Juan",
                "Perez"
        );

        assertNotNull(resultado);
        assertEquals(12345678, resultado.getDni());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Perez", resultado.getApellido());

        verify(residenteRepository).save(any(Residente.class));
    }

    @Test
    void noPermitirDniDuplicado() {

        when(residenteRepository.existsByDni(12345678))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            residenteService.agregarResidente(
                    12345678,
                    "Juan",
                    "Perez"
            );
        });

        verify(residenteRepository, never())
                .save(any(Residente.class));
    }

    @Test
    void buscarResidenteExistente() {

        when(residenteRepository.findById(1L))
                .thenReturn(Optional.of(residente));

        Residente resultado = residenteService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());

        verify(residenteRepository).findById(1L);
    }

    @Test
    void buscarResidenteInexistente() {

        when(residenteRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            residenteService.buscarPorId(99L);
        });
    }

    @Test
    void listarResidentesCorrectamente() {

        Residente residente2 = new Residente(
                23456789,
                "Maria",
                "Gomez"
        );

        when(residenteRepository.findAll())
                .thenReturn(List.of(residente, residente2));

        List<Residente> resultado
                = residenteService.listarResidentes();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(residente));
        assertTrue(resultado.contains(residente2));

        verify(residenteRepository).findAll();
    }

    @Test
    void modificarResidenteCorrectamente() {

        when(residenteRepository.findById(1L))
                .thenReturn(Optional.of(residente));

        when(residenteRepository.save(any(Residente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Residente resultado = residenteService.modificarResidente(
                1L,
                12345678,
                "Carlos",
                "Lopez"
        );

        assertEquals("Carlos", resultado.getNombre());
        assertEquals("Lopez", resultado.getApellido());

        verify(residenteRepository).save(residente);
    }

    @Test
    void noPermitirModificarDniDuplicado() {

        when(residenteRepository.findById(1L))
                .thenReturn(Optional.of(residente));

        when(residenteRepository.existsByDni(99999999))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            residenteService.modificarResidente(
                    1L,
                    99999999,
                    "Carlos",
                    "Lopez"
            );
        });

        verify(residenteRepository, never())
                .save(any(Residente.class));
    }

    @Test
    void eliminarResidenteCorrectamente() {

        when(residenteRepository.findById(1L))
                .thenReturn(Optional.of(residente));

        residenteService.eliminarResidente(1L);

        verify(residenteRepository).delete(residente);
    }
}
