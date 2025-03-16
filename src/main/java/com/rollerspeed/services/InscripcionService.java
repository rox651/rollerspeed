package com.rollerspeed.services;

import com.rollerspeed.models.Clase;
import com.rollerspeed.models.Estudiante;
import com.rollerspeed.models.Inscripcion;
import com.rollerspeed.models.Inscripcion.EstadoInscripcion;
import com.rollerspeed.repositories.ClaseRepository;
import com.rollerspeed.repositories.EstudianteRepository;
import com.rollerspeed.repositories.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final ClaseRepository claseRepository;
    private final ClaseService claseService;

    @Autowired
    public InscripcionService(
            InscripcionRepository inscripcionRepository,
            EstudianteRepository estudianteRepository,
            ClaseRepository claseRepository,
            ClaseService claseService) {
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.claseRepository = claseRepository;
        this.claseService = claseService;
    }

    @Transactional
    public Inscripcion inscribirEstudiante(Long estudianteId, Long claseId) {
        // Verificar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Verificar que la clase existe
        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        // Verificar que la clase tiene cupo disponible
        if (!claseService.verificarDisponibilidadClase(claseId)) {
            throw new RuntimeException("La clase no tiene cupos disponibles");
        }

        // Verificar que el estudiante no esté ya inscrito en esta clase
        List<Inscripcion> inscripcionesActivas = inscripcionRepository.findInscripcionActiva(estudianteId, claseId);
        if (!inscripcionesActivas.isEmpty()) {
            throw new RuntimeException("El estudiante ya está inscrito en esta clase");
        }

        // Crear la inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setClase(clase);
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstado(EstadoInscripcion.ACTIVA);

        return inscripcionRepository.save(inscripcion);
    }

    @Transactional
    public Inscripcion guardarInscripcion(Inscripcion inscripcion) {
        // Verificar que la clase tiene cupo disponible si es una nueva inscripción
        if (inscripcion.getId() == null && inscripcion.getClase() != null) {
            if (!claseService.verificarDisponibilidadClase(inscripcion.getClase().getId())) {
                throw new RuntimeException("La clase no tiene cupos disponibles");
            }
        }

        if (inscripcion.getFechaInscripcion() == null) {
            inscripcion.setFechaInscripcion(LocalDate.now());
        }

        if (inscripcion.getEstado() == null) {
            inscripcion.setEstado(EstadoInscripcion.ACTIVA);
        }

        return inscripcionRepository.save(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<Inscripcion> obtenerTodasLasInscripciones() {
        return inscripcionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Inscripcion> obtenerInscripcionPorId(Long id) {
        return inscripcionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Inscripcion> buscarInscripcionesPorEstudianteId(Long estudianteId) {
        return inscripcionRepository.findByEstudianteId(estudianteId);
    }

    @Transactional(readOnly = true)
    public List<Inscripcion> buscarInscripcionesPorClaseId(Long claseId) {
        return inscripcionRepository.findByClaseId(claseId);
    }

    @Transactional
    public Inscripcion actualizarEstadoInscripcion(Long id, EstadoInscripcion nuevoEstado) {
        return inscripcionRepository.findById(id)
                .map(inscripcion -> {
                    inscripcion.setEstado(nuevoEstado);

                    if (nuevoEstado == EstadoInscripcion.FINALIZADA || nuevoEstado == EstadoInscripcion.CANCELADA) {
                        inscripcion.setFechaFinalizacion(LocalDate.now());
                    }

                    return inscripcionRepository.save(inscripcion);
                })
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
    }

    @Transactional
    public void cancelarInscripcion(Long id) {
        inscripcionRepository.findById(id)
                .ifPresent(inscripcion -> {
                    // Solo cancelamos si la inscripción está activa
                    if (inscripcion.getEstado() == EstadoInscripcion.ACTIVA) {
                        inscripcion.setEstado(EstadoInscripcion.CANCELADA);
                        inscripcion.setFechaFinalizacion(LocalDate.now());
                        inscripcionRepository.save(inscripcion);

                        // El cupo se libera automáticamente ya que la consulta de cupos
                        // disponibles solo considera inscripciones ACTIVAS
                    }
                });
    }

    @Transactional
    public void finalizarInscripcion(Long id) {
        actualizarEstadoInscripcion(id, EstadoInscripcion.FINALIZADA);
    }

    @Transactional
    public Inscripcion actualizarInscripcion(Long id, Inscripcion inscripcionActualizada) {
        return inscripcionRepository.findById(id)
                .map(inscripcion -> {
                    // Actualizar solo el estado
                    inscripcion.setEstado(inscripcionActualizada.getEstado());

                    // Si el estado cambia a FINALIZADA o CANCELADA, actualizar la fecha de
                    // finalización
                    if (inscripcionActualizada.getEstado() == EstadoInscripcion.FINALIZADA ||
                            inscripcionActualizada.getEstado() == EstadoInscripcion.CANCELADA) {
                        inscripcion.setFechaFinalizacion(LocalDate.now());
                    } else {
                        inscripcion.setFechaFinalizacion(null); // Si vuelve a ACTIVA, eliminar fecha de finalización
                    }

                    return inscripcionRepository.save(inscripcion);
                })
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
    }

}