package com.vpr42.marketplaceauthapi.dto.request

data class RegistrationRequest(
    val email: String,
    val password: String,
    val surname: String,
    val name: String,
    val patronymic: String,
    val city: Int,
)
