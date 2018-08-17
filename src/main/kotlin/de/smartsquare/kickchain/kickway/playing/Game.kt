package de.smartsquare.kickchain.kickway.playing

data class Game(val owner: String) {
    val teamLeft = Team(owner)
    val teamRight = Team()

    private var completed = false

    fun scoreTeamLeft() {
        if (completed.not()) {
            teamLeft.score()
        }

        if (leftTeamWon()) {
            completed = true
        }
    }

    fun scoreTeamRight() {
        if (completed.not()) {
            teamRight.score()
        }

        if (rightTeamWon()) {
            completed = true
        }
    }

    fun isCompleted(): Boolean = completed

    fun rightTeamWon(): Boolean = teamRight.score == 10

    fun leftTeamWon(): Boolean = teamLeft.score == 10

    fun isFull(): Boolean = teamLeft.isFull() && teamRight.isFull()

}