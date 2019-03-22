package de.smartsquare.kickchain.kickway.elo

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class EloRating(
    @EmbeddedId
    val team: Team,
    val elo: Double = 1000.0,
    val matches: Int = 0
) {
    constructor(firstPlayer: String, secondPlayer: String, elo: Double, matches: Int) : this(
        Team(firstPlayer, secondPlayer), elo, matches
    )
    constructor(firstPlayer: String, secondPlayer: String) : this(Team(firstPlayer, secondPlayer))
    constructor(player: String) : this(Team(player))
}
