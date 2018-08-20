package de.smartsquare.kickchain.kickway.playing

import spock.lang.Specification

class ServerSpecification extends Specification {

    def repository = Mock(GameRepository)
    def server = new Server(repository)

    def 'create new lobby'() {
        when:
        server.createNewLobby('Ballerbude', 'deen')
        then:
        server.lobbies['Ballerbude'].leftTeam.players.contains('deen')
    }

    def 'server throws exception if lobby already exists'() {
        when:
        server.createNewLobby('Ballerbude', 'deen')
        server.createNewLobby('Ballerbude', 'deen')
        then:
        def error = thrown(RuntimeException)
        error.message == 'The lobby Ballerbude already exists'
    }

    def 'join team of owner'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.joinLeft('Ballerbude', 'ruby')
        then:
        server.lobbies['Ballerbude'].leftTeam.players.contains('ruby')
    }

    def 'join other team'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.joinRight('Ballerbude', 'ruby')
        then:
        server.lobbies['Ballerbude'].rightTeam.players.contains('ruby')
    }

    def 'join non existent lobby'() {
        when:
        server.joinLeft('Ballerbude', 'ruby')
        then:
        def error = thrown(RuntimeException)
        error.message == 'Lobby with name Ballerbude not found'
    }

    def 'spectate'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.joinRight('Ballerbude', 'ruby')
        then:
        server.spectate('Ballerbude') == server.lobbies['Ballerbude']
    }

    def 'server throws exception if a lobby without name is attempted to create'() {
        when:
        server.createNewLobby('', 'deen')
        then:
        def error = thrown(RuntimeException)
        error.message == 'A lobby must have a non-empty name'
    }

    def 'score team left'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.scoreLeftTeam('Ballerbude')
        then:
        server.lobbies['Ballerbude'].leftTeam.score == 1
    }

    def 'score team right'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.scoreLeftTeam('Ballerbude')
        then:
        server.lobbies['Ballerbude'].leftTeam.score == 1
    }

    def 'server persists game if one team has won'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        10.times { server.scoreLeftTeam('Ballerbude') }
        then:
        1 * repository.save(_)
    }

    def 'server persists only once'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        11.times { server.scoreLeftTeam('Ballerbude') }
        then:
        1 * repository.save(_)
    }

    def 'server removes completed game'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        10.times { server.scoreLeftTeam('Ballerbude') }
        then:
        server.lobbies.isEmpty()
    }

    def 'server throws exception if lobby is attempted to spectate a lobby that does not exist'() {
        when:
        server.spectate('lobbywhichdoesnotexists')
        then:
        def error = thrown(RuntimeException)
        error.message == 'Lobby with name lobbywhichdoesnotexists not found'
    }

    def 'server returns list of joinable lobby with one player'() {
        when:
        server.createNewLobby('Ballerbude', 'deen')
        then:
        server.joinableLobbyNames.contains('Ballerbude')
    }

    def 'server does not return joinable lobby if game is full'() {
        when:
        server.createNewLobby('Ballerbude', 'deen')
        server.joinLeft('Ballerbude', 'ruby')
        server.joinRight('Ballerbude', 'skonair')
        server.joinRight('Ballerbude', 'alexn')

        then:
        server.joinableLobbyNames.isEmpty()
    }

    def 'leave server'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.joinLeft('Ballerbude', 'ruby')
        server.leave('Ballerbude', 'ruby')
        then:
        server.lobbies['Ballerbude'].leftTeam.players == ['deen']
    }

    def 'server removes lobby if is empty'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.leave('Ballerbude', 'deen')
        then:
        server.lobbies.isEmpty()
    }

    def 'server throws exception if the playergame is already used'() {
        setup:
        server.createNewLobby('Ballerbude', 'deen')
        when:
        server.joinRight('Ballerbude', 'deen')
        then:
        def error = thrown(PlayerAlreadyExistsException)
        error.message == 'The player deen already joined the lobby'
    }
}
