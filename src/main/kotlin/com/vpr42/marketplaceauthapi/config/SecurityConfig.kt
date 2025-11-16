package com.vpr42.marketplaceauthapi.config

import com.vpr42.marketplaceauthapi.exception.UserNotFoundException
import com.vpr42.marketplaceauthapi.repository.UserRepository
import com.vpr42.marketplaceauthapi.service.JwtService
import com.vpr42.marketplaceauthapi.util.toUserAuthDetails
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    @Bean
    fun userDetailsService() = UserDetailsService { login: String ->
        userRepository.findByEmail(login)?.toUserAuthDetails()
            ?: throw UserNotFoundException("with login $login")
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(
        userDetailsService: UserDetailsService,
        passwordEncoder: BCryptPasswordEncoder,
    ): AuthenticationProvider = DaoAuthenticationProvider(userDetailsService).apply {
        setPasswordEncoder(passwordEncoder)
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        userDetailsService: UserDetailsService,
    ): SecurityFilterChain {
        http.csrf { it.disable() }
            .addFilterBefore(
                JwtAuthenticationFilter(userDetailsService, jwtService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .authorizeHttpRequests { authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers(
                        "/api/auth/who-am-i",
                    )
                    .authenticated()
                    .anyRequest()
                    .permitAll()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        return http.build()
    }
}
