package de.smartsquare.kickchain.kickway.playing

import spock.lang.Specification

class TeamSpecification extends Specification {

    def team = new Team()

    def 'score increments the score by one'() {
        expect:
        team.score().score == 1
    }

    def 'team throws exception if a third player tries to join the team'() {
        when:
        team + 'deen' + 'ruby' + 'skonair'
        then:
        def error = thrown(RuntimeException)
        error.message == "The team is full"
    }

    def 'team throws exception if a player with a empty name attempts to join'() {
        when:
        team + ''
        then:
        def error = thrown(RuntimeException)
        error.message == 'A player must have a non-empty name'
    }

}