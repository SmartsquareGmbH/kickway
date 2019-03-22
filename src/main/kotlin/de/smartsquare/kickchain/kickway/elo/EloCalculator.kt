package de.smartsquare.kickchain.kickway.elo

import de.smartsquare.kickchain.kickway.Blockchain
import java.math.BigDecimal
import java.math.MathContext

/**
 * This method is used to recalculate the elo scores after a match has been played.
 *
 * @return a copy of the both EloRating with an adjusted elo score.
 * @see https://en.wikipedia.org/wiki/Elo_rating_system
 */
fun Blockchain.Block.Game.readjust(one: EloRating, other: EloRating): AdjustedEloScore {
    val newEloOne =
        one.elo + one.experienceFactor() * (this.matchFactor(one.team.first, one.team.second) - (one odds other))
    val newEloOther = other.elo + (one.elo - newEloOne)

    return AdjustedEloScore(
        one.copy(matches = one.matches + 1, elo = newEloOne),
        other.copy(matches = other.matches + 1, elo = newEloOther)
    )
}

/**
 * The match factor is part of the elo formula.
 *
 * @return one in case of a victory or zero in case of a defeat.
 * @see https://en.wikipedia.org/wiki/Elo_rating_system
 */
fun Blockchain.Block.Game.matchFactor(firstPlayer: String, secondPlayer: String) =
    when {
        this.team1.players.containsAll(listOf(firstPlayer, secondPlayer)) && this.score.goals1 == 10 -> 1
        this.team2.players.containsAll(listOf(firstPlayer, secondPlayer)) && this.score.goals2 == 10 -> 1
        else -> 0
    }

/**
 * The experience factor is part of the elo formula to treat the different player levels.
 *
 * @return 10 in case of a top player, 20 by default and 40 in case of a newbie.
 * @see https://en.wikipedia.org/wiki/Elo_rating_system
 */
fun EloRating.experienceFactor() = when {
    this.elo > 2400 -> 10
    this.matches < 30 -> 40
    else -> 20
}

/**
 * The odds is part of the elo formula and is used to treat the skill gap.
 *
 * @return the probability for the instance to defeat the opposite.
 * @see https://en.wikipedia.org/wiki/Elo_rating_system
 */
infix fun EloRating.odds(opposite: EloRating) =
    BigDecimal((1 / (1 + Math.pow(10.0, (opposite.elo - this.elo) / 400)))).round(MathContext(3)).toDouble()