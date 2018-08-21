package de.smartsquare.kickchain.kickway.analyzing

class KickchainException : RuntimeException {
    constructor(cause: Exception) : super(cause)
    constructor(message: String) : super(message)
}

class UnknownPlayerException(val name: String) : RuntimeException() {
    override val message: String?
        get() = "The kickchain contains no games associated with the player $name"
}
