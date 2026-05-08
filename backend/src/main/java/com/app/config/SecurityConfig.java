package com.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.security.JWTFilter;

@Configuration // Báo cho Spring đây là config
public class SecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

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
                // auth
                .requestMatchers("/users/register").permitAll()
                .requestMatchers("/users/login").permitAll()

                // auction
                .requestMatchers(HttpMethod.GET, "/auctions/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/auctions").hasRole("SELLER")

                // bid 
                .requestMatchers("/bids").authenticated()

                // become-seller
                .requestMatchers("/users/become-seller").authenticated()

                // seller API
                .requestMatchers("/seller/**").hasRole("SELLER")

                // còn lại
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
