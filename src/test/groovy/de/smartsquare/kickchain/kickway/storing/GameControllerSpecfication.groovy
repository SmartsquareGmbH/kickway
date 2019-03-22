package de.smartsquare.kickchain.kickway.storing

import de.smartsquare.kickchain.kickway.Blockchain
import de.smartsquare.kickchain.kickway.elo.EloRatingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
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

    @Autowired
    EloRatingRepository eloRatingRepository

    def cleanup() {
        eloRatingRepository.deleteAll()
    }

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

    def 'store game'() {
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

    def 'initial elo rating is assigned to the winner team'() {
        given:
        def game = """
                {
                    "team1" : { "players" : ["deen", "ruby"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "10" , "goals2" : "1" }
                }
        """
        when:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(game))

        then:
        1020.0 == eloRatingRepository.findEloByPlayernames("deen", "ruby")
    }

    def 'initial elo rating is assigned to the looser team'() {
        given:
        def game = """
                {
                    "team1" : { "players" : ["deen", "ruby"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "10" , "goals2" : "1" }
                }
        """

        when:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(game))

        then:
        980.0 == eloRatingRepository.findEloByPlayernames("AlexN", "DanielR")
    }

    def 'play games with different playername orders'() {
        given:
        def firstGame = """
                {
                    "team1" : { "players" : ["deen", "ruby"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "10" , "goals2" : "1" }
                }
        """

        def secondGame = """
                {
                    "team1" : { "players" : ["ruby", "deen"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "10" , "goals2" : "1" }
                }
        """

        def thirdGame = """
                {
                    "team1" : { "players" : ["ruby", "deen"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "10" , "goals2" : "1" }
                }
        """

        when:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(firstGame))

        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(secondGame))

        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(thirdGame))

        then:
        1053.44 == eloRatingRepository.findEloByPlayernames("ruby", "deen")
    }

    def 'play games with different playername orders in team two'() {
        given:
        def firstGame = """
                {
                    "team1" : { "players" : ["deen", "ruby"] } , "team2" : { "players" : ["DanielR", "AlexN"] } ,
                    "score" : { "goals1" : "1" , "goals2" : "10" }
                }
        """

        def secondGame = """
                {
                    "team1" : { "players" : ["deen", "ruby"] } , "team2" : { "players" : ["AlexN", "DanielR"] } ,
                    "score" : { "goals1" : "1" , "goals2" : "10" }
                }
        """

        def thirdGame = """
                {
                    "team1" : { "players" : ["ruby", "deen"] } , "team2" : { "players" : ["DanielR", "AlexN"] } ,
                    "score" : { "goals1" : "1" , "goals2" : "10" }
                }
        """

        when:
        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(firstGame))

        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(secondGame))

        mockMvc.perform(post("/game")
                .contentType(APPLICATION_JSON)
                .content(thirdGame))

        then:
        1053.44 == eloRatingRepository.findEloByPlayernames("AlexN", "DanielR")
    }
}
