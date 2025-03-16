package com.rollerspeed.services;

import com.rollerspeed.models.Clase;
import com.rollerspeed.models.Horario;
import com.rollerspeed.models.Clase.DiaSemana;
import com.rollerspeed.models.Horario.EstadoHorario;
import com.rollerspeed.repositories.HorarioRepository;
import com.rollerspeed.utils.DiaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    @Autowired
    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    @Transactional
    public List<Horario> generarHorariosParaClase(Clase clase, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Horario> horarios = new ArrayList<>();
        LocalDate fechaActual = fechaInicio;

        while (!fechaActual.isAfter(fechaFin)) {
            DiaSemana diaSemanaActual = DiaUtils.convertirDayOfWeekADiaSemana(fechaActual.getDayOfWeek());

            if (diaSemanaActual == clase.getDiaSemana()) {
                Horario horario = new Horario();
                horario.setClase(clase);
                horario.setFecha(fechaActual);
                horario.setHoraInicio(clase.getHoraInicio());
                horario.setHoraFin(clase.getHoraFin());
                horario.setEstado(EstadoHorario.PROGRAMADO);
                horarios.add(horarioRepository.save(horario));
            }
            fechaActual = fechaActual.plusDays(1);
        }

        return horarios;
    }

    @Transactional(readOnly = true)
    public List<Horario> obtenerTodosLosHorarios() {
        return horarioRepository.findAll();
    }

    @Transactional
    public void cancelarHorario(Long id) {
        actualizarEstadoHorario(id, EstadoHorario.CANCELADO);
    }

    @Transactional
    public void completarHorario(Long id) {
        actualizarEstadoHorario(id, EstadoHorario.COMPLETADO);
    }

    private void actualizarEstadoHorario(Long id, EstadoHorario nuevoEstado) {
        horarioRepository.findById(id)
                .ifPresent(horario -> {
                    horario.setEstado(nuevoEstado);
                    horarioRepository.save(horario);
                });
    }
}