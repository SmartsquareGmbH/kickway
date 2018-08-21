package de.smartsquare.kickchain.kickway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate

@Configuration
class HttpBasicAuthConfiguration(private val properties: KickwayConfigurationProperties) {

    @Bean
    fun restTemplateWithBasicAuth(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        return restTemplateBuilder.basicAuthorization(properties.kickchain.name, properties.kickchain.password).build()
    }
}
