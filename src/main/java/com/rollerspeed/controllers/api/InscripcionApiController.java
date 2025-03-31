package com.rollerspeed.controllers.api;

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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones API", description = "API REST para gestionar las inscripciones de estudiantes a clases")
public class InscripcionApiController {

    private final InscripcionService inscripcionService;
    private final EstudianteService estudianteService;
    private final ClaseService claseService;

    @Autowired
    public InscripcionApiController(InscripcionService inscripcionService,
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
    public ResponseEntity<List<Inscripcion>> listarInscripciones() {
        return ResponseEntity.ok(inscripcionService.obtenerTodasLasInscripciones());
    }

    @PostMapping
    @Operation(summary = "Crear inscripción", description = "Crea una nueva inscripción en el sistema")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la inscripción a crear", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "estudiante": {"id": 1},
                "clase": {"id": 1},
                "estado": "ACTIVA"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la inscripción inválidos")
    })
    public ResponseEntity<?> crearInscripcion(
            @Valid @RequestBody Inscripcion inscripcion) {
        try {
            Inscripcion nuevaInscripcion = inscripcionService.guardarInscripcion(inscripcion);
            return ResponseEntity.ok(nuevaInscripcion);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/inscribir")
    @Operation(summary = "Inscribir estudiante", description = "Inscribe un estudiante en una clase específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estudiante inscrito exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la inscripción")
    })
    public ResponseEntity<?> inscribirEstudiante(
            @Parameter(description = "ID del estudiante") @RequestParam Long estudianteId,
            @Parameter(description = "ID de la clase") @RequestParam Long claseId) {
        try {
            inscripcionService.inscribirEstudiante(estudianteId, claseId);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Estudiante inscrito exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inscripción", description = "Obtiene una inscripción específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<?> obtenerInscripcion(@PathVariable Long id) {
        return inscripcionService.obtenerInscripcionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar inscripción", description = "Cancela una inscripción específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<?> cancelarInscripcion(@PathVariable Long id) {
        try {
            inscripcionService.cancelarInscripcion(id);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Inscripción cancelada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar inscripción", description = "Marca una inscripción como finalizada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción finalizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<?> finalizarInscripcion(@PathVariable Long id) {
        try {
            inscripcionService.finalizarInscripcion(id);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Inscripción finalizada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/estados")
    @Operation(summary = "Listar estados", description = "Obtiene una lista de todos los estados de inscripción posibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente")
    })
    public ResponseEntity<?> listarEstados() {
        return ResponseEntity.ok(EstadoInscripcion.values());
    }
}