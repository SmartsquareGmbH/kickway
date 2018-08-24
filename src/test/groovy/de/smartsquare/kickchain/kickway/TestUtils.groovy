package de.smartsquare.kickchain.kickway

import org.mockserver.client.serialization.ObjectMapperFactory

/**
 * @author Ruben Gees
 */
class TestUtils {

    static String toJson(Object object) {
        return ObjectMapperFactory.createObjectMapper().writeValueAsString(object)
    }
}
