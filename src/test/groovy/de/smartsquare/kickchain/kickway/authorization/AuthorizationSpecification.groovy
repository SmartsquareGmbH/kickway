package de.smartsquare.kickchain.kickway.authorization

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationSpecification extends Specification {

    @Autowired
    MockMvc mockMvc
    @Autowired
    AuthorizationRepository authorizationRepository

    def cleanup() { authorizationRepository.deleteAll() }

    def 'authorize'() {
        when:
        mockMvc.perform(post('/authorization')
                .header('name', 'deen')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))

        then:
        mockMvc.perform(get('/authorization')
                .header('name', 'deen')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))
                .andExpect(status().isOk())
    }

    def 'unauthorized request '() {
        expect:
        mockMvc.perform(get('/authorization')
                .header('name', 'deen')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))
                .andExpect(status().isUnauthorized())
    }

    def 'return bad request for get without name'() {
        expect:
        mockMvc.perform(get('/authorization')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))
                .andExpect(status().isBadRequest())
    }

    def 'return bad request for get get deviceId'() {
        expect:
        mockMvc.perform(get('/authorization')
                .header('name', 'deen'))
                .andExpect(status().isBadRequest())
    }


    def 'return bad request for get with blank name'() {
        expect:
        mockMvc.perform(get('/authorization')
                .header('name', '')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))
                .andExpect(status().isBadRequest())
    }


    def 'return bad request for get with blank deviceId'() {
        expect:
        mockMvc.perform(get('/authorization')
                .header('name', 'deen')
                .header('deviceId', ''))
                .andExpect(status().isBadRequest())
    }

    def 'return bad request for post with blank name'() {
        expect:
        mockMvc.perform(post('/authorization')
                .header('name', '')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))
                .andExpect(status().isBadRequest())
    }

    def 'return bad request for post with blank deviceId'() {
        expect:
        mockMvc.perform(post('/authorization')
                .header('name', 'deen')
                .header('deviceId', ''))
                .andExpect(status().isBadRequest())
    }

    def 'unauthorize'() {
        given:
        mockMvc.perform(post('/authorization')
                .header('name', 'deen')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))

        when:
        mockMvc.perform(delete('/authorization')
                .header('name', 'deen')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))

        then:
        mockMvc.perform(get('/authorization')
                .header('name', 'deen')
                .header('deviceId', 'xXx1337AndroIdPh0n3'))
                .andExpect(status().isUnauthorized())
    }
}
