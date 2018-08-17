package de.smartsquare.kickchain.kickway.playing

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.is
import static org.springframework.http.MediaType.TEXT_PLAIN
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

    def 'rest endpoint adds new lobby'() {
        when:
        mockMvc.perform(post("/game/solo/Ballerbude/deen")
                .content("141839841293")
                .contentType(TEXT_PLAIN)
                .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())

        then:
        server.lobbies['Ballerbude'].teamLeft.player == ['deen']

        cleanup:
        server.lobbies.clear()
    }

    def 'rest endpoint authorizes raspberry for the lobby'() {
        when:
        mockMvc.perform(post("/game/solo/Ballerbude/deen")
                .content("141839841293")
                .contentType(TEXT_PLAIN)
                .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())

        then:
        gameController.authorization['141839841293'] == 'Ballerbude'

        cleanup:
        server.lobbies.clear()
    }

    def 'server responds with bad request if no raspberry id is given'() {
        expect:
        mockMvc.perform(post("/game/solo/Ballerbude/deen"))
                .andExpect(status().is(400))
    }

    def 'server responds conflict if the lobby already exists'() {
        given:
        mockMvc.perform(post("/game/solo/Ballerbude/deen")
                .content("141839841293")
                .contentType(TEXT_PLAIN)
                .characterEncoding("UTF-8"))

        expect:
        mockMvc.perform(post("/game/solo/Ballerbude/deen")
                .content("141839841293")
                .contentType(TEXT_PLAIN))
                .andExpect(status().isConflict())

        cleanup:
        server.lobbies.clear()
    }

    def 'server adds link for joining the left team'() {
        expect:
        mockMvc.perform(post("/game/solo/Ballerbude/deen")
                .content("141839841293")
                .contentType(TEXT_PLAIN))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$._links.joinLeft.href', is('http://localhost/game/join/left/Ballerbude/{playerName}')))


        cleanup:
        server.lobbies.clear()
    }

    def 'server adds link for joining the right team'() {
        expect:
        mockMvc.perform(post("/game/solo/Ballerbude/deen")
                .content("141839841293")
                .contentType(TEXT_PLAIN))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$._links.joinRight.href', is('http://localhost/game/join/right/Ballerbude/{playerName}')))

        cleanup:
        server.lobbies.clear()
    }

    def 'server adds selfref'() {
        expect:
        mockMvc.perform(post("/game/solo/Ballerbude/deen")
                .content("141839841293")
                .contentType(TEXT_PLAIN))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$._links.self.href', is('http://localhost/game/Ballerbude')))

        cleanup:
        server.lobbies.clear()
    }


}
