package com.example.demo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPTATIVA")
public class Optativa extends Asignatura {

    // Constructores necesarios
    public Optativa() { 
        super(); 
    }
    
    public Optativa(String nombre) { 
        super(nombre); 
    }

    /**
     * Implementación del método de la interfaz Calificable.
     * Calcula el promedio simple de las notas.
     */
    @Override
    public double obtenerPromedio() {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }
        double suma = 0;
        for (double n : notas) {
            suma += n;
        }
        return suma / notas.size();
    }

    /**
     * Implementación del método de la interfaz Calificable.
     * En este caso, las optativas aprueban con un 4.5.
     */
    @Override
    public boolean estaAprobado() {
        return obtenerPromedio() >= 4.5;
    }
}