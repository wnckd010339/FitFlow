package com.acorn.gymmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final SessionAuthenticationFilter sessionAuthenticationFilter;
    private final SessionAuthenticationEntryPoint authenticationEntryPoint;
    private final SessionAccessDeniedHandler accessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception{
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/login",
                                "/error",
                                "/error/**",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/assets/**"
                        ).permitAll()

                        .requestMatchers("/logout")
                        .authenticated()

                        .requestMatchers("/admin/**")
                        .hasRole(SessionUser.ROLE_ADMIN)

                        .requestMatchers("/trainer/**")
                        .hasRole(SessionUser.ROLE_TRAINER)

                        .requestMatchers("/member/**")
                        .hasRole(SessionUser.ROLE_MEMBER)

                        .requestMatchers("/api/members/**")
                        .hasRole(SessionUser.ROLE_ADMIN)

                        .requestMatchers("/api/membership-products/**")
                        .hasRole(SessionUser.ROLE_ADMIN)

                        .requestMatchers("/api/payments/**")
                        .hasRole(SessionUser.ROLE_ADMIN)

                        .requestMatchers("/api/**")
                        .authenticated()

                        .anyRequest()
                        .denyAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                        .accessDeniedHandler(
                                accessDeniedHandler
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )
                .requestCache(cache -> cache.disable())
                .addFilterBefore(
                        sessionAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    FilterRegistrationBean<SessionAuthenticationFilter> disableAutomaticFilterRegistration(
            SessionAuthenticationFilter filter
    ) {
        FilterRegistrationBean<SessionAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
