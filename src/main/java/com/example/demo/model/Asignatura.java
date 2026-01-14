package com.example.demo.model;

import com.example.demo.model.interfaces.Calificable;
import com.example.demo.model.interfaces.Exportable;
import com.example.demo.model.interfaces.Identificable;
import com.example.demo.model.interfaces.Auditable;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_asignatura")
public abstract class Asignatura implements Calificable, Exportable, Identificable, Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    protected String nombre;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "asignatura_notas", joinColumns = @JoinColumn(name = "asignatura_id"))
    @Column(name = "nota")
    protected List<Double> notas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    protected Usuario usuario;

    // --- CONSTRUCTORES ---
    public Asignatura() {}
    
    public Asignatura(String nombre) { 
        this.nombre = nombre; 
    }

    // --- IMPLEMENTACIÓN DE IDENTIFICABLE ---
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getNombreMostrar() {
        return (nombre != null) ? nombre.toUpperCase() : "SIN NOMBRE";
    }

    // --- IMPLEMENTACIÓN DE AUDITABLE ---
    @Override
    public void reiniciarNotas() {
        if (this.notas != null) {
            this.notas.clear();
        }
    }

    @Override
    public int totalEvaluaciones() {
        return (this.notas != null) ? this.notas.size() : 0;
    }

    // --- IMPLEMENTACIÓN DE EXPORTABLE ---
    @Override
    public String exportarADetalle() {
        return "Asignatura: " + getNombreMostrar() + 
               " | Promedio: " + String.format("%.2f", obtenerPromedio()) + 
               " | Total Notas: " + totalEvaluaciones();
    }

    // --- MÉTODOS DE LA INTERFAZ CALIFICABLE (ABSTRACTOS) ---
    @Override
    public abstract double obtenerPromedio();

    @Override
    public abstract boolean estaAprobado();

    // --- GETTERS Y SETTERS ---
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public List<Double> getNotas() { 
        return notas; 
    }
    
    public void setNotas(List<Double> notas) { 
        this.notas = notas; 
    }

    public Usuario getUsuario() { 
        return usuario; 
    }
    
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }
    
    // Método de utilidad para añadir notas individualmente
    public void agregarNota(double nota) {
        if (this.notas == null) {
            this.notas = new ArrayList<>();
        }
        this.notas.add(nota);
    }
}