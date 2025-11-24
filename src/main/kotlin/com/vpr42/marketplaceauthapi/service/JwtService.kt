package com.vpr42.marketplaceauthapi.service

import com.vpr42.marketplaceauthapi.jooq.tables.pojos.Users
import com.vpr42.marketplaceauthapi.properties.ApplicationProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val applicationProperties: ApplicationProperties,
) {
    private val signKey = Keys.hmacShaKeyFor(
        Decoders.BASE64.decode(applicationProperties.auth.secret)
    )

    // Генератор токенов
    fun generateTokens(userDetails: Users) = Pair(
        generateAccessToken(userDetails),
        generateRefreshToken(userDetails)
    )

    private fun generateAccessToken(user: Users): String =
        Jwts.builder()
            .subject(user.email)
            .claim("id", user.id)
            .expiration(
                Date(
                    System.currentTimeMillis() + applicationProperties.auth.accessLifeTime
                )
            )
            .signWith(signKey)
            .compact()

    private fun generateRefreshToken(user: Users): String =
        Jwts.builder()
            .subject(user.email)
            .claim("id", user.id)
            .expiration(
                Date(
                    System.currentTimeMillis() + applicationProperties.auth.refreshLifeTime
                )
            )
            .signWith(signKey)
            .compact()

    // Валидаторы
    private fun isTokenExpired(token: String): Boolean = extractClaims(token).expiration.before(Date())

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractClaims(token).subject
        return username == userDetails.username && !isTokenExpired(token)
    }

    // Экстракторы
    fun getLogin(token: String): String = extractClaims(token).subject

    private fun extractClaims(token: String): Claims = Jwts
        .parser()
        .verifyWith(signKey)
        .build()
        .parseSignedClaims(token)
        .payload
}
