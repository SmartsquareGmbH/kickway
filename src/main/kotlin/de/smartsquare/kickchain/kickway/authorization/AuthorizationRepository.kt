package de.smartsquare.kickchain.kickway.authorization

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AuthorizationRepository : CrudRepository<Authorization, String> {

    fun findByDeviceId(deviceId: String): Optional<Authorization>
    fun findByName(name: String): Optional<Authorization>
}
