package com.vpr42.marketplaceauthapi.repository

import com.vpr42.marketplaceauthapi.jooq.tables.pojos.Users
import com.vpr42.marketplaceauthapi.jooq.tables.records.UsersRecord
import com.vpr42.marketplaceauthapi.jooq.tables.references.USERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val dsl: DSLContext
) {

    fun findByEmail(email: String) = dsl
        .selectFrom(USERS)
        .where(USERS.EMAIL.eq(email))
        .fetchOneInto(Users::class.java)

    fun insert(record: UsersRecord) = dsl
        .insertInto(USERS)
        .set(record)
        .returning()
        .fetchOneInto(Users::class.java)
}
