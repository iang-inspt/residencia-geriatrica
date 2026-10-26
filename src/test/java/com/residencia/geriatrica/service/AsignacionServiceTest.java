package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.Enfermero;
import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.model.Residente;
import com.residencia.geriatrica.repository.EnfermeroRepository;
import com.residencia.geriatrica.repository.HabitacionRepository;
import com.residencia.geriatrica.repository.ResidenteRepository;

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
class AsignacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private EnfermeroRepository enfermeroRepository;

    @Mock
    private ResidenteRepository residenteRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    private Habitacion habitacion;
    private Enfermero enfermero;
    private Residente residente;

    @BeforeEach
    void setUp() {
        habitacion = new Habitacion();
        enfermero = new Enfermero(12345678, "Carlos", "Gomez");
        residente = new Residente(23456789, "Juan", "Perez");
    }

    // 1. ASIGNAR ENFERMERO CORRECTAMENTE
    @Test
    void asignarEnfermeroCorrectamente() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(enfermeroRepository.findById(1L))
                .thenReturn(Optional.of(enfermero));

        when(habitacionRepository.save(habitacion))
                .thenReturn(habitacion);

        Habitacion resultado =
                asignacionService.asignarEnfermero(1L, 1L);

        assertSame(enfermero, resultado.getEnfermero());
        verify(habitacionRepository).save(habitacion);
    }

    // 2. ASIGNAR EN HABITACION INEXISTENTE
    @Test
    void asignarHabitacionInexistente() {

        when(habitacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionService.asignarEnfermero(99L, 1L));

        verify(enfermeroRepository, never()).findById(anyLong());
        verify(habitacionRepository, never()).save(any());
    }

    // 3. ASIGNAR ENFERMERO INEXISTENTE
    @Test
    void asignarEnfermeroInexistente() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(enfermeroRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionService.asignarEnfermero(1L, 99L));

        verify(habitacionRepository, never()).save(any());
    }

    // 4. IMPEDIR DOBLE ASIGNACION
    @Test
    void noAsignarHabitacionConEnfermero() {

        habitacion.setEnfermero(enfermero);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(enfermeroRepository.findById(2L))
                .thenReturn(Optional.of(
                        new Enfermero(34567890, "Ana", "Lopez")));

        assertThrows(IllegalStateException.class,
                () -> asignacionService.asignarEnfermero(1L, 2L));

        verify(habitacionRepository, never()).save(any());
    }

    // 5. DESASIGNAR ENFERMERO CORRECTAMENTE
    @Test
    void desasignarEnfermeroCorrectamente() {

        habitacion.setEnfermero(enfermero);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        asignacionService.desasignarEnfermero(1L);

        assertNull(habitacion.getEnfermero());
        verify(habitacionRepository).save(habitacion);
    }

    // 6. DESASIGNAR HABITACION INEXISTENTE
    @Test
    void desasignarHabitacionInexistente() {

        when(habitacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionService.desasignarEnfermero(99L));

        verify(habitacionRepository, never()).save(any());
    }

    // 7. DESASIGNAR SIN ENFERMERO
    @Test
    void noDesasignarSinEnfermero() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        assertThrows(IllegalStateException.class,
                () -> asignacionService.desasignarEnfermero(1L));

        verify(habitacionRepository, never()).save(any());
    }

    // 8. CAMBIAR ENFERMERO CORRECTAMENTE
    @Test
    void cambiarEnfermeroCorrectamente() {

        Enfermero nuevoEnfermero =
                new Enfermero(34567890, "Ana", "Lopez");

        habitacion.setEnfermero(enfermero);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(enfermeroRepository.findById(2L))
                .thenReturn(Optional.of(nuevoEnfermero));

        when(habitacionRepository.save(habitacion))
                .thenReturn(habitacion);

        Habitacion resultado =
                asignacionService.cambiarEnfermero(1L, 2L);

        assertSame(nuevoEnfermero, resultado.getEnfermero());
        verify(habitacionRepository).save(habitacion);
    }

    // 9. CAMBIAR ENFERMERO DE HABITACION INEXISTENTE
    @Test
    void cambiarEnfermeroHabitacionInexistente() {

        when(habitacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionService.cambiarEnfermero(99L, 2L));

        verify(enfermeroRepository, never()).findById(anyLong());
    }

    // 10. CAMBIAR SIN ASIGNACION PREVIA
    @Test
    void noCambiarSinAsignacionPrevia() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        assertThrows(IllegalStateException.class,
                () -> asignacionService.cambiarEnfermero(1L, 2L));

        verify(habitacionRepository, never()).save(any());
    }

    // 11. CAMBIAR POR ENFERMERO INEXISTENTE
    @Test
    void cambiarPorEnfermeroInexistente() {

        habitacion.setEnfermero(enfermero);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(enfermeroRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> asignacionService.cambiarEnfermero(1L, 99L));

        verify(habitacionRepository, never()).save(any());
    }

    // 12. LISTAR HABITACIONES POR ENFERMERO
    @Test
    void listarHabitacionesPorEnfermeroCorrectamente() {

        Habitacion habitacion2 = new Habitacion();

        when(enfermeroRepository.existsById(1L))
                .thenReturn(true);

        when(habitacionRepository.findByEnfermeroId(1L))
                .thenReturn(List.of(habitacion, habitacion2));

        List<Habitacion> resultado =
                asignacionService.listarHabitacionesPorEnfermero(1L);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(habitacion));
        assertTrue(resultado.contains(habitacion2));
    }

    // 13. LISTAR HABITACIONES DE ENFERMERO INEXISTENTE
    @Test
    void listarHabitacionesEnfermeroInexistente() {

        assertThrows(IllegalArgumentException.class,
                () -> asignacionService.listarHabitacionesPorEnfermero(99L));

        verify(habitacionRepository, never())
                .findByEnfermeroId(anyLong());
    }

    // 14. LISTAR RESIDENTES POR ENFERMERO
    @Test
    void listarResidentesPorEnfermeroCorrectamente() {

        habitacion.setResidente(residente);

        Habitacion habitacionLibre = new Habitacion();

        when(enfermeroRepository.existsById(1L))
                .thenReturn(true);

        when(habitacionRepository.findByEnfermeroId(1L))
                .thenReturn(List.of(habitacion, habitacionLibre));

        List<Residente> resultado =
                asignacionService.listarResidentesPorEnfermero(1L);

        assertEquals(1, resultado.size());
        assertSame(residente, resultado.get(0));
    }

    // 15. BUSCAR ENFERMERO POR RESIDENTE
    @Test
    void buscarEnfermeroPorResidenteCorrectamente() {

        habitacion.setResidente(residente);
        habitacion.setEnfermero(enfermero);

        when(residenteRepository.existsById(1L))
                .thenReturn(true);

        when(habitacionRepository.findByResidenteId(1L))
                .thenReturn(Optional.of(habitacion));

        Enfermero resultado =
                asignacionService.buscarEnfermeroPorResidente(1L);

        assertSame(enfermero, resultado);
    }

    // 16. BUSCAR ENFERMERO DE RESIDENTE INEXISTENTE
    @Test
    void buscarEnfermeroResidenteInexistente() {

        assertThrows(IllegalArgumentException.class,
                () -> asignacionService.buscarEnfermeroPorResidente(99L));

        verify(habitacionRepository, never())
                .findByResidenteId(anyLong());
    }

    // 17. BUSCAR ENFERMERO DE RESIDENTE SIN HABITACION
    @Test
    void buscarEnfermeroResidenteSinHabitacion() {

        when(residenteRepository.existsById(1L))
                .thenReturn(true);

        when(habitacionRepository.findByResidenteId(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> asignacionService.buscarEnfermeroPorResidente(1L));
    }

    // 18. BUSCAR ENFERMERO DE HABITACION SIN ASIGNACION
    @Test
    void buscarEnfermeroSinAsignacion() {

        habitacion.setResidente(residente);

        when(residenteRepository.existsById(1L))
                .thenReturn(true);

        when(habitacionRepository.findByResidenteId(1L))
                .thenReturn(Optional.of(habitacion));

        assertThrows(IllegalStateException.class,
                () -> asignacionService.buscarEnfermeroPorResidente(1L));
    }
}