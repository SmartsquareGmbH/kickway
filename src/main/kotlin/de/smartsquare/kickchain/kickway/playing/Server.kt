package de.smartsquare.kickchain.kickway.playing

import org.springframework.stereotype.Component

@Component
class Server(val repository: GameRepository) {

    private val lobbies: MutableMap<String, Game> = mutableMapOf()

    fun createNewGame(lobby: String, owner: String) {
        if (lobby.none()) throw RuntimeException("A lobby must have a non-empty name")
        if (lobbies.containsKey(lobby)) throw RuntimeException("A lobby with the name ${lobby} already exists")

        lobbies[lobby] = Game(owner)
    }

    fun spectate(lobby: String): Game? = lobbies[lobby]

    fun joinLeft(lobby: String, name: String) {
        val game = lobbies[lobby]?: throw RuntimeException("A lobby with the name ${lobby} does not exist")
        game.teamLeft.join(name)
    }

    fun joinRight(lobby: String, name: String) {
        val game = lobbies[lobby]?: throw RuntimeException("A lobby with the name ${lobby} does not exist")
        game.teamRight.join(name)
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