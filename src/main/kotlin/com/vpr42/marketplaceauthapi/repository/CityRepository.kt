package com.vpr42.marketplaceauthapi.repository

import com.vpr42.marketplaceauthapi.jooq.tables.pojos.Cities
import com.vpr42.marketplaceauthapi.jooq.tables.references.CITIES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class CityRepository(
    private val dsl: DSLContext
) {

    fun getCityList() = dsl
        .selectFrom(CITIES)
        .fetchInto(Cities::class.java)
}
