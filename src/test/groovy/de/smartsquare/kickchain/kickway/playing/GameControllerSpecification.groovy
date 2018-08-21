package de.smartsquare.kickchain.kickway.playing

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerSpecification extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    Server server

    @Autowired
    GameController gameController

    void cleanup() {
        server.lobbies.clear()
    }

    def 'rest endpoint adds new lobby on create'() {
        when:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))
                .andExpect(status().isCreated())

        then:
        server.lobbies['Ballerbude'].leftTeam.players == ['deen']
    }

    def 'rest endpoint authorizes raspberry for the lobby'() {
        when:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))
                .andExpect(status().isCreated())

        then:
        gameController.authorization['141839841293'] == 'Ballerbude'
    }

    def 'server responds with bad request if no raspberry id is given'() {
        expect:
        mockMvc.perform(post('/game/Ballerbude/deen'))
                .andExpect(status().isBadRequest())
    }

    def 'server responds conflict if the lobby already exists'() {
        given:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))

        expect:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))
                .andExpect(status().isConflict())

        cleanup:
        server.lobbies.clear()
    }

    def 'server adds link for joining the left team on create'() {
        expect:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$._links.joinLeft.href', is('http://localhost/game/join/left/Ballerbude/{playerName}')))
    }

    def 'server adds link for joining the right team on create'() {
        expect:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$._links.joinRight.href', is('http://localhost/game/join/right/Ballerbude/{playerName}')))
    }

    def 'server adds selfref on create'() {
        expect:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$._links.self.href', is('http://localhost/game/Ballerbude')))
    }

    def 'score left team'() {
        given:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))
        when:
        mockMvc.perform(patch('/game/score/left/Ballerbude')
                .header('raspberry', '141839841293'))

        then:
        server.lobbies['Ballerbude'].leftTeam.score == 1
    }

    def 'score left team will be denied if the raspberry id does not match'() {
        given:
        mockMvc.perform(patch('/game/Ballerbude/deen')
                .header('raspberry', '141839841293'))

        expect:
        mockMvc.perform(patch('/game/score/left/Ballerbude')
                .header('raspberry', '1337'))
                .andExpect(status().isUnauthorized())
    }

    def 'leave left team'() {
        given:
        mockMvc.perform(post('/game/Ballerbude/deen')
                .header('raspberry', '1337'))

        when:
        mockMvc.perform(patch('/game/join/left/Ballerbude/ruby'))
        mockMvc.perform(delete('/game/leave/Ballerbude/ruby'))

        then:
        server.lobbies['Ballerbude'].leftTeam.players == ['deen']
    }

    def 'server returns conflict if a player is already in game'() {
        when:
        mockMvc.perform(post('/game/Ballerbude/deen').header('raspberry', '141839841293'))

        then:
        mockMvc.perform(patch('/game/join/left/Ballerbude/deen'))
                .andExpect(status().isConflict())
                .andExpect(content().string("The player deen already joined the lobby"))
    }
}
