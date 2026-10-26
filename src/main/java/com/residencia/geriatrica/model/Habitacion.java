package com.residencia.geriatrica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "habitaciones")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado = EstadoHabitacion.LIBRE;

    // Relación Residente - Habitación (1:1)
    @OneToOne
    @JoinColumn(name = "residente_id", unique = true)
    private Residente residente;

    // Relación Enfermero - Habitación (N:1)
    @ManyToOne
    @JoinColumn(name = "enfermero_id")
    private Enfermero enfermero;

    public Habitacion() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public Residente getResidente() {
        return residente;
    }

    public void setResidente(Residente residente) {
        this.residente = residente;
    }

    public Enfermero getEnfermero() {
        return enfermero;
    }

    public void setEnfermero(Enfermero enfermero) {
        this.enfermero = enfermero;
    }
}