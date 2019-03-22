package de.smartsquare.kickchain.kickway.elo

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class EloRating(
    @EmbeddedId
    val team: Team,
    val elo: Int,
    val matches: Int
) : Serializable {
    constructor(firstPlayer: String, secondPlayer: String, elo: Int, matches: Int) : this(
        Team(firstPlayer, secondPlayer), elo, matches
    )
}