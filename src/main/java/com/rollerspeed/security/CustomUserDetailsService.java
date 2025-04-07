package com.rollerspeed.security;

import com.rollerspeed.models.Administrator;
import com.rollerspeed.repositories.AdministratorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private AdministratorRepository administratorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        Administrator administrator = administratorRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("No administrator found for email: {}", email);
                    return new UsernameNotFoundException("Administrador no encontrado con email: " + email);
                });

        logger.debug("Administrator found: {}", administrator.getEmail());
        return administrator;
    }
}