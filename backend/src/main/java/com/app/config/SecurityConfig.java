package com.app.config;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.security.JWTFilter;

import lombok.RequiredArgsConstructor;

@Configuration // Báo cho Spring đây là config
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    @Bean // Tạo object cho Spring quản lý
    public PasswordEncoder passwordEncoder(){ // Mã hóa mật khẩu
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{ // Kiểm soát toàn bộ request

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // ====== PUBLIC ======
                .requestMatchers("/auth/**",
                                            "/swagger-ui/**",
                                            "/v3/api-docs/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .anyRequest().authenticated()
            )
        .formLogin(form -> form.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
