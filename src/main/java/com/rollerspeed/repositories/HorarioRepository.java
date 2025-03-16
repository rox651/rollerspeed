package com.rollerspeed.repositories;

import com.rollerspeed.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    @Query("SELECT h FROM Horario h WHERE h.clase.id = :claseId")
    List<Horario> findByClaseId(Long claseId);
}