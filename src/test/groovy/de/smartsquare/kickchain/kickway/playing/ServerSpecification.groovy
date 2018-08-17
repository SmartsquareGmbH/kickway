package de.smartsquare.kickchain.kickway.playing

import spock.lang.Specification

class ServerSpecification extends Specification {

    def repository = Mock(GameRepository)
    def server = new Server(repository)

    def 'create new game'() {
        when:
        server.createNewGame('Ballerbude', 'deen')
        then:
        server.lobbies['Ballerbude'].teamLeft.player.contains('deen')
    }

    def 'server throws exception if lobby already exists'() {
        when:
        server.createNewGame('Ballerbude', 'deen')
        server.createNewGame('Ballerbude', 'deen')
        then:
        def error = thrown(RuntimeException)
        error.message == "A lobby with the name Ballerbude already exists"
    }

    def 'join team of owner'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        server.joinLeft('Ballerbude', 'ruby')
        then:
        server.lobbies['Ballerbude'].teamLeft.player.contains('ruby')
    }

    def 'join other team'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        server.joinRight('Ballerbude', 'ruby')
        then:
        server.lobbies['Ballerbude'].teamRight.player.contains('ruby')
    }

    def 'join non existent lobby'() {
        when:
        server.joinLeft('Ballerbude', 'ruby')
        then:
        def error = thrown(RuntimeException)
        error.message == "A lobby with the name Ballerbude does not exist"
    }

    def 'spectate'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        server.joinRight('Ballerbude', 'ruby')
        then:
        server.spectate('Ballerbude') == server.lobbies['Ballerbude']
    }

    def 'server throws exception if a lobby without name is attempted to create'() {
        when:
        server.createNewGame('', 'deen')
        then:
        def error = thrown(RuntimeException)
        error.message == 'A lobby must have a non-empty name'
    }


    def 'score team left'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        server.scoreTeamLeft('Ballerbude')
        then:
        server.lobbies['Ballerbude'].teamLeft.score == 1
    }

    def 'score team right'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        server.scoreTeamRight('Ballerbude')
        then:
        server.lobbies['Ballerbude'].teamRight.score == 1
    }

    def 'server persists game if one team has won'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        10.times { server.scoreTeamLeft('Ballerbude') }
        then:
        1 * repository.save(_)
    }

    def 'server persists only once'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        11.times { server.scoreTeamLeft('Ballerbude') }
        then:
        1 * repository.save(_)
    }

    def 'server removes completed game'() {
        given:
        server.createNewGame('Ballerbude', 'deen')
        when:
        10.times { server.scoreTeamLeft('Ballerbude') }
        then:
        server.lobbies.isEmpty()
    }

    def 'server throws exception if lobby is attempted to spectate a lobby that does not exists'() {
        when:
        server.spectate('lobbywhichdoesnotexists')
        then:
        def error = thrown(RuntimeException)
        error.message == 'A lobby with the name lobbywhichdoesnotexists does not exists'
    }

    def 'get list of joinable games from server'() {

    }

}
