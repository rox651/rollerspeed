package com.rollerspeed.controllers.api;

import com.rollerspeed.models.Horario;
import com.rollerspeed.services.HorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "Horarios API", description = "API REST para gestionar los horarios de las clases")
public class HorarioApiController {

    private final HorarioService horarioService;

    @Autowired
    public HorarioApiController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    @Operation(summary = "Listar horarios", description = "Obtiene una lista de todos los horarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios obtenida exitosamente")
    })
    public ResponseEntity<List<Horario>> listarHorarios() {
        return ResponseEntity.ok(horarioService.obtenerTodosLosHorarios());
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar horario", description = "Cancela un horario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public ResponseEntity<?> cancelarHorario(
            @Parameter(description = "ID del horario a cancelar") @PathVariable Long id) {
        try {
            horarioService.cancelarHorario(id);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Horario cancelado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/completar")
    @Operation(summary = "Completar horario", description = "Marca un horario como completado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario marcado como completado"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public ResponseEntity<?> completarHorario(
            @Parameter(description = "ID del horario a completar") @PathVariable Long id) {
        try {
            horarioService.completarHorario(id);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Horario marcado como completado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}