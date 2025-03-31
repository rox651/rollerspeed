package com.rollerspeed.controllers;

import com.rollerspeed.models.Horario;
import com.rollerspeed.services.HorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/horarios")
@Tag(name = "Horarios", description = "API para gestionar los horarios de las clases")
public class HorarioController {

    private final HorarioService horarioService;

    @Autowired
    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    @Operation(summary = "Listar horarios", description = "Obtiene una lista de todos los horarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios obtenida exitosamente")
    })
    public String listarHorarios(Model model) {
        model.addAttribute("horarios", horarioService.obtenerTodosLosHorarios());
        return "horarios/lista";
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar horario", description = "Cancela un horario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public String cancelarHorario(
            @Parameter(description = "ID del horario a cancelar") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            horarioService.cancelarHorario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Horario cancelado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    @PostMapping("/{id}/completar")
    @Operation(summary = "Completar horario", description = "Marca un horario como completado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario marcado como completado"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public String completarHorario(
            @Parameter(description = "ID del horario a completar") @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            horarioService.completarHorario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Horario marcado como completado");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }
}