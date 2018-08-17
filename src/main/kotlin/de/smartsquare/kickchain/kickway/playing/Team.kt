package de.smartsquare.kickchain.kickway.playing

import com.fasterxml.jackson.annotation.JsonIgnore

data class Team(val player: MutableList<String>, var score: Int) {

    constructor(owner: String) : this(mutableListOf(owner), 0)

    constructor() : this(mutableListOf(), 0)

    fun score() {
        score = score.inc()
    }

    fun join(name: String) {
        if (name.none()) throw IllegalArgumentException("A player must have a non-empty name")
        if (player.size == 2) throw TeamAlreadyFullException()
        if (player.contains(name)) throw PlayerAlreadyExistsException(name)

        player.add(name)
    }

    @JsonIgnore
    fun isFull(): Boolean = player.size == 2

    @JsonIgnore
    fun isEmpty(): Boolean = player.size == 0

    fun leave(name: String) {
        player.remove(name)
    }

}