package de.smartsquare.kickchain.kickway.elo

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class EloRating(
    @EmbeddedId
    val team: Team,
    val elo: Double,
    val matches: Int
) {
    constructor(firstPlayer: String, secondPlayer: String, elo: Double, matches: Int) : this(
        Team(firstPlayer, secondPlayer), elo, matches
    )
}
