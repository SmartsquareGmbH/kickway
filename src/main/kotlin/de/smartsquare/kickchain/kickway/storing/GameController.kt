package de.smartsquare.kickchain.kickway.storing

import de.smartsquare.kickchain.kickway.Blockchain
import de.smartsquare.kickchain.kickway.elo.EloRating
import de.smartsquare.kickchain.kickway.elo.EloRatingRepository
import de.smartsquare.kickchain.kickway.elo.readjust
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(
    private val gameRepository: GameRepository,
    private val eloRatingRepository: EloRatingRepository
) {

    @PostMapping("/game")
    internal fun save(@RequestBody game: Blockchain.Block.Game): ResponseEntity<Any> {
        if ((game.team1.players + game.team2.players).any(String::isBlank)) {
            return ResponseEntity.badRequest().body("A player name cannot be empty")
        }
        if (game.score.goals1 > 10 || game.score.goals2 > 10) {
            return ResponseEntity.badRequest().body("A team cannot score more than ten goals")
        }
        if (game.score.goals1 != 10 && game.score.goals2 != 10) {
            return ResponseEntity.badRequest().body("Unfinished games cannot be persisted")
        }
        if (game.score.goals1 < 0 || game.score.goals2 < 0) {
            return ResponseEntity.badRequest().body("A team cannot score minus goals")
        }

        val eloOne = eloRatingRepository.findEloRatingByTeamFirstAndTeamSecond(game.team1.first, game.team1.second)
            .orElse(EloRating(game.team1.first, game.team1.second, elo = 1000.0, matches = 0))
        val eloTwo = eloRatingRepository.findEloRatingByTeamFirstAndTeamSecond(game.team2.first, game.team2.second)
            .orElse(EloRating(game.team2.first, game.team2.second, elo = 1000.0, matches = 0))
        val (newEloOne, newEloTwo) = game.readjust(eloOne, eloTwo)
        eloRatingRepository.save(newEloOne)
        eloRatingRepository.save(newEloTwo)

        gameRepository.save(game)

        return ResponseEntity.ok().build()
    }
}
