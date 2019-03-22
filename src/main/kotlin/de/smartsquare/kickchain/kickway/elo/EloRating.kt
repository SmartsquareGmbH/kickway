package de.smartsquare.kickchain.kickway.elo

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class EloRating(
    @EmbeddedId
    val team: Team,
    val elo: Double,
    val matches: Int
) : Serializable {
    constructor(firstPlayer: String, secondPlayer: String, elo: Double, matches: Int) : this(
        Team(firstPlayer, secondPlayer), elo, matches
    )

    companion object {
        const val serialVersionUID = 1L
    }
}
