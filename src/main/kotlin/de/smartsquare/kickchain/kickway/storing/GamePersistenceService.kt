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
        val eloOfTeamOne = findEloOf(game.team1)
        val eloOfTeamTwo = findEloOf(game.team2)

        val (newEloOne, newEloTwo) = game.readjust(eloOfTeamOne, eloOfTeamTwo)

        eloRatingRepository.save(newEloOne)
        eloRatingRepository.save(newEloTwo)

        gameRepository.save(game)
    }

    private fun findEloOf(team: Blockchain.Block.Game.Team): EloRating {
        return if (team.players.size > 1) {
            eloRatingRepository.findEloRatingByTeamFirstAndTeamSecond(team.first, team.second)
                .orElse(EloRating(team.first, team.second))
        } else {
            eloRatingRepository.findEloRatingByTeamFirst(team.first).orElse(EloRating(team.first))
        }
    }
}
