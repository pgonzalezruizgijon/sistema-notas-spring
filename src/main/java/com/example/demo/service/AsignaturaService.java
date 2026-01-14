package com.example.demo.service;

import com.example.demo.model.Asignatura;
import com.example.demo.repository.AsignaturaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepo;

    // Inyectamos el repositorio que conecta con MySQL en Docker
    public AsignaturaService(AsignaturaRepository asignaturaRepo) {
        this.asignaturaRepo = asignaturaRepo;
    }

    public List<Asignatura> listar() {
        return asignaturaRepo.findAll();
    }

    public void agregar(Asignatura a) {
        asignaturaRepo.save(a);
    }

    // SOLUCIÓN ERROR: buscarPorId
    public Optional<Asignatura> buscarPorId(Long id) {
        return asignaturaRepo.findById(id);
    }

    // SOLUCIÓN ERROR: eliminarPorId
    public void eliminarPorId(Long id) {
        asignaturaRepo.deleteById(id);
    }

    public double promedioGeneral() {
        List<Asignatura> todas = asignaturaRepo.findAll();
        if (todas.isEmpty()) return 0.0;
        return todas.stream()
                .mapToDouble(Asignatura::obtenerPromedio)
                .average()
                .orElse(0.0);
    }

    // SOLUCIÓN ERROR: ordenarPorNombre (usando Spring Data)
    public List<Asignatura> ordenarPorNombre() {
        return asignaturaRepo.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }

    // SOLUCIÓN ERROR: ordenarPorPromedioDesc
    // Nota: Como el promedio es un método calculado y no una columna en la BD, 
    // lo ordenamos manualmente tras traerlos de la BD.
    public List<Asignatura> ordenarPorPromedioDesc() {
        List<Asignatura> lista = asignaturaRepo.findAll();
        lista.sort((a, b) -> Double.compare(b.obtenerPromedio(), a.obtenerPromedio()));
        return lista;
    }

    public void limpiarTodas() {
        asignaturaRepo.deleteAll();
    }
}