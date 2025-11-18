package com.vpr42.marketplaceauthapi.service

import com.vpr42.marketplaceauthapi.dto.User
import com.vpr42.marketplaceauthapi.dto.request.AuthenticationRequest
import com.vpr42.marketplaceauthapi.dto.request.RegistrationRequest
import com.vpr42.marketplaceauthapi.dto.response.AuthenticationResponse
import com.vpr42.marketplaceauthapi.exception.AuthenticationException
import com.vpr42.marketplaceauthapi.exception.UserNotFoundException
import com.vpr42.marketplaceauthapi.jooq.tables.pojos.Users
import com.vpr42.marketplaceauthapi.jooq.tables.records.UsersRecord
import com.vpr42.marketplaceauthapi.properties.ApplicationProperties
import com.vpr42.marketplaceauthapi.repository.UserRepository
import com.vpr42.marketplaceauthapi.util.toUserDto
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val applicationProperties: ApplicationProperties,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val cityService: CityService,
) {
    private val logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    @Transactional
    fun authorization(request: AuthenticationRequest, response: HttpServletResponse): AuthenticationResponse {
        validateCredentials(request.email, request.password)

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        val userEntity: Users = userRepository.findByEmail(request.email)
            ?: throw UserNotFoundException("Пользователь не найден")

        val (accessToken, refreshToken) = jwtService.generateTokens(userEntity)
        setRefreshToken(response, refreshToken)

        logger.debug("User {} has been authorized", userEntity)
        logger.info("Authorization is successful")

        return AuthenticationResponse(
            message = "Авторизация прошла успешно",
            token = accessToken,
            user = userEntity.toUserDto()
        )
    }

    @Transactional
    fun registration(request: RegistrationRequest, response: HttpServletResponse): AuthenticationResponse {
        validateCredentials(request.email, request.password)
        validateUserInfo(request)

        if (userRepository.findByEmail(request.email) != null) {
            logger.warn("Registration error: User with ${request.email} is success")
            throw AuthenticationException("Пользователь с таким логином уже существует")
        }

        val user = userRepository.insert(request.toUsersRecord())
            ?: throw Exception("Не вышло создать запись в базе данных")

        val (accessToken, refreshToken) = jwtService.generateTokens(user)
        setRefreshToken(response, refreshToken)

        logger.debug("User {} has been register", user.id)
        logger.info("Registration is successful")

        return AuthenticationResponse(
            message = "Пользователь зарегистрирован",
            token = accessToken,
            user = user.toUserDto()
        )
    }

    fun logout(response: HttpServletResponse): Map<String, String> {
        val cookie = Cookie("refreshToken", null)
        cookie.maxAge = 0
        cookie.path = "/"
        response.addCookie(cookie)

        return mapOf("message" to "Выход из аккаунта прошел успешно")
    }

    fun refresh(token: String, response: HttpServletResponse): AuthenticationResponse {
        if (token.isEmpty()) {
            logger.warn("Token is empty")
            throw AuthenticationException("Токен пуст")
        }

        val userEntity = userRepository.findByEmail(jwtService.getLogin(token.substring(7)))
            ?: throw UserNotFoundException("Пользователь не существует")

        val (accessToken, refreshToken) = jwtService.generateTokens(userEntity)

        setRefreshToken(response, refreshToken)

        logger.debug("Token for user {} has been refreshed", userEntity.id)

        return AuthenticationResponse(
            message = "Токены успешно обновлены",
            token = accessToken,
            user = userEntity.toUserDto()
        )
    }

    fun whoAmI(token: String): User {
        val userEntity = userRepository.findByEmail(jwtService.getLogin(token.substring(7)))
            ?: throw UserNotFoundException("Пользователь не существует")
        logger.info("WhoAmI for user ${userEntity.id} successful")
        return userEntity.toUserDto()
    }

    fun setRefreshToken(response: HttpServletResponse, token: String) {
        val cookie = ResponseCookie.from("refreshToken", "Bearer_$token")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofDays(30))
            .sameSite("None")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    private fun validateCredentials(login: String, password: String) {
        if (login.isEmpty() || password.isEmpty())
            throw AuthenticationException("Поля логин и/или пароль пустые")
    }

    private fun validateUserInfo(request: RegistrationRequest) {
        if (request.surname.isEmpty() || request.name.isEmpty() || request.city >= cityService.cities.size) {
            throw AuthenticationException("Обязательная информация о пользователе пуста")
        }
    }

    fun RegistrationRequest.toUsersRecord() = UsersRecord().apply {
        this.id = UUID.randomUUID()
        this.email = this@toUsersRecord.email
        this.password = passwordEncoder.encode(this@toUsersRecord.password)
        this.surname = this@toUsersRecord.surname
        this.name = this@toUsersRecord.name
        this.patronymic = this@toUsersRecord.patronymic
        this.avatarPath = "${applicationProperties.user.defaultAvatarUrl}?username=$surname+$name"
        this.createdAt = LocalDateTime.now()
        this.city = this@toUsersRecord.city
    }
}
