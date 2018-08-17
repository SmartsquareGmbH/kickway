package de.smartsquare.kickchain.kickway.analyzing

import org.mockserver.integration.ClientAndServer
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

import static org.mockserver.integration.ClientAndServer.startClientAndServer
import static org.mockserver.matchers.Times.exactly
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class BlockchainStructureSpecification extends Specification {

    def kickchain = new RestTemplate()

    @Shared
    ClientAndServer clientAndServer = startClientAndServer(8111)

    def setupSpec() {
        clientAndServer.when(request()
                .withMethod("GET")
                .withPath("/chain"), exactly(1))
                .respond(response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json")
                .withBody(readBlockchainJSONFromTestResources()))
    }

    def readBlockchainJSONFromTestResources() {
        URL blockchainURL = this.getClass().getResource("/blockchain.json")
        URI blockchainURI = blockchainURL.toURI()
        Path blockchainPath = new File(blockchainURI).toPath()
        return Files.readAllBytes(blockchainPath)
    }

    def 'parse blockchain from json'() {
        expect:
        kickchain.getForObject("http://localhost:8111/chain", Blockchain.class).blocks.size() == 10
    }


    def cleanupSpec() {
        clientAndServer.stop()
    }

}
