package com.rollerspeed.security;

import com.rollerspeed.models.Administrator;
import com.rollerspeed.repositories.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Administrator administrator = administratorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado con email: " + email));

        return administrator;
    }
}