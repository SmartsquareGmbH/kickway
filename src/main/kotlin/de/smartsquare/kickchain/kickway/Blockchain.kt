package de.smartsquare.kickchain.kickway

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

data class Blockchain(@JsonProperty("chain") @Valid val blocks: List<Block>) {

    data class Block(@JsonProperty("content") val games: List<Game>?) {

        data class Game(val team1: Team, val team2: Team, val score: Score) {

            data class Team(val players: List<String>)

            data class Score(val goals1: Int, val goals2: Int)
        }
    }
}
