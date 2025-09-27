package de.agiehl.cometogether.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Re-enabled ADMIN role requirement
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/", "/login", "/error", "/find-survey", "/webjars/**", "/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/find-survey", "/ws/**", "/h2-console/**")) // Removed temporary CSRF ignore for /admin/users
                .headers(headers -> headers.frameOptions().sameOrigin())
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler((request, response, accessDeniedException) -> response.sendRedirect("/login"))
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
