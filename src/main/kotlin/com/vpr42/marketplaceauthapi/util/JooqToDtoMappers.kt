package com.vpr42.marketplaceauthapi.util

import com.vpr42.marketplaceauthapi.dto.User
import com.vpr42.marketplaceauthapi.dto.UserAuthDetails
import com.vpr42.marketplaceauthapi.jooq.tables.pojos.Users

fun Users.toUserDto() = User(
    id = this.id,
    email = this.email,
    surname = this.surname,
    name = this.name,
    patronymic = this.patronymic,
    avatarPath = this.avatarPath,
    createdAt = this.createdAt,
    city = this.city,
)

fun Users.toUserAuthDetails() = UserAuthDetails(
    login = this.email,
    authPassword = this.password,
)
