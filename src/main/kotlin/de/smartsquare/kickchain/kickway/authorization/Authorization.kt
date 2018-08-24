package de.smartsquare.kickchain.kickway.authorization

import org.springframework.data.annotation.Id

/**
 * @author Ruben Gees
 */
data class Authorization(
    @Id val deviceId: String,
    val name: String
)
