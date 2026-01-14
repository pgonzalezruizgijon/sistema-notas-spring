package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // REDIRECT: Si el usuario entra a http://localhost:8080/ lo mandamos al login
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    // Muestra la página de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // Procesa el formulario de login
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password, 
                        HttpSession session, 
                        Model model) {
        
        Usuario user = usuarioRepository.findByUsername(username);
        
        // Verificamos si el usuario existe y si la contraseña coincide
        if (user != null && password.equals(user.getPassword())) {
            // Guardamos el objeto completo en la sesión con el nombre que espera el otro controlador
            session.setAttribute("usuarioLogueado", user);
            return "redirect:/asignaturas";
        } else {
            // Si falla, volvemos al login con un mensaje de error
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    // Muestra la página de registro
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    // Procesa el formulario de registro
    @PostMapping("/registrar")
    public String registrar(@RequestParam String username, 
                            @RequestParam String password) {
        
        // Evitamos duplicados: solo guardamos si el username no existe
        if (usuarioRepository.findByUsername(username) == null) {
            Usuario nuevoUsuario = new Usuario(username);
            nuevoUsuario.setPassword(password);
            usuarioRepository.save(nuevoUsuario);
        }
        // Tras registrarse con éxito, lo mandamos a que haga login
        return "redirect:/login";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Borra todos los datos de la sesión
        return "redirect:/login";
    }
}