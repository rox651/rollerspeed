package com.rollerspeed.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotNull(message = "La edad es obligatoria")
    private Integer edad;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    private boolean activo = true;

    public enum Genero {
        M("Masculino"),
        F("Femenino"),
        O("Otro");

        private final String descripcion;

        Genero(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}