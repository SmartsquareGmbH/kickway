package de.smartsquare.kickchain.kickway.analyzing

import org.springframework.stereotype.Service

@Service
class BlockchainAnalyzer(private val blockchainRepository: BlockchainRepository) {

    private fun findAllNonNullGames() = blockchainRepository.fetch().blocks.mapNotNull { it.games?.first() }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun List<Pair<String, Int>>.topTen() =
        this.groupBy { (playerName) -> playerName }
            .mapValues { (_, scores) -> scores.sumBy { (_, score) -> score } }
            .toList().sortedByDescending { (_, goals) -> goals }.take(10).map { it.first }

    fun findTopTenSoloQPlayers() = findAllNonNullGames()
        .filter { it.team1.players.size == 1 && it.team2.players.size == 1 }
        .flatMap {
            listOf(
                it.team1.players.first() to it.score.goals1,
                it.team2.players.first() to it.score.goals2
            )
        }.topTen()

    fun findTopTenDuoQPlayers() = findAllNonNullGames()
        .filter { it.team1.players.size == 2 && it.team2.players.size == 2 }
        .flatMap {
            listOf(
                it.team1.players.first() to it.score.goals1,
                it.team1.players[1] to it.score.goals1,
                it.team2.players.first() to it.score.goals2,
                it.team2.players[1] to it.score.goals2
            )
        }.topTen()

    fun findTopTenFlexQPlayers() = findAllNonNullGames()
        .filter {
            it.team1.players.size == 2 && it.team2.players.size == 1 ||
                    it.team1.players.size == 1 && it.team2.players.size == 2
        }
        .flatMap {
            if (it.team1.players.size == 2) {
                listOf(
                    it.team1.players.first() to it.score.goals1,
                    it.team1.players[1] to it.score.goals1,
                    it.team2.players.first() to it.score.goals2
                )
            } else {
                listOf(
                    it.team1.players.first() to it.score.goals1,
                    it.team2.players.first() to it.score.goals2,
                    it.team2.players[1] to it.score.goals2
                )
            }
        }.topTen()

    fun findStatisticsOfPlayer(name: String): PlayerStatistic {
        val games: List<Pair<String, Int>> = findAllNonNullGames()
            .flatMap {
                listOf(
                    it.team1.players.first() to it.score.goals1,
                    it.team2.players.first() to it.score.goals2
                )
            }
            .filter { (playerName) -> playerName == name }

        if (games.isEmpty()) {
            throw UnknownPlayerException(name)
        }

        val averageGoalsPerGame = games.sumBy { (_, goals) -> goals } / games.size
        val totalNumberOfWins = games.filter { (_, goals) -> goals == 10 }.size
        val totalNumberOfLosses = games.size - totalNumberOfWins
        val winRate: Double = totalNumberOfWins.toDouble().div(games.size)
        val totalCrawls = games.filter { (_, goals) -> goals == 0 }.size

        return PlayerStatistic(averageGoalsPerGame, winRate, totalCrawls, totalNumberOfWins, totalNumberOfLosses)
    }
}
