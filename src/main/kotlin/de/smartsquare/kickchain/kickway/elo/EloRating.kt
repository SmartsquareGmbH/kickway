package de.smartsquare.kickchain.kickway.elo

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity
data class EloRating(
    @EmbeddedId
    val team: Team,
    val elo: Int
) : Serializable