package de.smartsquare.kickchain.kickway.playing

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.hateoas.ResourceSupport

data class Game(val leftTeam: Team, val rightTeam: Team) : ResourceSupport() {

    constructor(owner: String) : this(Team(owner), Team())

    @get:JsonIgnore
    val full
        get() = leftTeam.isFull() && rightTeam.isFull()

    @get:JsonIgnore
    val empty
        get() = rightTeam.isEmpty() && leftTeam.isEmpty()

    @get:JsonIgnore
    val rightTeamWon
        get() = rightTeam.score >= 10

    @get:JsonIgnore
    val leftTeamWon
        get() = leftTeam.score >= 10

    @get:JsonIgnore
    val completed = leftTeamWon || rightTeamWon

    val owner: String
        get() = leftTeam.players.firstOrNull()
            ?: rightTeam.players.firstOrNull()
            ?: throw IllegalStateException("No players found")

    fun scoreLeftTeam(): Game = if (completed.not()) {
        this.copy(leftTeam = leftTeam.score())
    } else {
        this
    }

    fun scoreRightTeam(): Game = if (completed.not()) {
        this.copy(rightTeam = rightTeam.score())
    } else {
        this
    }

    fun joinLeftTeam(name: String): Game {
        if (rightTeam hasPlayer name) throw PlayerAlreadyExistsException(name)
        return this.copy(leftTeam = leftTeam + name)
    }

    fun joinRightTeam(name: String): Game {
        if (leftTeam hasPlayer name) throw PlayerAlreadyExistsException(name)
        return this.copy(rightTeam = rightTeam + name)
    }

    fun leaveLeftTeam(name: String) = this.copy(leftTeam = leftTeam - name)

    fun leaveRightTeam(name: String) = this.copy(rightTeam = rightTeam - name)
}
