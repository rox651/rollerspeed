package com.rollerspeed.controllers.api;

import com.rollerspeed.models.Clase;
import com.rollerspeed.services.ClaseService;
import com.rollerspeed.services.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clases")
@Tag(name = "Clases API", description = "API REST para gestionar las clases de patinaje")
public class ClaseApiController {

    private final ClaseService claseService;
    private final InstructorService instructorService;

    @Autowired
    public ClaseApiController(ClaseService claseService, InstructorService instructorService) {
        this.claseService = claseService;
        this.instructorService = instructorService;
    }

    @GetMapping
    @Operation(summary = "Listar clases", description = "Obtiene una lista de todas las clases o solo las disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases obtenida exitosamente")
    })
    public ResponseEntity<List<Clase>> listarClases(
            @Parameter(description = "Filtrar solo clases disponibles") @RequestParam(required = false) Boolean soloDisponibles) {
        List<Clase> clases = Boolean.TRUE.equals(soloDisponibles) ? claseService.buscarClasesDisponibles()
                : claseService.obtenerTodasLasClases();
        return ResponseEntity.ok(clases);
    }

    @PostMapping
    @Operation(summary = "Crear clase", description = "Crea una nueva clase en el sistema")
    @RequestBody(description = "Datos de la clase a crear", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "nombre": "Patinaje Básico",
                "descripcion": "Clase de patinaje para principiantes",
                "diaSemana": "LUNES",
                "horaInicio": "09:00",
                "horaFin": "10:30",
                "capacidadMaxima": 15,
                "nivel": "PRINCIPIANTE",
                "instructor": {
                    "id": 1
                },
                "activo": true
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la clase inválidos")
    })
    public ResponseEntity<?> crearClase(
            @org.springframework.web.bind.annotation.RequestBody @Valid Clase clase) {
        try {
            // Validaciones
            if (clase.getDiaSemana() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El día de la semana es obligatorio"));
            }

            if (clase.getHoraInicio() != null && clase.getHoraFin() != null &&
                    clase.getHoraInicio().isAfter(clase.getHoraFin())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La hora de inicio debe ser anterior a la hora de fin"));
            }

            Clase nuevaClase = claseService.guardarClase(clase);
            return ResponseEntity.ok(nuevaClase);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener clase", description = "Obtiene una clase específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<?> obtenerClase(
            @Parameter(description = "ID de la clase a obtener") @PathVariable Long id) {
        return claseService.obtenerClasePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar clase", description = "Actualiza los datos de una clase existente")
    @RequestBody(description = "Datos de la clase a actualizar", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "nombre": "Patinaje Básico",
                "descripcion": "Clase de patinaje para principiantes",
                "diaSemana": "LUNES",
                "horaInicio": "09:00",
                "horaFin": "10:30",
                "capacidadMaxima": 15,
                "nivel": "PRINCIPIANTE",
                "instructor": {
                    "id": 1
                },
                "activo": true
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la clase inválidos"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<?> actualizarClase(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid Clase clase) {
        try {
            Clase claseActualizada = claseService.actualizarClase(id, clase);
            return ResponseEntity.ok(claseActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar clase", description = "Elimina una clase del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<?> eliminarClase(
            @Parameter(description = "ID de la clase a eliminar") @PathVariable Long id) {
        try {
            claseService.eliminarClase(id);
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Clase eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/instructores")
    @Operation(summary = "Listar instructores disponibles", description = "Obtiene una lista de todos los instructores disponibles para asignar a clases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de instructores obtenida exitosamente")
    })
    public ResponseEntity<?> listarInstructores() {
        return ResponseEntity.ok(instructorService.obtenerTodosLosInstructores());
    }

    @GetMapping("/dias-semana")
    @Operation(summary = "Listar días de la semana", description = "Obtiene una lista de todos los días de la semana disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de días obtenida exitosamente")
    })
    public ResponseEntity<?> listarDiasSemana() {
        return ResponseEntity.ok(Clase.DiaSemana.values());
    }
}