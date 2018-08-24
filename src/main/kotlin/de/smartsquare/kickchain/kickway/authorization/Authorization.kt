package de.smartsquare.kickchain.kickway.authorization

import org.springframework.data.annotation.Id
import org.springframework.data.keyvalue.annotation.KeySpace

@KeySpace
data class Authorization(
    @Id
    val deviceId: String,
    val name: String
) {
    companion object {
        fun unauthorized() = Authorization("", "")
    }
}
