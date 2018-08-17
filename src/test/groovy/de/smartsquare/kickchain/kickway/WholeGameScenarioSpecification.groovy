package de.smartsquare.kickchain.kickway

import de.smartsquare.kickchain.kickway.playing.Server
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class WholeGameScenarioSpecification extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    Server server

    def 'spectate game after third goal'() {
        when:
        mockMvc.perform(post('/game/solo/Ballerbude/deen').header('raspberry', '141839841293'))

        mockMvc.perform(patch('/game/join/left/Ballerbude/ruby'))
        mockMvc.perform(patch('/game/join/right/Ballerbude/skonair'))
        mockMvc.perform(patch('/game/join/right/Ballerbude/drs'))

        3.times { mockMvc.perform(patch('/game/score/left/Ballerbude').header('raspberry', '141839841293')) }

        then:
        mockMvc.perform(get("/game/Ballerbude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.leftTeam.players[0]', is('deen')))
                .andExpect(jsonPath('$.leftTeam.players[1]', is('ruby')))
                .andExpect(jsonPath('$.rightTeam.players[0]', is('skonair')))
                .andExpect(jsonPath('$.rightTeam.players[1]', is('drs')))
                .andExpect(jsonPath('$.rightTeam.score', is(0)))
                .andExpect(jsonPath('$.leftTeam.score', is(3)))
                .andExpect(jsonPath('$.owner', is('deen')))

        cleanup:
        server.lobbies.clear()
    }

    def 'server removes lobby after game is over'() {
        when:
        mockMvc.perform(post('/game/solo/Ballerbude/deen').header('raspberry', '141839841293'))

        mockMvc.perform(patch('/game/join/left/Ballerbude/ruby'))
        mockMvc.perform(patch('/game/join/right/Ballerbude/skonair'))
        mockMvc.perform(patch('/game/join/right/Ballerbude/drs'))

        10.times { mockMvc.perform(patch('/game/score/left/Ballerbude').header('raspberry', '141839841293')) }


        then:
        mockMvc.perform(get("/game/Ballerbude"))
                .andExpect(status().isNotFound())

        cleanup:
        server.lobbies.clear()
    }

    def 'play soloq'() {
        when:
        mockMvc.perform(post('/game/solo/Ballerbude/deen').header('raspberry', '141839841293'))
        mockMvc.perform(patch('/game/join/right/Ballerbude/skonair'))

        3.times { mockMvc.perform(patch('/game/score/left/Ballerbude').header('raspberry', '141839841293')) }

        then:
        mockMvc.perform(get("/game/Ballerbude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.leftTeam.players[0]', is('deen')))
                .andExpect(jsonPath('$.rightTeam.players[0]', is('skonair')))
                .andExpect(jsonPath('$.rightTeam.score', is(0)))
                .andExpect(jsonPath('$.leftTeam.score', is(3)))
                .andExpect(jsonPath('$.owner', is('deen')))


        cleanup:
        server.lobbies.clear()
    }

}
