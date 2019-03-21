package de.smartsquare.kickchain.kickway.elo

import de.smartsquare.kickchain.kickway.Blockchain

infix fun Blockchain.Block.Game.calculate(game: Blockchain.Block.Game): AdjustedEloScore {
    return AdjustedEloScore(1, 2)
}

infix fun Blockchain.Block.Game.Team.odds(opposite: Blockchain.Block.Game.Team) =
    (1 / (1 + Math.pow(10.0, (this.elo - opposite.elo.toDouble()) / 400))).roundToThreeDecimals()

private fun Double.roundToThreeDecimals() = Math.round(this * 1000.0) / 1000.0