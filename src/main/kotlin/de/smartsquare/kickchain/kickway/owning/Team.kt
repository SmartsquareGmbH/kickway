package de.smartsquare.kickchain.kickway.owning

data class Team(val player: MutableList<String>, var score: Int) {

    constructor(owner: String) : this(mutableListOf(owner), 0)

    constructor() : this(mutableListOf(), 0)

    fun score() {
        score = score.inc()
    }

}