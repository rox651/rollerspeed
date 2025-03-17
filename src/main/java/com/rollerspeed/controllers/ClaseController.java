package com.rollerspeed.controllers;

import com.rollerspeed.models.Clase;
import com.rollerspeed.services.ClaseService;
import com.rollerspeed.services.InscripcionService;
import com.rollerspeed.services.InstructorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clases")
public class ClaseController {

    private final ClaseService claseService;
    private final InstructorService instructorService;

    @Autowired
    public ClaseController(ClaseService claseService, InstructorService instructorService,
            InscripcionService inscripcionService) {
        this.claseService = claseService;
        this.instructorService = instructorService;
    }

    @GetMapping
    public String listarClases(@RequestParam(required = false) Boolean soloDisponibles, Model model) {
        if (Boolean.TRUE.equals(soloDisponibles)) {
            model.addAttribute("clases", claseService.buscarClasesDisponibles());
            model.addAttribute("mostrandoDisponibles", true);
        } else {
            model.addAttribute("clases", claseService.obtenerTodasLasClases());
            model.addAttribute("mostrandoDisponibles", false);
        }
        return "clases/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("clase", new Clase());
        model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
        model.addAttribute("diasSemana", Clase.DiaSemana.values());
        return "clases/formulario";
    }

    @PostMapping("/guardar")
    public String guardarClase(@Valid @ModelAttribute Clase clase,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
                model.addAttribute("diasSemana", Clase.DiaSemana.values());
                model.addAttribute("error", "Por favor corrija los errores en el formulario");
                return "clases/formulario";
            }

            // Validación del día de la semana
            if (clase.getDiaSemana() == null) {
                result.rejectValue("diaSemana", "error.clase", "El día de la semana es obligatorio");
                model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
                model.addAttribute("diasSemana", Clase.DiaSemana.values());
                return "clases/formulario";
            }

            // Validación de horario
            if (clase.getHoraInicio() != null && clase.getHoraFin() != null &&
                    clase.getHoraInicio().isAfter(clase.getHoraFin())) {
                result.rejectValue("horaInicio", "error.clase", "La hora de inicio debe ser anterior a la hora de fin");
                model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
                model.addAttribute("diasSemana", Clase.DiaSemana.values());
                return "clases/formulario";
            }

            claseService.guardarClase(clase);
            redirectAttributes.addFlashAttribute("mensaje", "Clase guardada exitosamente");
            return "redirect:/clases";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la clase: " + e.getMessage());
            model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
            model.addAttribute("diasSemana", Clase.DiaSemana.values());
            return "clases/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        claseService.obtenerClasePorId(id)
                .ifPresent(clase -> {
                    model.addAttribute("clase", clase);
                    model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
                    model.addAttribute("diasSemana", Clase.DiaSemana.values());
                });
        return "clases/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarClase(@PathVariable Long id,
            @Valid @ModelAttribute Clase clase,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
            model.addAttribute("diasSemana", Clase.DiaSemana.values());
            return "clases/formulario";
        }

        try {
            claseService.actualizarClase(id, clase);
            redirectAttributes.addFlashAttribute("mensaje", "Clase actualizada exitosamente");
            return "redirect:/clases";
        } catch (RuntimeException e) {
            result.rejectValue("horaInicio", "error.clase", e.getMessage());
            model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
            model.addAttribute("diasSemana", Clase.DiaSemana.values());
            return "clases/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarClase(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        claseService.eliminarClase(id);
        redirectAttributes.addFlashAttribute("mensaje", "Clase eliminada exitosamente");
        return "redirect:/clases";
    }

}