package com.vpr42.marketplaceauthapi.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class ApplicationProperties(
    val auth: AuthenticationProperties,
    val user: UserProperties,
) {
    data class AuthenticationProperties(
        val accessLifeTime: Long,
        val refreshLifeTime: Long,
        val secret: String,
    )

    data class UserProperties(
        val defaultAvatarUrl: String
    )
}
