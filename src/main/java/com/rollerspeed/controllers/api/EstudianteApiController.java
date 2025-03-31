package com.rollerspeed.controllers.api;

import com.rollerspeed.models.Estudiante;
import com.rollerspeed.models.Usuario;
import com.rollerspeed.services.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes API", description = "API REST para gestionar los estudiantes de la escuela")
public class EstudianteApiController {

    private final EstudianteService estudianteService;

    @Autowired
    public EstudianteApiController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    @Operation(summary = "Listar estudiantes", description = "Obtiene una lista de todos los estudiantes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida exitosamente")
    })
    public ResponseEntity<List<Estudiante>> listarEstudiantes() {
        return ResponseEntity.ok(estudianteService.obtenerTodosLosEstudiantes());
    }

    @PostMapping
    @Operation(summary = "Crear estudiante", description = "Crea un nuevo estudiante en el sistema")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del estudiante a crear", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Ejemplo de estudiante", value = """
            {
                "nombre": "Juan Pérez",
                "email": "juan.perez@email.com",
                "telefono": "123456789",
                "edad": 15,
                "genero": "M",
                "nivel": "PRINCIPIANTE",
                "fechaInicio": "2025-03-31"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estudiante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del estudiante inválidos")
    })
    public ResponseEntity<?> crearEstudiante(
            @Parameter(description = "Datos del estudiante a crear") @Valid @RequestBody Estudiante estudiante) {
        try {
            Estudiante nuevoEstudiante = estudianteService.guardarEstudiante(estudiante);
            return ResponseEntity.ok(nuevoEstudiante);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener estudiante", description = "Obtiene un estudiante específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<?> obtenerEstudiante(
            @Parameter(description = "ID del estudiante a obtener") @PathVariable Long id) {
        return estudianteService.obtenerEstudiantePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estudiante", description = "Actualiza los datos de un estudiante existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estudiante actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del estudiante inválidos"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<?> actualizarEstudiante(
            @Parameter(description = "ID del estudiante a actualizar") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del estudiante") @Valid @RequestBody Estudiante estudiante) {
        try {
            Estudiante estudianteActualizado = estudianteService.actualizarEstudiante(id, estudiante);
            return ResponseEntity.ok(estudianteActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estudiante", description = "Elimina un estudiante del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estudiante eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<?> eliminarEstudiante(
            @Parameter(description = "ID del estudiante a eliminar") @PathVariable Long id) {
        try {
            estudianteService.eliminarEstudiante(id);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Estudiante eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/generos")
    @Operation(summary = "Listar géneros", description = "Obtiene una lista de todos los géneros disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de géneros obtenida exitosamente")
    })
    public ResponseEntity<?> listarGeneros() {
        return ResponseEntity.ok(Usuario.Genero.values());
    }
}