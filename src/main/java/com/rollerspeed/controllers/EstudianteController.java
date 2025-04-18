package com.rollerspeed.controllers;

import com.rollerspeed.models.Estudiante;
import com.rollerspeed.models.Usuario;
import com.rollerspeed.services.EstudianteService;
import com.rollerspeed.services.InscripcionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estudiantes")
@Tag(name = "Estudiantes", description = "API para gestionar los estudiantes de la escuela")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @Autowired
    public EstudianteController(EstudianteService estudianteService, InscripcionService inscripcionService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public String listarEstudiantes(Model model) {
        model.addAttribute("estudiantes", estudianteService.obtenerTodosLosEstudiantes());
        return "estudiantes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("generos", Usuario.Genero.values());
        return "estudiantes/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarEstudiante(
            @Parameter(description = "Datos del estudiante a guardar") @Valid @ModelAttribute Estudiante estudiante,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("generos", Usuario.Genero.values());
            return "estudiantes/formulario";
        }

        try {
            estudianteService.guardarEstudiante(estudiante);
            redirectAttributes.addFlashAttribute("mensaje", "Estudiante guardado exitosamente");
            return "redirect:/estudiantes";
        } catch (Exception e) {
            e.printStackTrace();
            result.rejectValue("email", "error.estudiante", "Ya existe un estudiante con este correo electrónico");
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("generos", Usuario.Genero.values());
            return "estudiantes/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @Parameter(description = "ID del estudiante a editar") @PathVariable Long id,
            Model model) {
        estudianteService.obtenerEstudiantePorId(id)
                .ifPresent(estudiante -> {
                    model.addAttribute("estudiante", estudiante);
                    model.addAttribute("generos", Usuario.Genero.values());
                });
        return "estudiantes/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarEstudiante(
            @Parameter(description = "ID del estudiante a actualizar") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del estudiante") @Valid @ModelAttribute Estudiante estudiante,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("generos", Usuario.Genero.values());
            return "estudiantes/formulario";
        }

        try {
            estudianteService.actualizarEstudiante(id, estudiante);
            redirectAttributes.addFlashAttribute("mensaje", "Estudiante actualizado exitosamente");
            return "redirect:/estudiantes";
        } catch (Exception e) {
            e.printStackTrace();
            result.rejectValue("email", "error.estudiante", "Ya existe un estudiante con este correo electrónico");
            model.addAttribute("estudiante", estudiante);
            model.addAttribute("generos", Usuario.Genero.values());
            return "estudiantes/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarEstudiante(
            @Parameter(description = "ID del estudiante a eliminar") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        estudianteService.eliminarEstudiante(id);
        redirectAttributes.addFlashAttribute("mensaje", "Estudiante eliminado exitosamente");
        return "redirect:/estudiantes";
    }

}