package de.smartsquare.kickchain.kickway.playing

import org.springframework.stereotype.Component

@Component
class Server(val repository: GameRepository) : Spectatable {

    private val lobbies: MutableMap<String, Game> = mutableMapOf()

    fun createNewLobby(lobby: String, owner: String) {
        if (lobby.none()) throw RuntimeException("A lobby must have a non-empty name")
        if (lobbies.containsKey(lobby)) throw RuntimeException("A lobby with the name ${lobby} already exists")

        lobbies[lobby] = Game(owner)
    }

    override fun spectate(lobby: String): Game = getLobbyWithNameOrThrowException(lobby)

    fun joinLeft(lobbyName: String, name: String) {
        val lobby = getLobbyWithNameOrThrowException(lobbyName)
        lobby.teamLeft.join(name)
    }

    fun joinRight(lobbyName: String, name: String) {
        val lobby = getLobbyWithNameOrThrowException(lobbyName)
        lobby.teamRight.join(name)
    }

    fun leave(lobbyName: String, name: String) {
        val lobby = getLobbyWithNameOrThrowException(lobbyName)

        lobby.teamLeft.leave(name)
        lobby.teamRight.leave(name)

        if(lobby.isEmpty()) lobbies.remove(lobbyName)
    }

    private fun getLobbyWithNameOrThrowException(name: String) = lobbies[name]
            ?: throw RuntimeException("A lobby with the name ${name} does not exist")

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

    fun getJoinableLobbies(): List<String> {
        return lobbies.filterNot { it.value.isFull() }.keys.toList()
    }

}