package de.smartsquare.kickchain.kickway.elo

import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner::class)
class EloRatingRepositoryTest {

    @Autowired
    lateinit var repository: EloRatingRepository

    @Test
    fun `find elo rating by player names`() {
        val givenEloRating = repository.save(EloRating("deen", "ruby", 1000, 1))

        val returnedEloRating = repository.findEloRatingByTeamFirstAndTeamSecond(first = "deen", second = "ruby")

        returnedEloRating.isPresent shouldEqual true
        returnedEloRating.get() shouldEqual givenEloRating
    }

    @Test
    fun `find elo value by player names`() {
        repository.save(EloRating("deen", "ruby", 1337, 12))

        val elo = repository.findEloByPlayernames(first = "deen", second = "ruby")

        elo shouldEqual 1337
    }

    @Test
    fun `return default elo if the team has no match history`() {
        val elo = repository.findEloByPlayernames(first = "deen", second = "ruby")

        elo shouldEqual 1000
    }
}