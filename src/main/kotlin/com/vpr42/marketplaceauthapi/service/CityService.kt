package com.vpr42.marketplaceauthapi.service

import com.vpr42.marketplaceauthapi.jooq.tables.pojos.Cities
import com.vpr42.marketplaceauthapi.repository.CityRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class CityService(
    private val cityRepository: CityRepository
) {
    private val logger = LoggerFactory.getLogger(CityService::class.java)

    private var _cities: List<Cities> = listOf()
    val cities: List<Cities>
        get() = _cities

    @PostConstruct
    fun initCitiesList() {
        logger.info("Cities list initializing...")
        _cities = cityRepository.getCityList()
        logger.info("Cities list initialized successfully")
    }

    @Scheduled(cron = "\${app.schedule.city-list-update}")
    fun citiesUpload() {
        _cities = cityRepository.getCityList()
    }
}
