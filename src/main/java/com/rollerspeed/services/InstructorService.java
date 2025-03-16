package com.rollerspeed.services;

import com.rollerspeed.models.Clase;
import com.rollerspeed.models.Instructor;
import com.rollerspeed.repositories.InstructorRepository;
import com.rollerspeed.repositories.ClaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final ClaseRepository claseRepository;
    private final ClaseService claseService;

    public InstructorService(InstructorRepository instructorRepository, ClaseRepository claseRepository,
            ClaseService claseService) {
        this.instructorRepository = instructorRepository;
        this.claseRepository = claseRepository;
        this.claseService = claseService;
    }

    @Transactional
    public Instructor guardarInstructor(Instructor instructor) {
        if (instructor.getEdad() != null) {
            int edad = instructor.getEdad().intValue();
            if (edad < 18) {
                throw new IllegalArgumentException("El instructor debe ser mayor de edad (18 años)");
            }
            if (edad > 70) {
                throw new IllegalArgumentException("La edad no puede ser mayor a 70 años");
            }
        }

        if (instructorRepository.findByEmail(instructor.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un instructor con ese email");
        }

        if (instructor.getId() == null) {
            instructor.setActivo(true);
        }

        return instructorRepository.save(instructor);
    }

    @Transactional(readOnly = true)
    public List<Instructor> obtenerTodosLosInstructores() {
        return instructorRepository.findByActivo(true);
    }

    @Transactional(readOnly = true)
    public Optional<Instructor> obtenerInstructorPorId(Long id) {
        return instructorRepository.findById(id);
    }

    @Transactional
    public Instructor actualizarInstructor(Long id, Instructor instructorActualizado) {
        Instructor instructorExistente = instructorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instructor no encontrado"));

        Optional<Instructor> instructorConEmail = instructorRepository.findByEmail(instructorActualizado.getEmail());
        if (instructorConEmail.isPresent() && !instructorConEmail.get().getId().equals(id)) {
            throw new RuntimeException("Ya existe un instructor con ese email");
        }

        instructorExistente.setNombre(instructorActualizado.getNombre());
        instructorExistente.setEmail(instructorActualizado.getEmail());
        instructorExistente.setTelefono(instructorActualizado.getTelefono());

        if (instructorActualizado.getEdad() != null) {
            int edad = instructorActualizado.getEdad();
            if (edad < 18) {
                throw new IllegalArgumentException("El instructor debe ser mayor de edad (18 años)");
            }
            if (edad > 70) {
                throw new IllegalArgumentException("La edad no puede ser mayor a 70 años");
            }
            instructorExistente.setEdad(edad);
        }

        instructorExistente.setGenero(instructorActualizado.getGenero());
        instructorExistente.setEspecialidad(instructorActualizado.getEspecialidad());

        return instructorRepository.save(instructorExistente);
    }

    @Transactional
    public void desactivarInstructor(Long id) {
        instructorRepository.findById(id)
                .ifPresent(instructor -> {
                    List<Clase> clasesInstructor = claseRepository.findByInstructorIdAndActivoTrue(id);

                    for (Clase clase : clasesInstructor) {
                        claseService.desactivarClase(clase.getId());
                    }

                    instructor.setActivo(false);
                    instructorRepository.save(instructor);
                });
    }
}