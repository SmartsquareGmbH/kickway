package de.smartsquare.kickchain.kickway.playing

import de.smartsquare.kickchain.kickway.spectating.SpectatorController
import de.smartsquare.kickchain.kickway.toUri
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotEmpty

@RestController
class GameController(private val server: Server) {

    private val authorization = HashMap<String, String>()

    @PostMapping("/game/solo/{lobbyName}/{ownerName}")
    fun create(
        @PathVariable("lobbyName") lobbyName: String,
        @PathVariable("ownerName") ownerName: String,
        @NotEmpty @RequestHeader raspberry: String
    ): ResponseEntity<Any> {
        try {
            server.createNewLobby(lobbyName, ownerName)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(e.message)
        } catch (e: LobbyAlreadyExistsException) {
            return ResponseEntity.status(CONFLICT).body(e.message)
        }
        authorization[raspberry] = lobbyName

        return HATEOASResponse().apply {
            add(linkTo(methodOn(GameController::class.java).joinLeft(lobbyName, null)).withRel("joinLeft"))
            add(linkTo(methodOn(GameController::class.java).joinRight(lobbyName, null)).withRel("joinRight"))
            add(linkTo(methodOn(SpectatorController::class.java).watch(lobbyName)).withSelfRel())
        }.let {
            ResponseEntity.created("/game/$lobbyName".toUri()).body(it)
        }
    }

    @PatchMapping("/game/join/left/{lobbyName}/{playerName}")
    fun joinLeft(
        @PathVariable("lobbyName") lobbyName: String,
        @PathVariable("playerName") playerName: String?
    ): ResponseEntity<Any> {
        try {
            if (playerName == null) {
                return ResponseEntity.badRequest().build()
            }

            server.joinLeft(lobbyName, playerName)
            return ResponseEntity.ok().build()
        } catch (e: LobbyNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/game/join/right/{lobbyName}/{playerName}")
    fun joinRight(
        @PathVariable("lobbyName") lobbyName: String,
        @PathVariable("playerName") playerName: String?
    ): ResponseEntity<Any> {
        try {
            if (playerName == null) {
                return ResponseEntity.badRequest().build()
            }

            server.joinRight(lobbyName, playerName)
            return ResponseEntity.ok().build()
        } catch (e: LobbyNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/game/score/left/{lobbyName}")
    fun scoreLeft(
        @PathVariable("lobbyName") lobbyName: String,
        @NotEmpty @RequestHeader raspberry: String
    ): ResponseEntity<Any> {
        authorization[raspberry] ?: return ResponseEntity.status(UNAUTHORIZED).build()

        server.scoreLeftTeam(lobbyName)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/game/score/right/{lobbyName}")
    fun scoreRight(
        @PathVariable("lobbyName") lobbyName: String,
        @NotEmpty @RequestHeader raspberry: String
    ): ResponseEntity<Any> {
        authorization[raspberry] ?: return ResponseEntity.status(UNAUTHORIZED).build()

        server.scoreRightTeam(lobbyName)
        return ResponseEntity.ok().build()
    }

    private class HATEOASResponse : ResourceSupport()
}
