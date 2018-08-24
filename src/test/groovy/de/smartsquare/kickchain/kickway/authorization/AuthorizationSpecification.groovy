package de.smartsquare.kickchain.kickway.authorization

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.lang.Unroll

import static de.smartsquare.kickchain.kickway.TestUtils.toJson
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationSpecification extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    AuthorizationRepository authorizationRepository

    def cleanup() { authorizationRepository.deleteAll() }

    def 'create authorization'() {
        given:
        AuthorizationForm input = new AuthorizationForm('xXx1337AndroIdPh0n3', 'deen')

        when:
        def result = mockMvc.perform(post('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))

        then:
        result.andExpect(status().isOk())
        authorizationRepository.findById('xXx1337AndroIdPh0n3').get().name == 'deen'
    }

    def 'create authorization with existing name'() {
        setup:
        authorizationRepository.save(new Authorization('xXx1337AndroIdPh0n3', 'deen'))
        AuthorizationForm input = new AuthorizationForm('IoSpHoNe', 'deen')

        when:
        def result = mockMvc.perform(post('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))

        then:
        result.andExpect(status().isConflict())
        !authorizationRepository.findById('IoSpHoNe').isPresent()
    }

    def 'update authorization'() {
        setup:
        authorizationRepository.save(new Authorization('xXx1337AndroIdPh0n3', 'deen'))
        AuthorizationForm input = new AuthorizationForm('xXx1337AndroIdPh0n3', 'ruby')

        when:
        def result = mockMvc.perform(post('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))

        then:
        result.andExpect(status().isOk())
        authorizationRepository.findById('xXx1337AndroIdPh0n3').get().name == 'ruby'
    }

    def 'update authorization with existing name'() {
        setup:
        authorizationRepository.save(new Authorization('xXx1337AndroIdPh0n3', 'deen'))
        authorizationRepository.save(new Authorization('IoSpHoNe', 'ruby'))
        AuthorizationForm input = new AuthorizationForm('xXx1337AndroIdPh0n3', 'ruby')

        when:
        def result = mockMvc.perform(post('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))

        then:
        result.andExpect(status().isConflict())
        authorizationRepository.findById('xXx1337AndroIdPh0n3').get().name == 'deen'
    }

    def 'validate authorization'() {
        setup:
        authorizationRepository.save(new Authorization('xXx1337AndroIdPh0n3', 'deen'))
        AuthorizationForm input = new AuthorizationForm('xXx1337AndroIdPh0n3', 'deen')

        expect:
        mockMvc.perform(post('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))
                .andExpect(status().isOk())
    }

    @Unroll
    def 'return bad request for authorize request with invalid values'() {
        given:
        AuthorizationForm input = new AuthorizationForm(name, id)

        expect:
        mockMvc.perform(post('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))
                .andExpect(status().isBadRequest())

        where:
        name   | id
        ''     | 'xXx1337AndroIdPh0n3'
        'deen' | ''
    }

    def 'unauthorize'() {
        setup:
        authorizationRepository.save(new Authorization('xXx1337AndroIdPh0n3', 'deen'))
        AuthorizationForm input = new AuthorizationForm('xXx1337AndroIdPh0n3', 'deen')

        when:
        def result = mockMvc.perform(delete('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))

        then:
        result.andExpect(status().isOk())
        !authorizationRepository.findById('xXx1337AndroIdPh0n3').isPresent()
    }

    @Unroll
    def 'authorize with unknown values'() {
        setup:
        authorizationRepository.save(new Authorization('xXx1337AndroIdPh0n3', 'deen'))
        AuthorizationForm input = new AuthorizationForm(name, id)

        expect:
        mockMvc.perform(delete('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))
                .andExpect(status().isUnauthorized())

        where:
        name   | id
        'ruby' | 'xXx1337AndroIdPh0n3'
        'deen' | 'IoSpHoNe'
    }

    @Unroll
    def 'return bad request for unauthorize request with invalid values'() {
        given:
        AuthorizationForm input = new AuthorizationForm(name, id)

        expect:
        mockMvc.perform(delete('/authorization')
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))
                .andExpect(status().isBadRequest())

        where:
        name   | id
        ''     | 'xXx1337AndroIdPh0n3'
        'deen' | ''
    }
}
