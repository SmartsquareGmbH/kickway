package de.smartsquare.kickchain.kickway.analyzing

import org.springframework.stereotype.Service

@Service
class BlockchainAnalyzer(private val blockchainRepository: BlockchainRepository) {

    private fun findAllNonNullGames() = blockchainRepository.fetch().blocks.mapNotNull { it.games?.firstOrNull() }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun List<GameParticipant>.topTen() = this.groupBy { (playerName) -> playerName }
        .mapValues { (_, participants) ->
            participants.sumBy { participant -> if (participant.won) 1 else 0 } to
                participants.sumBy { it.goals }
        }
        .toList()
        .sortedWith(
            compareByDescending<Pair<String, Pair<Int, Int>>> { (_, score) -> score.first }
                .thenByDescending { (_, score) -> score.second }
        )
        .take(10)
        .map { (name, score) -> Player(name, score.first, score.second) }

    fun findTopTenSoloQPlayers() = findAllNonNullGames()
        .filter { it.team1.players.size == 1 && it.team2.players.size == 1 }
        .flatMap {
            listOf(
                GameParticipant(it.team1.players.first(), it.score.goals1),
                GameParticipant(it.team2.players.first(), it.score.goals2)
            )
        }.topTen()

    fun findTopTenDuoQPlayers() = findAllNonNullGames()
        .filter { it.team1.players.size == 2 && it.team2.players.size == 2 }
        .flatMap {
            listOf(
                GameParticipant(it.team1.players.first(), it.score.goals1),
                GameParticipant(it.team1.players[1], it.score.goals1),
                GameParticipant(it.team2.players.first(), it.score.goals2),
                GameParticipant(it.team2.players[1], it.score.goals2)
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
                    GameParticipant(it.team1.players.first(), it.score.goals1),
                    GameParticipant(it.team1.players[1], it.score.goals1),
                    GameParticipant(it.team2.players.first(), it.score.goals2)
                )
            } else {
                listOf(
                    GameParticipant(it.team1.players.first(), it.score.goals1),
                    GameParticipant(it.team2.players.first(), it.score.goals2),
                    GameParticipant(it.team2.players[1], it.score.goals2)
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

    private data class GameParticipant(val name: String, val goals: Int, val won: Boolean = goals >= 10)
}
