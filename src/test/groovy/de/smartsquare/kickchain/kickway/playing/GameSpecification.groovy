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

}
