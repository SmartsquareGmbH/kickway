package de.smartsquare.kickchain.kickway.authorization

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AuthorizationRepository : CrudRepository<Authorization, String> {

    fun findByName(name: String): Optional<Authorization>
    fun findByDeviceIdAndName(id: String, name: String): Optional<Authorization>
}
