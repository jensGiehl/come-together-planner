package de.agiehl.cometogether.service;

import de.agiehl.cometogether.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Value("${app.admin.password}")
    private String adminPassword;

    private String adminPasswordHash;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void init() {
        this.adminPasswordHash = "{bcrypt}" + new BCryptPasswordEncoder().encode(adminPassword);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Check if the entered code matches the raw admin password (case-insensitive).
        if (adminPassword.equalsIgnoreCase(username)) {
            return User.builder()
                    .username("admin") // Set principal name to 'admin'
                    .password(adminPasswordHash) // Use the pre-hashed password
                    .roles("ADMIN", "USER")
                    .build();
        }

        // If not admin, check for a regular user by access code.
        return userRepository.findByAccessCodeword(username)
                .map(appUser -> User.builder()
                        .username(appUser.getAccessCodeword())
                        .password("{noop}" + appUser.getAccessCodeword()) // Password is the code itself
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));
    }
}
