package de.smartsquare.kickchain.kickway.playing

import org.springframework.stereotype.Component

@Component
class Server(val repository: GameRepository) : Spectatable {

    val joinableLobbyNames get() = lobbies.filterNot { (_, lobby) -> lobby.full }.keys.toList()

    private val lobbies: MutableMap<String, Game> = mutableMapOf()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun MutableMap<String, Game>.getOrThrowException(name: String): Game {
        return this[name] ?: throw LobbyNotFoundException(name)
    }

    fun createNewLobby(lobby: String, owner: String): Game {
        if (lobby.none()) throw IllegalArgumentException("A lobby must have a non-empty name")
        if (lobbies.containsKey(lobby)) throw LobbyAlreadyExistsException(lobby)

        return Game(owner).also { lobbies[lobby] = it }
    }

    override fun spectate(lobbyName: String): Game = lobbies.getOrThrowException(lobbyName)

    fun joinLeft(lobbyName: String, name: String) {
        lobbies[lobbyName] = lobbies.getOrThrowException(lobbyName).joinLeftTeam(name)
    }

    fun joinRight(lobbyName: String, name: String) {
        lobbies[lobbyName] = lobbies.getOrThrowException(lobbyName).joinRightTeam(name)
    }

    fun leave(lobbyName: String, name: String) {
        lobbies.getOrThrowException(lobbyName)
            .leaveLeftTeam(name)
            .leaveRightTeam(name)
            .also { lobbies[lobbyName] = it }
            .takeIf { it.empty }
            ?.also { lobbies.remove(lobbyName) }
    }

    fun scoreLeftTeam(lobbyName: String) {
        lobbies[lobbyName]
            ?.scoreLeftTeam()
            ?.also { saveOrRemove(it, lobbyName) }
    }

    fun scoreRightTeam(lobbyName: String) {
        lobbies[lobbyName]
            ?.scoreRightTeam()
            ?.also { saveOrRemove(it, lobbyName) }
    }

    private fun saveOrRemove(
        lobbyWithIncrementedScore: Game,
        lobbyName: String
    ) {
        if (lobbyWithIncrementedScore.completed) {
            repository.save(lobbyWithIncrementedScore)
            lobbies.remove(lobbyName)
        } else {
            lobbies[lobbyName] = lobbyWithIncrementedScore
        }
    }
}
