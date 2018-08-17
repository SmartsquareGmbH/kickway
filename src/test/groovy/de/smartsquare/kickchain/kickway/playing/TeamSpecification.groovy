package de.smartsquare.kickchain.kickway.playing

import spock.lang.Specification

class TeamSpecification extends Specification {

    def team = new Team()

    def 'score increments the score by one'() {
        when:
        team.score()
        then:
        team.score == 1
    }

    def 'team throws exception if a third player tries to join the team'() {
        when:
        team.join("deen")
        team.join("ruby")
        team.join("skonair")

        then:
        def error = thrown(RuntimeException)
        error.message == "The team is full"
    }

    def 'team throws exception if a player with a empty name attempts to join'() {
        when:
        team.join('')
        then:
        def error = thrown(RuntimeException)
        error.message == 'A player must have a non-empty name'
    }

}