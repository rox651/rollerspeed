package com.rollerspeed.controllers;

import com.rollerspeed.models.Horario;
import com.rollerspeed.services.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/horarios")
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
    public String cancelarHorario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            horarioService.cancelarHorario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Horario cancelado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }

    @PostMapping("/{id}/completar")
    public String completarHorario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            horarioService.completarHorario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Horario marcado como completado");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/horarios";
    }
}