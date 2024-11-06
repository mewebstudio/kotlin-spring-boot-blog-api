package com.mewebstudio.blogapi.config

import com.mewebstudio.blogapi.security.CustomAuthenticationFilter
import com.mewebstudio.blogapi.security.JwtAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class WebSecurityConfig(
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val customAuthenticationFilter: CustomAuthenticationFilter
) {
    /**
     * Configure CORS.
     *
     * @return CorsConfigurationSource
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin("*")
        configuration.addAllowedHeader("*")
        configuration.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { h ->
                h.frameOptions { it.sameOrigin() }
                    .xssProtection { it.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK) }
                    .contentSecurityPolicy {
                        it.policyDirectives(
                            "default-src 'self'; script-src 'self'; img-src 'self' data:; style-src 'self'"
                        )
                    }
            }
            .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/",
                        "/assets/**",
                        "/actuator/**",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/auth/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }

        return http.build()
    }
}
