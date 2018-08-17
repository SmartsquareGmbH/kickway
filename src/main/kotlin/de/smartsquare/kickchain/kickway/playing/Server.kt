package de.smartsquare.kickchain.kickway.playing

import org.springframework.stereotype.Component
import sun.plugin.dom.exception.InvalidStateException

@Component
class Server(val repository: GameRepository) : Spectatable {

    private val lobbies: MutableMap<String, Game> = mutableMapOf()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun MutableMap<String, Game>.getOrThrowException(name: String): Game {
        return this[name] ?: throw IllegalArgumentException("A lobby with the name ${name} does not exist")
    }

    fun createNewLobby(lobby: String, owner: String) {
        if (lobby.none()) throw IllegalArgumentException("A lobby must have a non-empty name")
        if (lobbies.containsKey(lobby)) throw InvalidStateException("A lobby with the name ${lobby} already exists")

        lobbies[lobby] = Game(owner)
    }

    override fun spectate(lobbyName: String): Game = lobbies.getOrThrowException(lobbyName)

    fun joinLeft(lobbyName: String, name: String) {
        val lobby = lobbies.getOrThrowException(lobbyName)
        lobby.teamLeft.join(name)
    }

    fun joinRight(lobbyName: String, name: String) {
        val lobby = lobbies.getOrThrowException(lobbyName)
        lobby.teamRight.join(name)
    }

    fun leave(lobbyName: String, name: String) {
        val lobby = lobbies.getOrThrowException(lobbyName)

        lobby.teamLeft.leave(name)
        lobby.teamRight.leave(name)

        if (lobby.isEmpty()) lobbies.remove(lobbyName)
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

    fun getJoinableLobbies(): List<String> {
        return lobbies.filterNot { it.value.isFull() }.keys.toList()
    }

}