package com.vpr42.marketplaceauthapi.exception

open class AuthenticationException(
    message: String,
    e: Throwable? = null
) : Exception("Authentication exception: $message", e)
