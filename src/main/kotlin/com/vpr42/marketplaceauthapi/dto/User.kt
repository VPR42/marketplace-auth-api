package com.vpr42.marketplaceauthapi.dto

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val surname: String,
    val name: String,
    val patronymic: String,
    val avatarPath: String,
    val createdAt: LocalDateTime,
    val city: Int,
)
