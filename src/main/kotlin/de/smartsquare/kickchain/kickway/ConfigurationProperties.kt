package de.smartsquare.kickchain.kickway

import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.properties.Delegates.notNull

@Validated
@Component
@ConfigurationProperties(prefix = "kickway")
class ConfigurationProperties {

    @Valid
    @NotNull
    var kickchain = Kickchain()

    class Kickchain {

        val authorization = Authorization()

        @get:URL
        @get:NotBlank
        var url by notNull<String>()

        class Authorization {

            @get:NotBlank
            var name by notNull<String>()

            @get:NotBlank
            var password by notNull<String>()
        }
    }
}
