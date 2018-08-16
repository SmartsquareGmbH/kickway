package de.smartsquare.kickchain.kickway.owning

interface GameRepository {

    fun save(game: Game): Int

}