package com.vpr42.marketplaceauthapi.dto.request

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
