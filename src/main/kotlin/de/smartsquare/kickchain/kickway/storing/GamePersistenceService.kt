package de.smartsquare.kickchain.kickway.storing

import de.smartsquare.kickchain.kickway.Blockchain
import de.smartsquare.kickchain.kickway.elo.EloRating
import de.smartsquare.kickchain.kickway.elo.EloRatingRepository
import de.smartsquare.kickchain.kickway.elo.readjust
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GamePersistenceService(
    private val eloRatingRepository: EloRatingRepository,
    private val gameRepository: GameRepository
) {

    @Transactional
    fun store(game: Blockchain.Block.Game) {
        val eloOne = eloRatingRepository.findEloRatingByTeamFirstAndTeamSecond(game.team1.first, game.team1.second)
            .orElse(EloRating(game.team1.first, game.team1.second, elo = 1000.0, matches = 0))
        val eloTwo = eloRatingRepository.findEloRatingByTeamFirstAndTeamSecond(game.team2.first, game.team2.second)
            .orElse(EloRating(game.team2.first, game.team2.second, elo = 1000.0, matches = 0))
        val (newEloOne, newEloTwo) = game.readjust(eloOne, eloTwo)

        eloRatingRepository.save(newEloOne)
        eloRatingRepository.save(newEloTwo)

        gameRepository.save(game)
    }
}