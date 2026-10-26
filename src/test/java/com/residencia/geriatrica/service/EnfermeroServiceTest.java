package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.Enfermero;
import com.residencia.geriatrica.repository.EnfermeroRepository;
import com.residencia.geriatrica.repository.HabitacionRepository;
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
class EnfermeroServiceTest {

    @Mock
    private EnfermeroRepository enfermeroRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private EnfermeroService enfermeroService;

    private Enfermero enfermero;

    @BeforeEach
    void setUp() {
        enfermero = new Enfermero(
                12345678,
                "Carlos",
                "Gomez"
        );
    }

    @Test
    void agregarEnfermeroCorrectamente() {

        when(enfermeroRepository.save(any(Enfermero.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Enfermero resultado = enfermeroService.agregarEnfermero(
                12345678,
                "Carlos",
                "Gomez"
        );

        assertNotNull(resultado);
        assertEquals(12345678, resultado.getDni());
        assertEquals("Carlos", resultado.getNombre());
        assertEquals("Gomez", resultado.getApellido());

        verify(enfermeroRepository).save(any(Enfermero.class));
    }

    @Test
    void noPermitirDniDuplicado() {

        when(enfermeroRepository.existsByDni(12345678))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            enfermeroService.agregarEnfermero(
                    12345678, "Carlos", "Gomez");
        });

        verify(enfermeroRepository, never())
                .save(any(Enfermero.class));
    }

    @Test
    void buscarEnfermeroExistente() {

        when(enfermeroRepository.findById(1L))
                .thenReturn(Optional.of(enfermero));

        Enfermero resultado = enfermeroService.buscarPorId(1L);

        assertSame(enfermero, resultado);
        verify(enfermeroRepository).findById(1L);
    }

    @Test
    void buscarEnfermeroInexistente() {

        when(enfermeroRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> enfermeroService.buscarPorId(99L));
    }

    @Test
    void listarEnfermerosCorrectamente() {

        Enfermero otroEnfermero =
                new Enfermero(23456789, "Ana", "Lopez");

        when(enfermeroRepository.findAll())
                .thenReturn(List.of(enfermero, otroEnfermero));

        List<Enfermero> resultado =
                enfermeroService.listarEnfermeros();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(enfermero));
        assertTrue(resultado.contains(otroEnfermero));

        verify(enfermeroRepository).findAll();
    }

    @Test
    void modificarEnfermeroCorrectamente() {

        when(enfermeroRepository.findById(1L))
                .thenReturn(Optional.of(enfermero));

        when(enfermeroRepository.save(enfermero))
                .thenReturn(enfermero);

        Enfermero resultado = enfermeroService.modificarEnfermero(
                1L, 12345678, "Pedro", "Martinez");

        assertEquals("Pedro", resultado.getNombre());
        assertEquals("Martinez", resultado.getApellido());

        verify(enfermeroRepository).save(enfermero);
    }

    @Test
    void noPermitirModificarDniDuplicado() {

        when(enfermeroRepository.findById(1L))
                .thenReturn(Optional.of(enfermero));

        when(enfermeroRepository.existsByDni(99999999))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            enfermeroService.modificarEnfermero(
                    1L, 99999999, "Pedro", "Martinez");
        });

        verify(enfermeroRepository, never())
                .save(any(Enfermero.class));
    }

    @Test
    void eliminarEnfermeroCorrectamente() {

        when(enfermeroRepository.findById(1L))
                .thenReturn(Optional.of(enfermero));

        enfermeroService.eliminarEnfermero(1L);

        verify(habitacionRepository).existsByEnfermeroId(1L);
        verify(enfermeroRepository).delete(enfermero);
    }

    @Test
    void noEliminarEnfermeroConHabitacionesAsignadas() {

        when(enfermeroRepository.findById(1L))
                .thenReturn(Optional.of(enfermero));

        when(habitacionRepository.existsByEnfermeroId(1L))
                .thenReturn(true);

        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> enfermeroService.eliminarEnfermero(1L)
        );

        assertEquals(
                "No se puede eliminar un enfermero con habitaciones asignadas",
                excepcion.getMessage()
        );

        verify(enfermeroRepository, never())
                .delete(any(Enfermero.class));
    }
}