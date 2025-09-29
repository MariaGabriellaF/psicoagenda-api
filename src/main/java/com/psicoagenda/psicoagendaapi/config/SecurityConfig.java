package com.psicoagenda.psicoagendaapi.config;

import com.psicoagenda.psicoagendaapi.models.UserRole;
import com.psicoagenda.psicoagendaapi.security.JwtAuthenticationFilter;
import com.psicoagenda.psicoagendaapi.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita o uso de @PreAuthorize nos Controllers
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // Injeção via construtor
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // Configura o BCryptPasswordEncoder como um Bean
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura o AuthenticationManager para processar o login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configura o provedor de autenticação (usa o UserDetailsService e PasswordEncoder)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    // Define as regras de acesso da API
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas (permitAll)
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/psicologos").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pacientes").permitAll()

                        // Regras de listagem (PSICOLOGO e PACIENTE)
                        .requestMatchers(HttpMethod.GET, "/psicologos/**").permitAll() // Todos podem listar/ver perfis de psicólogos
                        .requestMatchers(HttpMethod.GET, "/disponibilidades/**").permitAll() // Todos podem listar disponibilidades
                        .requestMatchers(HttpMethod.GET, "/pacientes").hasRole(UserRole.PSICOLOGO.name()) // Apenas Psicólogo lista pacientes

                        // Rotas de Agendamento liberadas para ambos, com controle fino no Controller
                        .requestMatchers("/agendamentos/**").hasAnyRole(UserRole.PSICOLOGO.name(), UserRole.PACIENTE.name())

                        // Rotas de manipulação (DELETE/PATCH) de Pacientes (controle fino no Controller)
                        .requestMatchers("/pacientes/**").hasAnyRole(UserRole.PSICOLOGO.name(), UserRole.PACIENTE.name())

                        // Rotas de Disponibilidade (controle total para Psicólogo, já que GET foi liberado acima)
                        .requestMatchers("/disponibilidades/**").hasRole(UserRole.PSICOLOGO.name())

                        // O restante exige autenticação
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}