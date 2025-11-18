package com.vpr42.marketplaceauthapi.controller

import com.vpr42.marketplaceauthapi.dto.User
import com.vpr42.marketplaceauthapi.dto.request.AuthenticationRequest
import com.vpr42.marketplaceauthapi.dto.request.RegistrationRequest
import com.vpr42.marketplaceauthapi.dto.response.AuthenticationResponse
import com.vpr42.marketplaceauthapi.exception.AuthenticationException
import com.vpr42.marketplaceauthapi.service.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Аутентификация",
    description = "Основной контроллер аутентификации"
)
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    @PostMapping("/authorization")
    @Operation(summary = "Авторизация пользователя")
    fun authorization(
        @RequestBody request: AuthenticationRequest,
        response: HttpServletResponse,
    ): ResponseEntity<AuthenticationResponse> {
        logger.info("Request to authorization")
        return ResponseEntity.ok(authenticationService.authorization(request, response))
    }

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя")
    fun registration(
        @RequestBody request: RegistrationRequest,
        response: HttpServletResponse,
    ): ResponseEntity<AuthenticationResponse> {
        logger.info("Request to registration")
        return ResponseEntity.ok(authenticationService.registration(request, response))
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход пользователя с сайта")
    fun logout(
        @CookieValue(value = "refreshToken") token: String,
        response: HttpServletResponse,
    ): ResponseEntity<Map<String, String>> {
        logger.info("Request to logout")
        if (token.isEmpty()) throw AuthenticationException("Refresh токен пустой")
        return ResponseEntity.ok(authenticationService.logout(response))
    }

    @GetMapping("/refresh")
    @Operation(summary = "Обновление токена")
    fun refresh(
        @CookieValue(value = "refreshToken") token: String,
        response: HttpServletResponse,
    ): ResponseEntity<AuthenticationResponse> {
        logger.info("Request to refresh")
        return ResponseEntity.ok(authenticationService.refresh(token, response))
    }

    @GetMapping("/who-am-i")
    @Operation(description = "Полная информация об аутентифицированном пользователе")
    fun whoAmI(
        @RequestHeader(value = "Authorization") token: String,
    ): ResponseEntity<User> {
        logger.info("Request to WhoAmI")
        return ResponseEntity.ok().body(authenticationService.whoAmI(token))
    }
}
