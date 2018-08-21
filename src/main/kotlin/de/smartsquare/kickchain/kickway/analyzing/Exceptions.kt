package de.smartsquare.kickchain.kickway.analyzing

@Suppress("UtilityClassWithPublicConstructor")
class KickchainException : RuntimeException {
    constructor(cause: Exception, message: String) : super(message, cause)
    constructor(message: String) : super(message)
}

class UnknownPlayerException(val name: String) : RuntimeException() {
    override val message: String?
        get() = "The kickchain contains no games associated with the player $name"
}
