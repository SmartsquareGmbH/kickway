package de.smartsquare.kickchain.kickway.elo

import org.amshove.kluent.shouldEqual
import org.junit.Test

class EloCalculatorTest {

    @Test
    fun `deen and drs should probably win against ruby and skonair`() {
        val deen_and_drs = EloRating("deen", "drs", 2577, 1)
        val ruby_and_skonair = EloRating("ruby", "skonair", 2806, 1)

        val odds = deen_and_drs odds ruby_and_skonair

        odds shouldEqual 0.789
    }

    @Test
    fun `danielr and alexn should probably win against saschar and ruby`() {
        val danielr_and_alexn = EloRating("danielr", "alexn", 2577, 1)
        val saschar_and_ruby = EloRating("saschar", "ruby", 2300, 1)

        val odds = danielr_and_alexn odds saschar_and_ruby

        odds shouldEqual 0.169
    }

    @Test
    fun `match between ruby and deen vs skonair and lena probably results in a draw`() {
        val ruby_and_deen = EloRating("ruby", "deen", 2577, 1)
        val skonair_and_lena = EloRating("skonair", "lena", 2577, 1)

        val odds = ruby_and_deen odds skonair_and_lena

        odds shouldEqual 0.5
    }
}