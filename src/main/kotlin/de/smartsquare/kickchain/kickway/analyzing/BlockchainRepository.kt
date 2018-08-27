package de.smartsquare.kickchain.kickway.analyzing

import de.smartsquare.kickchain.kickway.ConfigurationProperties
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Repository
class BlockchainRepository(
    private val kickchain: RestTemplate,
    private val config: ConfigurationProperties
) {

    fun fetch(): Blockchain = try {
        kickchain.getForObject("${config.kickchain.url}/chain", Blockchain::class.java)
    } catch (restException: RestClientException) {
        throw KickchainException(restException, restException.mostSpecificCause.message ?: "Internal Error")
    } ?: throw KickchainException("The kickchain response could not be parsed.")
}
