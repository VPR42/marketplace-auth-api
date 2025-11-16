package com.vpr42.marketplaceauthapi.dto

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserAuthDetails(
    val login: String,
    val authPassword: String,
) : UserDetails {
    override fun getUsername() = this.login
    override fun getPassword() = this.authPassword
    override fun getAuthorities() = listOf(SimpleGrantedAuthority("USER"))
}
