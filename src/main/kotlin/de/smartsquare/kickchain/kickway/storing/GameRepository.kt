package de.smartsquare.kickchain.kickway.storing

import de.smartsquare.kickchain.kickway.ConfigurationProperties
import de.smartsquare.kickchain.kickway.analyzing.Blockchain
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Repository
class GameRepository(
    private val kickchain: RestTemplate,
    private val config: ConfigurationProperties
) {

    fun save(game: Blockchain.Block.Game) {
        kickchain.postForEntity<String>("${config.kickchain.url}/game/new", game)
    }
}
