package com.rollerspeed.repositories;

import com.rollerspeed.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UsuarioRepository<T extends Usuario> extends JpaRepository<T, Long> {
    Optional<T> findByEmail(String email);

    List<T> findByActivo(boolean activo);

    boolean existsByEmail(String email);
}