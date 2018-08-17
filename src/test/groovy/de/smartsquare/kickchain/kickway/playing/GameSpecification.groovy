package de.smartsquare.kickchain.kickway.playing

import spock.lang.Specification

class GameSpecification extends Specification {

    def game = new Game('deen')

    def 'game is over if team left has scored ten goals'() {
        expect:
        (1..10).inject(game) { result, i -> result.scoreLeftTeam() }.completed
    }

    def 'game is over if team right has scored ten goals'() {
        expect:
        (1..10).inject(game) { result, i -> result.scoreRightTeam() }.completed
    }

    def 'right team has won if it has scored ten goals'() {
        expect:
        (1..10).inject(game) { result, i -> result.scoreRightTeam() }.rightTeamWon
    }

    def 'left team has won if it has scored ten goals'() {
        expect:
        (1..10).inject(game) { result, i -> result.scoreLeftTeam() }.leftTeam.score == 10
    }

    def 'max of score is ten'() {
        expect:
        (1..11).inject(game) { result, i -> result.scoreLeftTeam() }.leftTeam.score == 10
    }

    def 'lobby is full if four players joined'() {
        expect:
        game.joinLeftTeam("ruby").joinRightTeam("skonair").joinRightTeam("drs").full
    }

    def 'game throws exception on duplicate name'() {
        when:
        game.joinRightTeam('ruby').joinRightTeam('ruby')
        then:
        def error = thrown(RuntimeException)
        error.message == "The player ruby already joined the lobby"
    }

    def 'first player of left team is owner by default'() {
        expect:
        game.owner == 'deen'
    }

    def 'owner will be reassigned if previous owner leaves'() {
        expect:
        game.joinRightTeam('ruby').leaveLeftTeam('deen').owner == 'ruby'
    }

}
