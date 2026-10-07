package com.quizapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthFilter jwtAuthFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            /*
             * JWT authentication is stateless.
             * CSRF is therefore disabled for this REST API.
             */
            .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth

                    /*
                     * Registration and login don't require
                     * an existing JWT.
                     */
                    .requestMatchers("/api/auth/**")
                    .permitAll()

                                        /*
                                         * All other requests require a valid JWT.
                                         */
                        .requestMatchers(org.springframework.http.HttpMethod.POST,
                                                "/api/quizzes"
                                        ).hasRole("ADMIN")
                         
                        .requestMatchers(org.springframework.http.HttpMethod.PUT,
                                                "/api/quizzes/**"
                                        ).hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE,
                                                "/api/quizzes/**"
                                        ).hasRole("ADMIN")
                                         
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                                "/api/quizzes/*/publish"
                                        ).hasRole("ADMIN")
                         .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/quizzes/*/questions"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/questions/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/questions/**"
                        ).hasRole("ADMIN")

                        /*
                        * ADMIN ONLY
                        *
                        * Option management.
                        */
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/questions/*/options"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/options/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/options/**"
                        ).hasRole("ADMIN")

                        /*
                        * Everything else requires authentication.
                        */
                        .anyRequest()
                        .authenticated()
                        )

            /*
             * Read and validate JWT before Spring's normal
             * username/password authentication filter.
             */
            .addFilterBefore(
                    jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }
}