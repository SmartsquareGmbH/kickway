package de.smartsquare.kickchain.kickway.playing

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotEmpty

@RestController
class GameController(val server: Server) {

    val authorization = HashMap<String, String>()

    @PostMapping("/game/solo/{lobbyName}/{ownerName}")
    fun create(@PathVariable("lobbyName") lobbyName: String, @PathVariable("ownerName") ownerName: String, @NotEmpty @RequestBody raspberry: String): ResponseEntity<Any> {
        server.createNewLobby(lobbyName, ownerName)
        authorization[raspberry] = lobbyName

        return ResponseEntity.ok().build<Any>()
    }

}