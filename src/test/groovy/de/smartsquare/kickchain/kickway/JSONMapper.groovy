package de.smartsquare.kickchain.kickway

import org.mockserver.client.serialization.ObjectMapperFactory

/**
 * @author Ruben Gees
 */
final class JSONMapper {

    private JSONMapper() {}

    static String toJson(Object object) {
        return ObjectMapperFactory.createObjectMapper().writeValueAsString(object)
    }
}
