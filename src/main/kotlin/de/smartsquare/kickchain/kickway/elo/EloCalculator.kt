package de.smartsquare.kickchain.kickway.elo

import de.smartsquare.kickchain.kickway.Blockchain
/*
/**
 * @see https://de.wikipedia.org/wiki/Elo-Zahl
 */
fun Blockchain.Block.Game.adjustedEloScore(rating: EloRating): AdjustedEloScore {
    AdjustedEloScore(null, null)
}*/

/**
 * @see https://de.wikipedia.org/wiki/Elo-Zahl
 */
infix fun EloRating.odds(opposite: EloRating) =
    (1 / (1 + Math.pow(10.0, (this.elo - opposite.elo.toDouble()) / 400))).roundToThreeDecimals()

private fun Double.roundToThreeDecimals() = Math.round(this * 1000.0) / 1000.0