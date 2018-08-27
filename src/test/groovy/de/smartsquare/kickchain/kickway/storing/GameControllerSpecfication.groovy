package de.smartsquare.kickchain.kickway.storing

import de.smartsquare.kickchain.kickway.analyzing.Blockchain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerSpecfication extends Specification {

    @Autowired
    MockMvc mockMvc
    @MockBean
    GameRepository gameRepository

    def 'unfinished game is a bad request'() {
        given:
        def unfinishedGame = """
                {
                    "team1" : { "players" : ["Deen", "Ruben"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "9" , "goals2" : "9" }
                }
        """
        expect:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(unfinishedGame))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Unfinished games cannot be persisted"))

    }

    def 'game wih eleven goals is a bad request'() {
        given:
        def gameWithElevenGoalsInLeftTeam = """
                {
                    "team1" : { "players" : ["Deen", "Ruben"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "11" , "goals2" : "10" }
                }
        """
        expect:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(gameWithElevenGoalsInLeftTeam))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A team cannot score more than ten goals"))

    }

    def 'game with empty player name is a bad request'() {
        given:
        def gameWithEmptyPlayerName = """
                {
                    "team1" : { "players" : ["", "Ruben"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "11" , "goals2" : "10" }
                }
        """
        expect:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(gameWithEmptyPlayerName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A player name cannot be empty"))
    }

    def 'game with scores 10 and -1 is bad request'() {
        given:
        def gameWithNegativeScoreInRightTeam = """
                {
                    "team1" : { "players" : ["Deen", "Ruben"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "10" , "goals2" : "-1" }
                }
        """
        expect:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(gameWithNegativeScoreInRightTeam))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A team cannot score minus goals"))
    }

    def 'score game'() {
        given:
        def game = """
                {
                    "team1" : { "players" : ["Deen", "Ruben"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "10" , "goals2" : "1" }
                }
        """
        when:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(game))

        then:
        verify(gameRepository, times(1)).save(any(Blockchain.Block.Game.class))
    }
}
