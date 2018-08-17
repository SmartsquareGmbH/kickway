package de.smartsquare.kickchain.kickway.playing

import de.smartsquare.kickchain.kickway.spectating.SpectatorController
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotEmpty

@RestController
class GameController(private val server: Server) {

    private val authorization = HashMap<String, String>()

    @PostMapping("/game/solo/{lobbyName}/{ownerName}")
    fun create(@PathVariable("lobbyName") lobbyName: String, @PathVariable("ownerName") ownerName: String, @NotEmpty @RequestBody raspberry: String): ResponseEntity<Any> {
        val game = try {
            server.createNewLobby(lobbyName, ownerName)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(e.message)
        } catch (e: LobbyAlreadyExistsException) {
            return ResponseEntity.status(CONFLICT).body(e.message)
        }
        authorization[raspberry] = lobbyName

        game.add(linkTo(methodOn(GameController::class.java).joinLeft(lobbyName, null)).withRel("joinLeft"))
        game.add(linkTo(methodOn(GameController::class.java).joinRight(lobbyName, null)).withRel("joinRight"))
        game.add(linkTo(methodOn(SpectatorController::class.java).watch(lobbyName)).withSelfRel())
        return ResponseEntity(game, CREATED)
    }

    @PostMapping("/game/join/left/{lobbyName}/{playerName}")
    fun joinLeft(@PathVariable("lobbyName") lobbyName: String, @PathVariable("playerName") playerName: String?): ResponseEntity<Any> {
        try {
            server.joinLeft(lobbyName, playerName!!)
            return ResponseEntity.ok().build()
        } catch (e: LobbyNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/game/join/right/{lobbyName}/{playerName}")
    fun joinRight(@PathVariable("lobbyName") lobbyName: String, @PathVariable("playerName") playerName: String?): ResponseEntity<Any> {
        try {
            server.joinRight(lobbyName, playerName!!)
            return ResponseEntity.ok().build()
        } catch (e: LobbyNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }

}