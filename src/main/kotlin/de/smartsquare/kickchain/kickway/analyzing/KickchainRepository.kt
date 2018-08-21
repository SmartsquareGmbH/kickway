package de.smartsquare.kickchain.kickway.analyzing

import de.smartsquare.kickchain.kickway.KickwayConfigurationProperties
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Repository
class KickchainRepository(
    private val restTemplate: RestTemplate,
    private val config: KickwayConfigurationProperties
) : BlockchainRepository {

    override fun fetch(): Blockchain = try {
        restTemplate.getForObject("${config.kickchain.url}/chain", Blockchain::class.java)
    } catch (restException: RestClientException) {
        throw KickchainException(restException, restException.mostSpecificCause.message ?: "Internal Error")
    } ?: throw KickchainException("The kickchain response could not be parsed.")
}
