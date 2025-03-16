package com.rollerspeed.controllers;

import com.rollerspeed.models.Inscripcion;
import com.rollerspeed.models.Inscripcion.EstadoInscripcion;
import com.rollerspeed.services.ClaseService;
import com.rollerspeed.services.EstudianteService;
import com.rollerspeed.services.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final EstudianteService estudianteService;
    private final ClaseService claseService;

    @Autowired
    public InscripcionController(InscripcionService inscripcionService,
            EstudianteService estudianteService,
            ClaseService claseService) {
        this.inscripcionService = inscripcionService;
        this.estudianteService = estudianteService;
        this.claseService = claseService;
    }

    @GetMapping
    public String listarInscripciones(Model model) {
        model.addAttribute("inscripciones", inscripcionService.obtenerTodasLasInscripciones());
        return "inscripciones/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("inscripcion", new Inscripcion());
        model.addAttribute("estudiantes", estudianteService.obtenerTodosLosEstudiantes());
        model.addAttribute("clases", claseService.obtenerTodasLasClases());
        model.addAttribute("estados", EstadoInscripcion.values());
        return "inscripciones/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarInscripcion(
            @Valid @ModelAttribute Inscripcion inscripcion,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("estudiantes", estudianteService.obtenerTodosLosEstudiantes());
            model.addAttribute("clases", claseService.obtenerTodasLasClases());
            model.addAttribute("estados", EstadoInscripcion.values());
            return "inscripciones/formulario";
        }

        try {
            inscripcionService.guardarInscripcion(inscripcion);
            redirectAttributes.addFlashAttribute("mensaje", "Inscripción guardada exitosamente");
            return "redirect:/inscripciones";
        } catch (Exception e) {
            result.rejectValue("clase", "error.inscripcion", e.getMessage());
            model.addAttribute("estudiantes", estudianteService.obtenerTodosLosEstudiantes());
            model.addAttribute("clases", claseService.obtenerTodasLasClases());
            model.addAttribute("estados", EstadoInscripcion.values());
            return "inscripciones/formulario";
        }
    }

    @PostMapping("/inscribir")
    public String inscribirEstudiante(
            @RequestParam Long estudianteId,
            @RequestParam Long claseId,
            RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.inscribirEstudiante(estudianteId, claseId);
            redirectAttributes.addFlashAttribute("mensaje", "Estudiante inscrito exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clases/" + claseId + "/inscripciones";
    }

    @GetMapping("/{id}")
    public String verDetalleInscripcion(@PathVariable Long id, Model model) {
        inscripcionService.obtenerInscripcionPorId(id)
                .ifPresent(inscripcion -> model.addAttribute("inscripcion", inscripcion));
        return "inscripciones/detalle";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarInscripcion(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.cancelarInscripcion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Inscripción cancelada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones";
    }

    @PostMapping("/{id}/finalizar")
    public String finalizarInscripcion(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.finalizarInscripcion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Inscripción finalizada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones";
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstadoInscripcion(@PathVariable Long id,
            @RequestParam EstadoInscripcion estado,
            RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.actualizarEstadoInscripcion(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado de inscripción actualizado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Inscripcion> inscripcionOpt = inscripcionService.obtenerInscripcionPorId(id);
        if (!inscripcionOpt.isPresent()) {
            return "redirect:/inscripciones";
        }

        model.addAttribute("inscripcion", inscripcionOpt.get());
        model.addAttribute("estudiantes", estudianteService.obtenerTodosLosEstudiantes());
        model.addAttribute("clases", claseService.obtenerTodasLasClases());
        model.addAttribute("estados", EstadoInscripcion.values());
        return "inscripciones/formulario";
    }

    @PostMapping("/editar/{id}")
    public String actualizarInscripcion(
            @PathVariable Long id,
            @Valid @ModelAttribute Inscripcion inscripcion,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("estudiantes", estudianteService.obtenerTodosLosEstudiantes());
            model.addAttribute("clases", claseService.obtenerTodasLasClases());
            model.addAttribute("estados", EstadoInscripcion.values());
            return "inscripciones/formulario";
        }

        try {
            inscripcionService.actualizarInscripcion(id, inscripcion);
            redirectAttributes.addFlashAttribute("mensaje", "Inscripción actualizada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones";
    }
}