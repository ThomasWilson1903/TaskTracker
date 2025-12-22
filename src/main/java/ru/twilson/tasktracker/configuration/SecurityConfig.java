package ru.twilson.tasktracker.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccessTokenFilter accessTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)  // Отключаем Basic Auth
                .formLogin(AbstractHttpConfigurer::disable)  // Отключаем форму логина
                .logout(AbstractHttpConfigurer::disable)           // Отключаем logout
                .rememberMe(AbstractHttpConfigurer::disable) // Отключаем remember me
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/public/**",
                                "/api/public/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
