package com.example.demo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OBLIGATORIA")
public class Obligatoria extends Asignatura {

    public Obligatoria() {
        super();
    }

    public Obligatoria(String nombre) {
        super(nombre);
    }

    @Override
    public double obtenerPromedio() {
        if (notas == null || notas.isEmpty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }

    @Override
    public boolean estaAprobado() {
        return obtenerPromedio() >= 5.0;
    }
}