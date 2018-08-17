package de.smartsquare.kickchain.kickway.playing

interface GameRepository {

    fun save(game: Game): Int

}