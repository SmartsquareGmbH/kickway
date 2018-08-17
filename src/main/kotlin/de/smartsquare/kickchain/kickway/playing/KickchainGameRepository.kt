package de.smartsquare.kickchain.kickway.playing

import org.springframework.stereotype.Repository

@Repository
class KickchainGameRepository : GameRepository {
    override fun save(game: Game): Int = 1
}