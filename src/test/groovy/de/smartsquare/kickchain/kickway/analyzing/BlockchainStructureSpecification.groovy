package de.smartsquare.kickchain.kickway.analyzing

import org.mockserver.integration.ClientAndServer
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static org.mockserver.matchers.Times.exactly
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class BlockchainStructureSpecification extends Specification {

    @Shared
    ClientAndServer server = new ClientAndServer()
    def client = new RestTemplate()

    def setupSpec() {
        server.when(request()
                .withMethod("GET")
                .withPath("/chain"), exactly(1))
                .respond(response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json")
                .withBody(this.getClass().classLoader.getResource("blockchain.json").text))
    }

    def cleanupSpec() {
        server.stop()
    }

    def 'parse blockchain from json'() {
        expect:
        client.getForObject("http://localhost:${server.localPort}/chain", Blockchain.class).blocks.size() == 10
    }
}
