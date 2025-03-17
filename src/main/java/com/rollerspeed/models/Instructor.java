package com.rollerspeed.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructores")
public class Instructor extends Usuario {

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @OneToMany(mappedBy = "instructor")
    private List<Clase> clases = new ArrayList<>();

    @Column(length = 500)
    private String biografia;

    private boolean disponible = true;

    // Getters y Setters
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<Clase> getClases() {
        return clases;
    }

    public void setClases(List<Clase> clases) {
        this.clases = clases;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}