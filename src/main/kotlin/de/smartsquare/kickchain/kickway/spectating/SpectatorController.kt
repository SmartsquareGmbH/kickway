package de.smartsquare.kickchain.kickway.spectating

import de.smartsquare.kickchain.kickway.playing.Game
import de.smartsquare.kickchain.kickway.playing.LobbyNotFoundException
import de.smartsquare.kickchain.kickway.playing.Spectatable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SpectatorController(val server: Spectatable) {

    @GetMapping("/game/{lobbyName}")
    fun watch(@PathVariable("lobbyName") lobbyName: String): ResponseEntity<Game> {
        try {
            val lobby = server.spectate(lobbyName)
            return ResponseEntity.ok(lobby)
        } catch (e: LobbyNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }

}