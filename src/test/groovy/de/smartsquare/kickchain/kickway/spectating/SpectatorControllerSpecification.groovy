package de.smartsquare.kickchain.kickway.spectating

import de.smartsquare.kickchain.kickway.playing.Game
import de.smartsquare.kickchain.kickway.playing.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.is
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SpectatorControllerSpecification extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    Server server

    void cleanup() {
        server.lobbies.clear()
    }

    def 'controller returns http not found if lobby doesnt exists'() {
        expect:
        mockMvc.perform(get('/game/abc')).andExpect(status().isNotFound())
    }

    def 'controller returns necessary game information from server'() {
        given:
        server.lobbies['Ballerbude'] = new Game('deen')

        expect:
        mockMvc.perform(get('/game/Ballerbude').accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.rightTeam.players.length()', is(0)))
                .andExpect(jsonPath('$.leftTeam.players[0]', is('deen')))
                .andExpect(jsonPath('$.rightTeam.score', is(0)))
                .andExpect(jsonPath('$.leftTeam.score', is(0)))
                .andExpect(jsonPath('$.owner', is('deen')))
    }
}
