package com.rollerspeed.controllers;

import com.rollerspeed.models.Clase;
import com.rollerspeed.models.Inscripcion;
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

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clases")
public class ClaseController {

    private final ClaseService claseService;
    private final InstructorService instructorService;
    private final InscripcionService inscripcionService;

    @Autowired
    public ClaseController(ClaseService claseService, InstructorService instructorService,
            InscripcionService inscripcionService) {
        this.claseService = claseService;
        this.instructorService = instructorService;
        this.inscripcionService = inscripcionService;
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

        // Crear mapa de días traducidos
        Map<DayOfWeek, String> diasSemana = new LinkedHashMap<>();
        diasSemana.put(DayOfWeek.MONDAY, "Lunes");
        diasSemana.put(DayOfWeek.TUESDAY, "Martes");
        diasSemana.put(DayOfWeek.WEDNESDAY, "Miércoles");
        diasSemana.put(DayOfWeek.THURSDAY, "Jueves");
        diasSemana.put(DayOfWeek.FRIDAY, "Viernes");
        diasSemana.put(DayOfWeek.SATURDAY, "Sábado");
        diasSemana.put(DayOfWeek.SUNDAY, "Domingo");

        model.addAttribute("diasSemana", diasSemana);
        return "clases/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarClase(
            @Valid @ModelAttribute Clase clase,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
            model.addAttribute("diasSemana", DayOfWeek.values());
            return "clases/formulario";
        }

        try {
            claseService.guardarClase(clase);
            redirectAttributes.addFlashAttribute("mensaje", "Clase guardada exitosamente");
            return "redirect:/clases";
        } catch (Exception e) {
            result.rejectValue("horaInicio", "error.clase", e.getMessage());
            model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
            model.addAttribute("diasSemana", DayOfWeek.values());
            return "clases/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        claseService.obtenerClasePorId(id)
                .ifPresent(clase -> {
                    model.addAttribute("clase", clase);
                    model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
                    model.addAttribute("diasSemana", DayOfWeek.values());
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
            model.addAttribute("diasSemana", DayOfWeek.values());
            return "clases/formulario";
        }

        try {
            claseService.actualizarClase(id, clase);
            redirectAttributes.addFlashAttribute("mensaje", "Clase actualizada exitosamente");
            return "redirect:/clases";
        } catch (RuntimeException e) {
            result.rejectValue("horaInicio", "error.clase", e.getMessage());
            model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
            model.addAttribute("diasSemana", DayOfWeek.values());
            return "clases/formulario";
        }
    }

    @PostMapping("/desactivar/{id}")
    public String desactivarClase(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        claseService.desactivarClase(id);
        redirectAttributes.addFlashAttribute("mensaje", "Clase desactivada exitosamente");
        return "redirect:/clases";
    }

}