package de.smartsquare.kickchain.kickway.storing

data class Game(val leftTeam: Team, val rightTeam: Team) {
    data class Team(val players: List<String>, val score: Int)
}
