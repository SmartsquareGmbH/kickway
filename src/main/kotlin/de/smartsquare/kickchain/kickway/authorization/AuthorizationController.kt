package de.smartsquare.kickchain.kickway.authorization

import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class AuthorizationController(private val authorizationRepository: AuthorizationRepository) {

    @PostMapping("/authorization")
    fun authorize(
        @Valid @RequestBody input: AuthorizationForm,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build()
        }

        val authorizationWithSameName: Authorization? = authorizationRepository.findByName(input.name).orElse(null)

        return if (authorizationWithSameName != null) {
            if (authorizationWithSameName.deviceId == input.deviceId) {
                ResponseEntity.ok().build()
            } else {
                ResponseEntity.status(CONFLICT).build()
            }
        } else {
            authorizationRepository.save(Authorization(input.deviceId, input.name))

            ResponseEntity.ok().build()
        }
    }

    @DeleteMapping("/authorization")
    fun unauthorize(
        @Valid @RequestBody input: AuthorizationForm,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build()
        }

        return if (authorizationRepository.findByDeviceIdAndName(input.deviceId, input.name).isPresent) {
            authorizationRepository.deleteById(input.deviceId)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(UNAUTHORIZED).build()
        }
    }
}
