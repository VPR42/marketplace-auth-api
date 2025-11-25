package com.vpr42.marketplaceauthapi

import com.vpr42.marketplaceauthapi.properties.ApplicationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(
    ApplicationProperties::class
)
class MarketplaceAuthApiApplication

fun main(args: Array<String>) {
    runApplication<MarketplaceAuthApiApplication>(*args)
}
