package de.smartsquare.kickchain.kickway

import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.properties.Delegates

@Validated
@Component
@ConfigurationProperties(prefix = "kickway")
class KickwayConfigurationProperties {

    @Valid
    @NotNull
    var kickchain = Kickchain()

    class Kickchain {

        val authorization = Authorization()

        @get:URL
        @get:NotBlank
        var url by Delegates.notNull<String>()


        class Authorization {

            @get:NotBlank
            var name by Delegates.notNull<String>()

            @get:NotBlank
            var password by Delegates.notNull<String>()
        }
    }
}
