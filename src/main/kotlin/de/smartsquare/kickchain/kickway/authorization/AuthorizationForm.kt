package de.smartsquare.kickchain.kickway.authorization

import javax.validation.constraints.NotNull

/**
 * @author Ruben Gees
 */
data class AuthorizationForm(
    @field:NotNull val deviceId: String,
    @field:NotNull val name: String
)
