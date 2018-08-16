package de.smartsquare.kickchain.kickway.owning

import spock.lang.Specification

class TeamSpecification extends Specification {

    def team = new Team()

    def 'score increments the score by one'() {
        when: team.score()
        then: team.score == 1
    }

}
