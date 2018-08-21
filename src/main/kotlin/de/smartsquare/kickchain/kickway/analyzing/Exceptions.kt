package de.smartsquare.kickchain.kickway.analyzing

class KickchainException(message: String) : RuntimeException(message)

class UnknownPlayerException(val name: String) : RuntimeException() {
    override val message: String?
        get() = "The kickchain contains no games associated with the player $name"
}
