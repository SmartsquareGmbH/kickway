package de.smartsquare.kickchain.kickway.playing

data class Team(val player: MutableList<String>, var score: Int) {

    constructor(owner: String) : this(mutableListOf(owner), 0)

    constructor() : this(mutableListOf(), 0)

    fun score() {
        score = score.inc()
    }

    fun join(name: String) {
        if (name.none()) throw RuntimeException("A player must have a non-empty name")
        if (player.size == 2) throw RuntimeException("The team is full")
        if(player.contains(name)) throw RuntimeException("A player with the name ${name} is already in-game")

        player.add(name)
    }

    fun isFull(): Boolean = player.size == 2

    fun isEmpty(): Boolean = player.size == 0

    fun leave(name: String) {
        player.remove(name)
    }

}