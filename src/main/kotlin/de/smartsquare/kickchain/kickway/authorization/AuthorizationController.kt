package de.smartsquare.kickchain.kickway.authorization

import de.smartsquare.kickchain.kickway.authorization.Authorization.Companion.unauthorized
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorizationController(private val authorizationRepository: AuthorizationRepository) {

    @GetMapping("/authorization")
    fun isAuthorized(
        @RequestHeader("name") name: String,
        @RequestHeader("deviceId") deviceId: String
    ): ResponseEntity<Any> {
        if (name.isBlank().or(deviceId.isBlank())) return ResponseEntity.badRequest().build()

        return authorizationRepository.findByDeviceId(deviceId)
            .orElse(unauthorized())
            .let { if (it.name == name) ResponseEntity.ok() else ResponseEntity.status(UNAUTHORIZED) }.build()
    }

    @PostMapping("/authorization")
    fun authorize(
        @RequestHeader("name") name: String,
        @RequestHeader("deviceId") deviceId: String
    ): ResponseEntity<Any> {
        if (name.isBlank().or(deviceId.isBlank())) return ResponseEntity.badRequest().build()

        return if (authorizationRepository.findByName(name).isPresent) {
            ResponseEntity.status(UNAUTHORIZED)
        } else {
            authorizationRepository.save(Authorization(deviceId, name))
            ResponseEntity.ok()
        }.build()
    }

    @DeleteMapping("/authorization")
    fun unauthorize(
        @RequestHeader("name") name: String,
        @RequestHeader("deviceId") deviceId: String
    ): ResponseEntity<Any> =
        if (authorizationRepository.findByDeviceId(deviceId)
                .orElse(unauthorized())
                .name == name
        ) {
            authorizationRepository.deleteById(deviceId)
            ResponseEntity.ok()
        } else {
            ResponseEntity.status(UNAUTHORIZED)
        }.build()
}
