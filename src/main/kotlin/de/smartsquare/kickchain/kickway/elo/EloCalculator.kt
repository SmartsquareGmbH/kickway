package de.smartsquare.kickchain.kickway.elo

import de.smartsquare.kickchain.kickway.Blockchain

/**
 * @see https://de.wikipedia.org/wiki/Elo-Zahl (Anpassung der Elo-Zahl)
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
 * @see https://de.wikipedia.org/wiki/Elo-Zahl (Anpassung der Elo-Zahl)
 */
fun Blockchain.Block.Game.matchFactor(firstPlayer: String, secondPlayer: String) =
    if (this.team1.players.containsAll(listOf(firstPlayer, secondPlayer)) && this.score.goals1 == 10) {
        1
    } else if (this.team2.players.containsAll(listOf(firstPlayer, secondPlayer)) && this.score.goals2 == 10) {
        1
    } else {
        0
    }

/**
 * @see https://de.wikipedia.org/wiki/Elo-Zahl (Anpassung der Elo-Zahl)
 */
fun EloRating.experienceFactor() = when {
    this.elo > 2400 -> 10
    this.matches < 30 -> 40
    else -> 20
}

/**
 * @see https://de.wikipedia.org/wiki/Elo-Zahl (Erwartungswert)
 */
infix fun EloRating.odds(opposite: EloRating) =
    (1 / (1 + Math.pow(10.0, (opposite.elo - this.elo) / 400))).roundToThreeDecimals()

private fun Double.roundToThreeDecimals() = Math.round(this * 1000.0) / 1000.0
