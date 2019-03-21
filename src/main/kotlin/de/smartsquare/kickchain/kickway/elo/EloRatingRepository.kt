package de.smartsquare.kickchain.kickway.elo

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EloRatingRepository : CrudRepository<EloRating, Long> {

    fun findEloRatingByTeamFirstAndTeamSecond(first: String, second: String): Optional<EloRating>

    @JvmDefault
    fun findEloByPlayernames(first: String, second: String) = this.findEloRatingByTeamFirstAndTeamSecond(first, second)
        .map { it.elo }
        .orElse(1000)
}