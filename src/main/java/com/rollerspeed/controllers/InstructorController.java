package com.rollerspeed.controllers;

import com.rollerspeed.models.Instructor;
import com.rollerspeed.services.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Listar instructores", description = "Obtiene una lista de todos los instructores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de instructores obtenida exitosamente")
    })
    public String listarInstructores(Model model) {
        model.addAttribute("instructores", instructorService.obtenerTodosLosInstructores());
        return "instructores/lista";
    }

    @GetMapping("/nuevo")
    @Operation(summary = "Mostrar formulario de nuevo instructor", description = "Muestra el formulario para crear un nuevo instructor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario mostrado exitosamente")
    })
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("instructor", new Instructor());
        return "instructores/formulario";
    }

    @PostMapping("/nuevo")
    @Operation(summary = "Guardar nuevo instructor", description = "Guarda un nuevo instructor en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor guardado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del instructor inválidos")
    })
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
    @Operation(summary = "Mostrar formulario de edición", description = "Muestra el formulario para editar un instructor existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formulario mostrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Instructor no encontrado")
    })
    public String mostrarFormularioEditar(
            @Parameter(description = "ID del instructor a editar") @PathVariable Long id,
            Model model) {
        instructorService.obtenerInstructorPorId(id)
                .ifPresent(instructor -> model.addAttribute("instructor", instructor));
        return "instructores/formulario";
    }

    @PostMapping("/editar/{id}")
    @Operation(summary = "Actualizar instructor", description = "Actualiza los datos de un instructor existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del instructor inválidos"),
            @ApiResponse(responseCode = "404", description = "Instructor no encontrado")
    })
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
    @Operation(summary = "Eliminar instructor", description = "Elimina un instructor del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Instructor no encontrado")
    })
    public String eliminarInstructor(
            @Parameter(description = "ID del instructor a eliminar") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        instructorService.eliminarInstructor(id);
        redirectAttributes.addFlashAttribute("mensaje", "Instructor eliminado exitosamente");
        return "redirect:/instructores";
    }
}
