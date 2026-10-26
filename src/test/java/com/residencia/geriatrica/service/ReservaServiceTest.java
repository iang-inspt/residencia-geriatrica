package com.residencia.geriatrica.service;

import com.residencia.geriatrica.model.EstadoHabitacion;
import com.residencia.geriatrica.model.Habitacion;
import com.residencia.geriatrica.model.Residente;
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
class ReservaServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private ResidenteRepository residenteRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Habitacion habitacion;
    private Residente residente;

    @BeforeEach
    void setUp() {
        habitacion = new Habitacion();
        residente = new Residente(12345678, "Juan", "Perez");
    }

    // 1. RESERVAR HABITACIÓN CORRECTAMENTE
    @Test
    void reservarHabitacionCorrectamente() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(residenteRepository.findById(1L))
                .thenReturn(Optional.of(residente));

        when(habitacionRepository.save(habitacion))
                .thenReturn(habitacion);

        Habitacion resultado =
                reservaService.reservarHabitacion(1L, 1L);

        assertSame(residente, resultado.getResidente());
        assertEquals(EstadoHabitacion.OCUPADA, resultado.getEstado());

        verify(habitacionRepository).existsByResidenteId(1L);
        verify(habitacionRepository).save(habitacion);
    }

    // 2. RESERVAR HABITACIÓN INEXISTENTE
    @Test
    void reservarHabitacionInexistente() {

        when(habitacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.reservarHabitacion(99L, 1L));

        verify(residenteRepository, never()).findById(anyLong());
        verify(habitacionRepository, never())
                .save(any(Habitacion.class));
    }

    // 3. RESERVAR PARA RESIDENTE INEXISTENTE
    @Test
    void reservarParaResidenteInexistente() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(residenteRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.reservarHabitacion(1L, 99L));

        verify(habitacionRepository, never())
                .save(any(Habitacion.class));
    }

    // 4. IMPEDIR RESERVAR HABITACIÓN OCUPADA
    @Test
    void noReservarHabitacionOcupada() {

        habitacion.setResidente(
                new Residente(23456789, "Maria", "Lopez"));
        habitacion.setEstado(EstadoHabitacion.OCUPADA);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(residenteRepository.findById(1L))
                .thenReturn(Optional.of(residente));

        assertThrows(IllegalStateException.class,
                () -> reservaService.reservarHabitacion(1L, 1L));

        verify(habitacionRepository, never())
                .save(any(Habitacion.class));
    }

    // 5. IMPEDIR QUE UN RESIDENTE OCUPE DOS HABITACIONES
    @Test
    void noReservarSiResidenteYaTieneHabitacion() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        when(residenteRepository.findById(1L))
                .thenReturn(Optional.of(residente));

        when(habitacionRepository.existsByResidenteId(1L))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> reservaService.reservarHabitacion(1L, 1L));

        verify(habitacionRepository, never())
                .save(any(Habitacion.class));
    }

    // 6. CANCELAR RESERVA CORRECTAMENTE
    @Test
    void cancelarReservaCorrectamente() {

        habitacion.setResidente(residente);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        reservaService.cancelarReserva(1L);

        assertNull(habitacion.getResidente());
        assertEquals(EstadoHabitacion.LIBRE, habitacion.getEstado());

        verify(habitacionRepository).save(habitacion);
    }

    // 7. CANCELAR RESERVA DE HABITACIÓN INEXISTENTE
    @Test
    void cancelarReservaHabitacionInexistente() {

        when(habitacionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.cancelarReserva(99L));

        verify(habitacionRepository, never())
                .save(any(Habitacion.class));
    }

    // 8. CANCELAR HABITACIÓN SIN RESERVA
    @Test
    void noCancelarReservaInexistente() {

        when(habitacionRepository.findById(1L))
                .thenReturn(Optional.of(habitacion));

        assertThrows(IllegalStateException.class,
                () -> reservaService.cancelarReserva(1L));

        verify(habitacionRepository, never())
                .save(any(Habitacion.class));
    }

    // 9. BUSCAR HABITACIÓN POR RESIDENTE
    @Test
    void buscarHabitacionPorResidenteCorrectamente() {

        habitacion.setResidente(residente);

        when(habitacionRepository.findByResidenteId(1L))
                .thenReturn(Optional.of(habitacion));

        Habitacion resultado =
                reservaService.buscarHabitacionPorResidente(1L);

        assertSame(habitacion, resultado);
        assertSame(residente, resultado.getResidente());

        verify(habitacionRepository).findByResidenteId(1L);
    }

    // 10. BUSCAR HABITACIÓN DE RESIDENTE SIN RESERVA
    @Test
    void buscarHabitacionDeResidenteSinReserva() {

        when(habitacionRepository.findByResidenteId(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.buscarHabitacionPorResidente(1L));
    }

    // 11. LISTAR RESIDENTES CON HABITACIÓN
    @Test
    void listarResidentesConHabitacionCorrectamente() {

        habitacion.setResidente(residente);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);

        when(habitacionRepository.findByResidenteIsNotNull())
                .thenReturn(List.of(habitacion));

        List<Habitacion> resultado =
                reservaService.listarResidentesConHabitacion();

        assertEquals(1, resultado.size());
        assertSame(habitacion, resultado.get(0));
        assertSame(residente, resultado.get(0).getResidente());

        verify(habitacionRepository).findByResidenteIsNotNull();
    }
}