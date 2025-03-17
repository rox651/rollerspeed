package com.rollerspeed.services;

import com.rollerspeed.models.Estudiante;
import com.rollerspeed.models.Inscripcion;
import com.rollerspeed.repositories.EstudianteRepository;
import com.rollerspeed.repositories.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final InscripcionService inscripcionService;

    @Autowired
    public EstudianteService(EstudianteRepository estudianteRepository, InscripcionRepository inscripcionRepository,
            InscripcionService inscripcionService) {
        this.estudianteRepository = estudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.inscripcionService = inscripcionService;
    }

    @Transactional
    public Estudiante guardarEstudiante(Estudiante estudiante) {
        if (estudiante.getEdad() != null) {
            int edad = estudiante.getEdad();
            if (edad < 5) {
                throw new IllegalArgumentException("La edad mínima para inscribirse es 5 años");
            }
        }

        if (estudianteRepository.findByEmail(estudiante.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un estudiante con ese email");
        }

        if (estudiante.getId() == null) {
            estudiante.setActivo(true);
        }

        return estudianteRepository.save(estudiante);
    }

    @Transactional(readOnly = true)
    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteRepository.findByActivo(true);
    }

    @Transactional(readOnly = true)
    public Optional<Estudiante> obtenerEstudiantePorId(Long id) {
        return estudianteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Estudiante> buscarEstudiantePorEmail(String email) {
        return estudianteRepository.findByEmail(email);
    }

    @Transactional
    public Estudiante actualizarEstudiante(Long id, Estudiante estudianteActualizado) {
        Estudiante estudianteExistente = obtenerEstudiantePorId(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el estudiante"));

        Optional<Estudiante> estudianteConEmail = estudianteRepository.findByEmail(estudianteActualizado.getEmail());
        if (estudianteConEmail.isPresent() && !estudianteConEmail.get().getId().equals(id)) {
            throw new RuntimeException("Ya existe un estudiante con ese email");
        }

        estudianteExistente.setNombre(estudianteActualizado.getNombre());
        estudianteExistente.setEmail(estudianteActualizado.getEmail());
        estudianteExistente.setTelefono(estudianteActualizado.getTelefono());

        if (estudianteActualizado.getEdad() != null) {
            int edad = estudianteActualizado.getEdad();
            if (edad < 5) {
                throw new IllegalArgumentException("La edad mínima para inscribirse es 5 años");
            }
            estudianteExistente.setEdad(edad);
        }

        estudianteExistente.setGenero(estudianteActualizado.getGenero());
        estudianteExistente.setNivel(estudianteActualizado.getNivel());

        return estudianteRepository.save(estudianteExistente);
    }

    @Transactional
    public void eliminarEstudiante(Long id) {
        estudianteRepository.findById(id)
                .ifPresent(estudiante -> {
                    List<Inscripcion> inscripciones = inscripcionRepository.findByEstudianteId(id);
                    inscripcionRepository.deleteAll(inscripciones);

                    estudianteRepository.delete(estudiante);
                });
    }
}