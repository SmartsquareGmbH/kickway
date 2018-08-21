package de.smartsquare.kickchain.kickway.analyzing

import de.smartsquare.kickchain.kickway.KickwayConfigurationProperties
import org.mockserver.integration.ClientAndServer
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static org.mockserver.matchers.Times.exactly
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class KickchainRepositorySpecification extends Specification {

    @Shared
    ClientAndServer server = new ClientAndServer()

    def repository

    def setup() {
        def config = new KickwayConfigurationProperties()
        config.kickchain.url = "http://localhost:$server.localPort"

        repository = new KickchainRepository(new RestTemplate(), config)
    }

    def cleanupSpec() {
        server.stop()
    }

    def 'repository returns blockchain from /chain'() {
        given:
        server.when(request()
                .withMethod("GET")
                .withPath("/chain"), exactly(1))
                .respond(response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json")
                .withBody(this.getClass().classLoader.getResource("blockchain.json").text))

        expect:
        repository.fetch().blocks.size() == 27
    }

    def 'repository throws kickchain exception with status code if its != 200'() {
        given:
        server.when(request()
                .withMethod("GET")
                .withPath("/chain"), exactly(1))
                .respond(response()
                .withStatusCode(500))

        when:
        repository.fetch()

        then:
        def error = thrown(KickchainException)
        error.message == 'org.springframework.web.client.HttpServerErrorException: 500 Internal Server Error'
    }

    def 'repository throws kickchain exception if the response is invalid'() {
        given:
        server.when(request()
                .withMethod("GET")
                .withPath("/chain"), exactly(1))
                .respond(response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json")
                .withBody(''))

        when:
        repository.fetch()

        then:
        def error = thrown(KickchainException)
        error.message == 'The kickchain response could not be parsed.'
    }

}