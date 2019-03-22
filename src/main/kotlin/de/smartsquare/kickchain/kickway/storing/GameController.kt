package de.smartsquare.kickchain.kickway.storing

import de.smartsquare.kickchain.kickway.Blockchain
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GameController(
    private val gamePersistenceService: GamePersistenceService
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

        gamePersistenceService.store(game)

        return ResponseEntity.ok().build()
    }
}
