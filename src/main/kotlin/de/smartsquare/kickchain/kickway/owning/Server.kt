package de.smartsquare.kickchain.kickway.owning

import org.springframework.stereotype.Component

@Component
class Server(val repository: GameRepository) {

    private val lobbies: MutableMap<String, Game> = mutableMapOf()

    fun createNewGame(lobby: String, owner: String) {
        lobbies[lobby] = Game(owner)
    }

    fun spectate(lobby: String): Game? = lobbies[lobby]

    fun joinLeft(lobby: String, name: String) {
        lobbies[lobby]?.teamLeft?.player?.add(name)
    }

    fun joinRight(lobby: String, name: String) {
        lobbies[lobby]?.teamRight?.player?.add(name)
    }

    fun scoreTeamLeft(lobby: String) {
        lobbies[lobby]?.let {
            it.scoreTeamLeft()

            if (it.isCompleted()) {
                repository.save(it)
                lobbies.remove(lobby)
            }
        }
    }

    fun scoreTeamRight(lobby: String) {
        lobbies[lobby]?.scoreTeamRight()
    }

}