package de.smartsquare.kickchain.kickway.playing

class TeamAlreadyFullException : RuntimeException() {
    override val message: String?
        get() = "The team is full"
}

class PlayerAlreadyExistsException(val playerName: String) : RuntimeException() {
    override val message: String?
        get() = "The player $playerName already joined the lobby"
}

class LobbyAlreadyExistsException(val lobbyName: String) : RuntimeException() {
    override val message: String?
        get() = "The lobby $lobbyName already exists"
}

class LobbyNotFoundException(val lobbyName: String) : RuntimeException() {
    override val message: String?
        get() = "Lobby with name $lobbyName not found"
}
