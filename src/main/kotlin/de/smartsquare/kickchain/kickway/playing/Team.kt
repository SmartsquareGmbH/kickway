package de.smartsquare.kickchain.kickway.playing

import com.fasterxml.jackson.annotation.JsonIgnore

data class Team(val players: List<String>, val score: Int) {

    constructor(owner: String) : this(listOf(owner), 0)

    constructor() : this(emptyList(), 0)

    operator fun plus(name: String): Team {
        if (name.isBlank()) throw IllegalArgumentException("A player must have a non-empty name")
        if (isFull()) throw TeamAlreadyFullException()
        if (players.contains(name)) throw PlayerAlreadyExistsException(name)

        return this.copy(players = players + name)
    }

    operator fun minus(name: String): Team = this.copy(players = players - name)

    fun score(): Team {
        return this.copy(score = score + 1)
    }

    @JsonIgnore
    fun isFull(): Boolean = players.size == 2

    @JsonIgnore
    fun isEmpty(): Boolean = players.isEmpty()

}