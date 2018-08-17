package de.smartsquare.kickchain.kickway.playing

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.hateoas.ResourceSupport

data class Game(val owner: String) : ResourceSupport() {

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

    @JsonIgnore
    fun isCompleted(): Boolean = completed

    @JsonIgnore
    fun rightTeamWon(): Boolean = teamRight.score == 10

    @JsonIgnore
    fun leftTeamWon(): Boolean = teamLeft.score == 10

    @JsonIgnore
    fun isFull(): Boolean = teamLeft.isFull() && teamRight.isFull()

    @JsonIgnore
    fun isEmpty(): Boolean = teamRight.isEmpty() && teamLeft.isEmpty()

}