package com.vpr42.marketplaceauthapi.exception

class UserNotFoundException(
    user: String,
    e: Throwable? = null
) : AuthenticationException("User $user not found", e)
