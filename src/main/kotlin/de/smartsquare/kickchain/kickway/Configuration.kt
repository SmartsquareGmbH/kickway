package de.smartsquare.kickchain.kickway

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.map.repository.config.EnableMapRepositories
import org.springframework.web.client.RestTemplate

@Configuration
@EnableMapRepositories
class Configuration(
    private val kickway: ConfigurationProperties
) {

    @Bean
    fun restTemplateWithBasicAuth(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        return restTemplateBuilder.basicAuthorization(
            kickway.kickchain.authorization.name,
            kickway.kickchain.authorization.password
        ).build()
    }
}
