package com.rollerspeed.controllers.api;

import com.rollerspeed.models.Instructor;
import com.rollerspeed.services.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructores")
@Tag(name = "Instructores API", description = "API REST para gestionar los instructores de la escuela")
public class InstructorApiController {

    private final InstructorService instructorService;

    public InstructorApiController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    @Operation(summary = "Listar instructores", description = "Obtiene una lista de todos los instructores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de instructores obtenida exitosamente")
    })
    public ResponseEntity<List<Instructor>> listarInstructores() {
        return ResponseEntity.ok(instructorService.obtenerTodosLosInstructores());
    }

    @PostMapping
    @Operation(summary = "Crear instructor", description = "Crea un nuevo instructor en el sistema")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del instructor a crear", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "nombre": "Juan Pérez",
                "email": "juan.perez@email.com",
                "telefono": "123456789",
                "edad": 30,
                "genero": "M",
                "especialidad": "Patinaje artístico",
                "biografia": "Instructor con 10 años de experiencia"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del instructor inválidos")
    })
    public ResponseEntity<?> crearInstructor(@Valid @RequestBody Instructor instructor) {
        try {
            Instructor nuevoInstructor = instructorService.guardarInstructor(instructor);
            return ResponseEntity.ok(nuevoInstructor);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener instructor", description = "Obtiene un instructor específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Instructor no encontrado")
    })
    public ResponseEntity<?> obtenerInstructor(@PathVariable Long id) {
        return instructorService.obtenerInstructorPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar instructor", description = "Actualiza los datos de un instructor existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del instructor inválidos"),
            @ApiResponse(responseCode = "404", description = "Instructor no encontrado")
    })
    public ResponseEntity<?> actualizarInstructor(
            @PathVariable Long id,
            @Valid @RequestBody Instructor instructor) {
        try {
            Instructor instructorActualizado = instructorService.actualizarInstructor(id, instructor);
            return ResponseEntity.ok(instructorActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar instructor", description = "Elimina un instructor del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Instructor no encontrado")
    })
    public ResponseEntity<?> eliminarInstructor(@PathVariable Long id) {
        try {
            instructorService.eliminarInstructor(id);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Instructor eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}