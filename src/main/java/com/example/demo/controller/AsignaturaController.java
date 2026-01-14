package com.example.demo.controller;

import com.example.demo.model.Asignatura;
import com.example.demo.model.Obligatoria;
import com.example.demo.model.Optativa;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AsignaturaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asignaturas")
public class AsignaturaController {

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    // --- LISTAR ---
    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<Asignatura> lista = asignaturaRepository.findByUsuario(usuario);
        model.addAttribute("asignaturas", lista);
        model.addAttribute("promedioGeneral", calcularPromedioGeneral(lista));
        return "listaAsignaturas";
    }
    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre, 
                          @RequestParam String tipo, 
                          @RequestParam String notasString, 
                          HttpSession session) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        // 1. Crear la instancia según el tipo
        Asignatura nueva = tipo.equalsIgnoreCase("obligatoria") 
                           ? new Obligatoria(nombre) 
                           : new Optativa(nombre);

        // 2. Procesar el String de notas (ej: "7.5, 8, 4.3") a List<Double>
        List<Double> listaNotas = new ArrayList<>();
        if (notasString != null && !notasString.trim().isEmpty()) {
            try {
                String[] partes = notasString.split(",");
                for (String p : partes) {
                    listaNotas.add(Double.parseDouble(p.trim()));
                }
            } catch (NumberFormatException e) {
                // Error de formato
                System.out.println("Error convirtiendo notas: " + e.getMessage());
            }
        }

        // 3. Asignar datos y guardar
        nueva.setNotas(listaNotas);
        nueva.setUsuario(usuario);
        asignaturaRepository.save(nueva);

        return "redirect:/asignaturas";
    }

    // --- ORDENAR POR NOMBRE ---
    @GetMapping("/ordenarNombre")
    public String ordenarPorNombre(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<Asignatura> lista = asignaturaRepository.findByUsuario(usuario).stream()
                .sorted(Comparator.comparing(Asignatura::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        model.addAttribute("asignaturas", lista);
        model.addAttribute("promedioGeneral", calcularPromedioGeneral(lista));
        return "listaAsignaturas";
    }

    // --- ORDENAR POR PROMEDIO ---
    @GetMapping("/ordenarPromedio")
    public String ordenarPorPromedio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<Asignatura> lista = asignaturaRepository.findByUsuario(usuario).stream()
                .sorted(Comparator.comparingDouble(Asignatura::obtenerPromedio).reversed())
                .collect(Collectors.toList());

        model.addAttribute("asignaturas", lista);
        model.addAttribute("promedioGeneral", calcularPromedioGeneral(lista));
        return "listaAsignaturas";
    }

    // --- OTROS MÉTODOS ---
    @GetMapping("/nueva")
    public String mostrarFormularioNueva() {
        return "nuevaAsignatura";
    }

    @GetMapping("/detalle")
    public String verDetalle(@RequestParam Long id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        Optional<Asignatura> op = asignaturaRepository.findById(id);
        if (op.isPresent()) {
            model.addAttribute("asignatura", op.get());
            return "detalleAsignatura";
        }
        return "redirect:/asignaturas";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam Long id) {
        asignaturaRepository.deleteById(id);
        return "redirect:/asignaturas";
    }

    @GetMapping("/limpiar")
    public String limpiarTodo(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            List<Asignatura> lista = asignaturaRepository.findByUsuario(usuario);
            asignaturaRepository.deleteAll(lista);
        }
        return "redirect:/asignaturas";
    }

    private double calcularPromedioGeneral(List<Asignatura> lista) {
        if (lista == null || lista.isEmpty()) return 0.0;
        return lista.stream().mapToDouble(Asignatura::obtenerPromedio).average().orElse(0.0);
    }
}