package de.smartsquare.kickchain.kickway.elo

import de.smartsquare.kickchain.kickway.Blockchain
import de.smartsquare.kickchain.kickway.Blockchain.Block.Game.Score
import de.smartsquare.kickchain.kickway.Blockchain.Block.Game.Team
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldEqual
import org.junit.Test

class EloCalculatorTest {

    @Test
    fun `deen and drs should probably loose against ruby and skonair`() {
        val deen_and_drs = EloRating("deen", "drs", 2577.0, 1)
        val ruby_and_skonair = EloRating("ruby", "skonair", 2806.0, 1)

        val odds = deen_and_drs odds ruby_and_skonair

        odds shouldEqual 0.211
    }

    @Test
    fun `danielr and alexn should probably win against saschar and ruby`() {
        val danielr_and_alexn = EloRating("danielr", "alexn", 2577.0, 1)
        val saschar_and_ruby = EloRating("saschar", "ruby", 2300.0, 1)

        val odds = danielr_and_alexn odds saschar_and_ruby

        odds shouldEqual 0.831
    }

    @Test
    fun `match between ruby and deen vs skonair and lena probably results in a draw`() {
        val ruby_and_deen = EloRating("ruby", "deen", 2577.0, 1)
        val skonair_and_lena = EloRating("skonair", "lena", 2577.0, 1)

        val odds = ruby_and_deen odds skonair_and_lena

        odds shouldEqual 0.5
    }

    @Test
    fun `experience factor is 20 by default`() {
        val ruby_and_deen = EloRating("ruby", "deen", 2300.0, 90)

        val experienceFactor = ruby_and_deen.experienceFactor()

        experienceFactor shouldEqual 20
    }

    @Test
    fun `top players experience factor equals 10`() {
        val ruby_and_deen = EloRating("ruby", "deen", 3365.0, 90)

        val experienceFactor = ruby_and_deen.experienceFactor()

        experienceFactor shouldEqual 10
    }

    @Test
    fun `newbies experience factor equals 40`() {
        val ruby_and_deen = EloRating("ruby", "deen", 1200.0, 2)

        val experienceFactor = ruby_and_deen.experienceFactor()

        experienceFactor shouldEqual 40
    }

    @Test
    fun `match factor of a win equals 1`() {
        val game = Blockchain.Block.Game(
            Team(listOf("deen", "ruby")),
            Team(listOf("skonair", "lena")),
            Score(10, 2)
        )

        val matchFactor = game.matchFactor("deen")

        matchFactor shouldEqual 1
    }

    @Test
    fun `match factor of a loss equals 0`() {
        val game = Blockchain.Block.Game(
            Team(listOf("deen", "ruby")),
            Team(listOf("skonair", "lena")),
            Score(10, 2)
        )

        val matchFactor = game.matchFactor("skonair")

        matchFactor shouldEqual 0
    }

    @Test
    fun `elo is redistributed`() {
        val game = Blockchain.Block.Game(
            Team(listOf("deen", "ruby")),
            Team(listOf("skonair", "lena")),
            Score(10, 2)
        )
        val oldRatingFirstTeam = EloRating("deen", "ruby", 2563.0, 45)
        val oldRatingSecondTeam = EloRating("skonair", "lena", 1812.0, 23)

        val (newRatingFirstTeam, newRatingSecondTeam) = game.readjust(oldRatingFirstTeam, oldRatingSecondTeam)

        oldRatingFirstTeam.elo + oldRatingSecondTeam.elo shouldEqual newRatingFirstTeam.elo + newRatingSecondTeam.elo
    }

    @Test
    fun `increase winner elo`() {
        val game = Blockchain.Block.Game(
            Team(listOf("deen", "ruby")),
            Team(listOf("skonair", "lena")),
            Score(10, 2)
        )
        val oldWinnersRating = EloRating("deen", "ruby", 2563.0, 45)
        val oldLoosersRating = EloRating("skonair", "lena", 1812.0, 23)

        val (newWinnersTeam, _) = game.readjust(oldWinnersRating, oldLoosersRating)

        newWinnersTeam.elo shouldBeGreaterThan oldWinnersRating.elo
    }

    @Test
    fun `decrease looser elo`() {
        val game = Blockchain.Block.Game(
            Team(listOf("deen", "ruby")),
            Team(listOf("skonair", "lena")),
            Score(10, 2)
        )
        val oldWinnersRating = EloRating("deen", "ruby", 2563.0, 45)
        val oldLoosersRating = EloRating("skonair", "lena", 1812.0, 23)

        val (_, newLoosersRating) = game.readjust(oldWinnersRating, oldLoosersRating)

        newLoosersRating.elo shouldBeLessThan oldLoosersRating.elo
    }
}