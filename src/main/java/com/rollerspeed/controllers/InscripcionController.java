package com.rollerspeed.controllers;

import com.rollerspeed.models.Inscripcion;
import com.rollerspeed.models.Inscripcion.EstadoInscripcion;
import com.rollerspeed.services.ClaseService;
import com.rollerspeed.services.EstudianteService;
import com.rollerspeed.services.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Inscripciones", description = "API para gestionar las inscripciones de estudiantes a clases")
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
    @Operation(summary = "Listar inscripciones", description = "Obtiene una lista de todas las inscripciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida exitosamente")
    })
    public String listarInscripciones(Model model) {
        model.addAttribute("inscripciones", inscripcionService.obtenerTodasLasInscripciones());
        return "inscripciones/lista";
    }

    @GetMapping("/nuevo")
    @Operation(summary = "Mostrar formulario de nueva inscripción", description = "Muestra el formulario para crear una nueva inscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario mostrado exitosamente")
    })
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("inscripcion", new Inscripcion());
        model.addAttribute("estudiantes", estudianteService.obtenerTodosLosEstudiantes());
        model.addAttribute("clases", claseService.obtenerTodasLasClases());
        model.addAttribute("estados", EstadoInscripcion.values());
        return "inscripciones/formulario";
    }

    @PostMapping("/nuevo")
    @Operation(summary = "Guardar nueva inscripción", description = "Guarda una nueva inscripción en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción guardada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la inscripción inválidos")
    })
    public String guardarInscripcion(
            @Parameter(description = "Datos de la inscripción a guardar") @Valid @ModelAttribute Inscripcion inscripcion,
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
    @Operation(summary = "Inscribir estudiante", description = "Inscribe un estudiante en una clase específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estudiante inscrito exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la inscripción"),
            @ApiResponse(responseCode = "404", description = "Estudiante o clase no encontrado")
    })
    public String inscribirEstudiante(
            @Parameter(description = "ID del estudiante a inscribir") @RequestParam Long estudianteId,
            @Parameter(description = "ID de la clase en la que se inscribirá") @RequestParam Long claseId,
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
    @Operation(summary = "Ver detalle de inscripción", description = "Muestra los detalles de una inscripción específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de la inscripción obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public String verDetalleInscripcion(
            @Parameter(description = "ID de la inscripción a consultar") @PathVariable Long id,
            Model model) {
        inscripcionService.obtenerInscripcionPorId(id)
                .ifPresent(inscripcion -> model.addAttribute("inscripcion", inscripcion));
        return "inscripciones/detalle";
    }

    @PostMapping("/cancelar/{id}")
    @Operation(summary = "Cancelar inscripción", description = "Cancela una inscripción específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public String cancelarInscripcion(
            @Parameter(description = "ID de la inscripción a cancelar") @PathVariable Long id,
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
    @Operation(summary = "Finalizar inscripción", description = "Marca una inscripción como finalizada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción finalizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public String finalizarInscripcion(
            @Parameter(description = "ID de la inscripción a finalizar") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            inscripcionService.finalizarInscripcion(id);
            redirectAttributes.addFlashAttribute("mensaje", "Inscripción finalizada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones";
    }

    @GetMapping("/editar/{id}")
    @Operation(summary = "Mostrar formulario de edición", description = "Muestra el formulario para editar una inscripción existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario mostrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public String mostrarFormularioEditar(
            @Parameter(description = "ID de la inscripción a editar") @PathVariable Long id,
            Model model) {
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
    @Operation(summary = "Actualizar inscripción", description = "Actualiza los datos de una inscripción existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la inscripción inválidos"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public String actualizarInscripcion(
            @Parameter(description = "ID de la inscripción a actualizar") @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la inscripción") @Valid @ModelAttribute Inscripcion inscripcion,
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