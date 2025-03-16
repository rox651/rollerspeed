package com.rollerspeed.services;

import com.rollerspeed.models.Clase;
import com.rollerspeed.models.Instructor;
import com.rollerspeed.models.Inscripcion;
import com.rollerspeed.models.Horario;
import com.rollerspeed.repositories.ClaseRepository;
import com.rollerspeed.repositories.InscripcionRepository;
import com.rollerspeed.repositories.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final InscripcionRepository inscripcionRepository;
    private final HorarioRepository horarioRepository;
    private final HorarioService horarioService;

    @Autowired
    public ClaseService(ClaseRepository claseRepository, InscripcionRepository inscripcionRepository,
            HorarioRepository horarioRepository, HorarioService horarioService) {
        this.claseRepository = claseRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.horarioRepository = horarioRepository;
        this.horarioService = horarioService;
    }

    @Transactional
    public Clase guardarClase(Clase clase) {
        // Validaciones de negocio
        if (clase.getHoraInicio() != null && clase.getHoraFin() != null) {
            if (clase.getHoraInicio().isAfter(clase.getHoraFin())) {
                throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
            }
        }

        if (clase.getId() == null) {
            clase.setActivo(true);
            Clase claseGuardada = claseRepository.save(clase);

            // Generar horarios para los próximos 3 meses
            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaFin = fechaInicio.plusMonths(3);
            horarioService.generarHorariosParaClase(claseGuardada, fechaInicio, fechaFin);

            return claseGuardada;
        }

        return claseRepository.save(clase);
    }

    @Transactional(readOnly = true)
    public List<Clase> obtenerTodasLasClases() {
        return claseRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Clase> obtenerClasePorId(Long id) {
        return claseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Clase> buscarClasesDisponibles() {
        return claseRepository.findClasesDisponibles();
    }

    @Transactional(readOnly = true)
    public boolean verificarDisponibilidadClase(Long claseId) {
        Optional<Clase> claseOpt = claseRepository.findById(claseId);
        if (claseOpt.isPresent()) {
            Clase clase = claseOpt.get();
            Long inscripcionesActivas = inscripcionRepository.countInscripcionesActivasByClaseId(claseId);
            return inscripcionesActivas < clase.getCapacidadMaxima();
        }
        return false;
    }

    @Transactional
    public Clase actualizarClase(Long id, Clase claseActualizada) {
        return claseRepository.findById(id)
                .map(clase -> {
                    clase.setNombre(claseActualizada.getNombre());
                    clase.setDescripcion(claseActualizada.getDescripcion());
                    clase.setDiaSemana(claseActualizada.getDiaSemana());

                    if (claseActualizada.getHoraInicio() != null && claseActualizada.getHoraFin() != null) {
                        if (claseActualizada.getHoraInicio().isAfter(claseActualizada.getHoraFin())) {
                            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
                        }
                        clase.setHoraInicio(claseActualizada.getHoraInicio());
                        clase.setHoraFin(claseActualizada.getHoraFin());
                    }

                    clase.setCapacidadMaxima(claseActualizada.getCapacidadMaxima());
                    clase.setNivel(claseActualizada.getNivel());
                    clase.setInstructor(claseActualizada.getInstructor());

                    return claseRepository.save(clase);
                })
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
    }

    @Transactional
    public void desactivarClase(Long id) {
        claseRepository.findById(id)
                .ifPresent(clase -> {
                    // Primero cancelamos todas las inscripciones relacionadas
                    List<Inscripcion> inscripciones = inscripcionRepository.findByClaseId(id);
                    inscripcionRepository.deleteAll(inscripciones);

                    // Luego eliminamos todos los horarios relacionados
                    List<Horario> horarios = horarioRepository.findByClaseId(id);
                    horarioRepository.deleteAll(horarios);

                    // Finalmente desactivamos la clase
                    clase.setActivo(false);
                    claseRepository.save(clase);
                });
    }
}