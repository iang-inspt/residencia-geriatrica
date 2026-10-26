package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.Enfermero;
import com.residencia.geriatrica.model.EstadoHabitacion;
import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.model.Residente;
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
class HabitacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private HabitacionService habitacionService;

    private Habitacion habitacion;

    @BeforeEach
    void setUp() {
        habitacion = new Habitacion();
    }

    // 1. AGREGAR HABITACIÓN
    @Test
    void agregarHabitacionCorrectamente() {

        when(habitacionRepository.save(any(Habitacion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Habitacion resultado = habitacionService.agregarHabitacion();

        assertNotNull(resultado);
        assertEquals(EstadoHabitacion.LIBRE, resultado.getEstado());
        assertNull(resultado.getResidente());
        assertNull(resultado.getEnfermero());

        verify(habitacionRepository).save(any(Habitacion.class));
    }

    // 2. LISTAR HABITACIONES
    @Test
    void listarHabitacionesCorrectamente() {

        Habitacion habitacion2 = new Habitacion();

        when(habitacionRepository.findAll())
                .thenReturn(List.of(habitacion, habitacion2));

        List<Habitacion> resultado =
                habitacionService.listarHabitaciones();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(habitacion));
        assertTrue(resultado.contains(habitacion2));

        verify(habitacionRepository).findAll();
    }

    // 3. BUSCAR HABITACIÓN EXISTENTE
    @Test
    void buscarHabitacionExistente() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        Habitacion resultado =
                habitacionService.buscarPorCodigo(1L);

        assertSame(habitacion, resultado);

        verify(habitacionRepository).findById(1L);
    }

    // 4. BUSCAR HABITACIÓN INEXISTENTE
    @Test
    void buscarHabitacionInexistente() {

        when(habitacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> habitacionService.buscarPorCodigo(99L));
    }

    // 5. LISTAR HABITACIONES LIBRES
    @Test
    void listarHabitacionesLibres() {

        when(habitacionRepository.findByEstado(EstadoHabitacion.LIBRE))
                .thenReturn(List.of(habitacion));

        List<Habitacion> resultado =
                habitacionService.listarPorEstado(EstadoHabitacion.LIBRE);

        assertEquals(1, resultado.size());
        assertSame(habitacion, resultado.get(0));

        verify(habitacionRepository)
                .findByEstado(EstadoHabitacion.LIBRE);
    }

    // 6. LISTAR HABITACIONES OCUPADAS
    @Test
    void listarHabitacionesOcupadas() {

        Habitacion ocupada = new Habitacion();
        ocupada.setEstado(EstadoHabitacion.OCUPADA);

        when(habitacionRepository.findByEstado(EstadoHabitacion.OCUPADA))
                .thenReturn(List.of(ocupada));

        List<Habitacion> resultado =
                habitacionService.listarPorEstado(EstadoHabitacion.OCUPADA);

        assertEquals(1, resultado.size());
        assertEquals(EstadoHabitacion.OCUPADA,
                resultado.get(0).getEstado());
    }

    // 7. ELIMINAR HABITACIÓN LIBRE
    @Test
    void eliminarHabitacionCorrectamente() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        habitacionService.eliminarHabitacion(1L);

        verify(habitacionRepository).delete(habitacion);
    }

    // 8. IMPEDIR ELIMINAR HABITACIÓN OCUPADA
    @Test
    void noEliminarHabitacionOcupada() {

        Residente residente =
                new Residente(12345678, "Juan", "Perez");

        habitacion.setResidente(residente);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> habitacionService.eliminarHabitacion(1L)
        );

        assertEquals(
                "No se puede eliminar una habitación ocupada",
                excepcion.getMessage()
        );

        verify(habitacionRepository, never())
                .delete(any(Habitacion.class));
    }

    // 9. IMPEDIR ELIMINAR HABITACIÓN CON ENFERMERO
    @Test
    void noEliminarHabitacionConEnfermeroAsignado() {

        Enfermero enfermero =
                new Enfermero(23456789, "Carlos", "Gomez");

        habitacion.setEnfermero(enfermero);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> habitacionService.eliminarHabitacion(1L)
        );

        assertEquals(
                "No se puede eliminar una habitación con enfermero asignado",
                excepcion.getMessage()
        );

        verify(habitacionRepository, never())
                .delete(any(Habitacion.class));
    }
}