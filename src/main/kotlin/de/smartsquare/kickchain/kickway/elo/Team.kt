package de.smartsquare.kickchain.kickway.elo

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
data class Team(val first: String, val second: String) : Serializable