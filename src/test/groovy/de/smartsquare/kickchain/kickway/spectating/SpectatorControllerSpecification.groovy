package de.smartsquare.kickchain.kickway.spectating

import de.smartsquare.kickchain.kickway.playing.Game
import de.smartsquare.kickchain.kickway.playing.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SpectatorControllerSpecification extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    Server server

    def 'controller returns http not found if lobby doesnt exists'() {
        expect:
        mockMvc.perform(get("/game/abc")).andExpect(status().isNotFound())
    }

    def 'controller returns necessary game information from server'() {
        given:
        server.lobbies['Ballerbude'] = new Game('deen')

        expect:
        mockMvc.perform(get("/game/Ballerbude").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json('{"owner":"deen","teamLeft":{"player":["deen"],"score":0},"teamRight":{"player":[],"score":0}}'))
    }

}
