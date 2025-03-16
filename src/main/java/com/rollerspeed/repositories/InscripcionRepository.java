package com.rollerspeed.repositories;

import com.rollerspeed.models.Inscripcion;
import com.rollerspeed.models.Inscripcion.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    @Query("SELECT i FROM Inscripcion i WHERE i.estudiante.id = :estudianteId")
    List<Inscripcion> findByEstudianteId(Long estudianteId);

    @Query("SELECT i FROM Inscripcion i WHERE i.clase.id = :claseId")
    List<Inscripcion> findByClaseId(Long claseId);

    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.clase.id = :claseId AND i.estado = 'ACTIVA'")
    Long countInscripcionesActivasByClaseId(Long claseId);

    @Query("SELECT i FROM Inscripcion i WHERE i.estudiante.id = :estudianteId AND i.clase.id = :claseId AND i.estado = 'ACTIVA'")
    List<Inscripcion> findInscripcionActiva(Long estudianteId, Long claseId);

    List<Inscripcion> findByEstudianteIdAndEstado(Long estudianteId, EstadoInscripcion estado);
}