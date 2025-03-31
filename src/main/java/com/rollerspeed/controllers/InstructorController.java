package com.rollerspeed.controllers;

import com.rollerspeed.models.Instructor;
import com.rollerspeed.services.InstructorService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/instructores")
@Tag(name = "Instructores", description = "API para gestionar los instructores de la escuela")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public String listarInstructores(Model model) {
        model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
        return "instructores/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("instructor", new Instructor());
        return "instructores/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarInstructor(
            @Parameter(description = "Datos del instructor a guardar") @Valid @ModelAttribute Instructor instructor,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("instructor", instructor);
            return "instructores/formulario";
        }

        try {
            instructorService.guardarInstructor(instructor);
            redirectAttributes.addFlashAttribute("mensaje", "Instructor guardado exitosamente");
            return "redirect:/instructores";
        } catch (Exception e) {
            e.printStackTrace();
            result.rejectValue("email", "error.instructor", "Ya existe un instructor con ese email");
            model.addAttribute("instructor", instructor);
            return "instructores/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @Parameter(description = "ID del instructor a editar") @PathVariable Long id,
            Model model) {
        instructorService.obtenerInstructorPorId(id)
                .ifPresent(instructor -> model.addAttribute("instructor", instructor));
        return "instructores/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarInstructor(
            @Parameter(description = "ID del instructor a actualizar") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del instructor") @Valid @ModelAttribute Instructor instructor,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "instructores/formulario";
        }

        try {
            instructorService.actualizarInstructor(id, instructor);
            redirectAttributes.addFlashAttribute("mensaje", "Instructor actualizado exitosamente");
            return "redirect:/instructores";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.instructor", "Correo invalido");
            return "instructores/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarInstructor(
            @Parameter(description = "ID del instructor a eliminar") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        instructorService.eliminarInstructor(id);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor eliminado exitosamente");
        return "redirect:/instructores";
    }
}
