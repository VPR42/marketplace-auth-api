package com.vpr42.marketplaceauthapi.dto.response

import com.vpr42.marketplaceauthapi.dto.User

data class AuthenticationResponse(
    val message: String,
    val token: String,
    val user: User,
)
