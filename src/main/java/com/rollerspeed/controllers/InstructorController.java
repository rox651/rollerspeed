package com.rollerspeed.controllers;

import com.rollerspeed.models.Instructor;
import com.rollerspeed.services.InstructorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/instructores")
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
            @Valid @ModelAttribute Instructor instructor,
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
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        instructorService.obtenerInstructorPorId(id)
                .ifPresent(instructor -> model.addAttribute("instructor", instructor));
        return "instructores/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarInstructor(@PathVariable Long id,
            @Valid @ModelAttribute Instructor instructor,
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
    public String eliminarInstructor(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        instructorService.eliminarInstructor(id);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor eliminado exitosamente");
        return "redirect:/instructores";
    }
}
