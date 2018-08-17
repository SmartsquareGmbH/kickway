package de.smartsquare.kickchain.kickway.playing

import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import sun.plugin.dom.exception.InvalidStateException
import javax.validation.constraints.NotEmpty

@RestController
class GameController(val server: Server) {

    val authorization = HashMap<String, String>()

    @PostMapping("/game/solo/{lobbyName}/{ownerName}")
    fun create(@PathVariable("lobbyName") lobbyName: String, @PathVariable("ownerName") ownerName: String, @NotEmpty @RequestBody raspberry: String): ResponseEntity<Any> {
        try {
            server.createNewLobby(lobbyName, ownerName)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(e.message)
        } catch (e: InvalidStateException) {
            return ResponseEntity.status(CONFLICT).body(e.message)
        }
        authorization[raspberry] = lobbyName

        return ResponseEntity.ok().build<Any>()
    }

    @PostMapping("/game/join/left/{lobbyName}/{playerName}")
    fun joinLeft(@PathVariable("lobbyName") lobbyName: String, @PathVariable("playerName") playerName: String, @NotEmpty @RequestBody raspberry: String): ResponseEntity<Any> {

        return ResponseEntity.ok().build<Any>()
    }


}