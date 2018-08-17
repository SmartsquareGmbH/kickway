package de.smartsquare.kickchain.kickway.playing

import spock.lang.Specification

class GameSpecification extends Specification {

    def game = new Game('deen')

    def 'game is over if team left has scored ten goals'() {
        when:
        10.times { game.scoreTeamLeft() }
        then:
        game.isCompleted()
    }

    def 'game is over if team right has scored ten goals'() {
        when:
        10.times { game.scoreTeamRight() }
        then:
        game.isCompleted()
    }

    def 'right team has won if it has scored ten goals'() {
        when:
        10.times { game.scoreTeamRight() }
        then:
        game.rightTeamWon()
    }

    def 'left team has won if it has scored ten goals'() {
        when:
        10.times { game.scoreTeamLeft() }
        then:
        game.leftTeamWon()
    }

    def 'max of score is ten'() {
        when:
        11.times { game.scoreTeamLeft() }
        then:
        game.teamLeft.score == 10
    }

    def 'lobby is full if four players joined'() {
        when:
        game.teamLeft.join("ruby")
        game.teamRight.join("skonair")
        game.teamRight.join("drs")
        then:
        game.isFull()
    }

    def 'game throws exception on duplicate name'() {
        when:
        game.teamRight.join('ruby')
        game.teamRight.join('ruby')
        then:
        def error = thrown(RuntimeException)
        error.message == "A player with the name ruby is already in-game"
    }

}
