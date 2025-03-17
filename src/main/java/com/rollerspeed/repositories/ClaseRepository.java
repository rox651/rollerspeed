package com.rollerspeed.repositories;

import com.rollerspeed.models.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByActivoTrue();

    @Query("SELECT c FROM Clase c WHERE c.activo = true AND c.capacidadMaxima > (SELECT COUNT(i) FROM Inscripcion i WHERE i.clase = c AND i.estado = 'ACTIVA')")
    List<Clase> findClasesDisponibles();

    @Query("UPDATE Inscripcion i SET i.estado = 'CANCELADO' WHERE i.clase.id = :claseId")
    @Modifying
    void cancelarInscripcionesPorClaseId(Long claseId);

    List<Clase> findByInstructorIdAndActivoTrue(Long instructorId);

    List<Clase> findByInstructorId(Long instructorId);
}