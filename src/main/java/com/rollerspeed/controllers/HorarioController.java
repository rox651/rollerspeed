package com.rollerspeed.controllers;

import com.rollerspeed.services.HorarioService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String listarHorarios(Model model) {
        model.addAttribute("horarios", horarioService.obtenerTodosLosHorarios());
        return "horarios/lista";
    }

    @PostMapping("/{id}/cancelar")
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