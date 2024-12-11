package com.app.interstory.config;

import jdk.jfr.Enabled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@Enabled
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug(" WebSecurityConfig Start !!! ");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/login", "/auth/login","/auth/signup").anonymous()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session  // 추가: 세션 관리 :  동일한 계정으로 다른 기기나 브라우저에서 로그인하면 이전 세션이 만료
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/auth/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/auth/login")
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring().requestMatchers(toH2Console());
    }

}
