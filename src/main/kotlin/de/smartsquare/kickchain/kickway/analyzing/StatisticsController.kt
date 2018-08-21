package de.smartsquare.kickchain.kickway.analyzing

import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class StatisticsController(val blockchainAnalyzer: BlockchainAnalyzer) {

    @GetMapping("/statistics/topten/soloq")
    fun findTopTenSoloQPlayers(): ResponseEntity<Any> =
        try {
            val topTen = blockchainAnalyzer.findTopTenSoloQPlayers()
            ResponseEntity.ok().body(topTen)
        } catch (e: KickchainException) {
            ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.message)
        }

    @GetMapping("/statistics/topten/duoq")
    fun findTopTenDuoQPlayers(): ResponseEntity<Any> =
        try {
            val topTen = blockchainAnalyzer.findTopTenDuoQPlayers()
            ResponseEntity.ok().body(topTen)
        } catch (e: KickchainException) {
            ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.message)
        }

    @GetMapping("/statistics/topten/flexq")
    fun findTopTenFlexQPlayers(): ResponseEntity<Any> =
        try {
            val topTen = blockchainAnalyzer.findTopTenFlexQPlayers()
            ResponseEntity.ok().body(topTen)
        } catch (e: KickchainException) {
            ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.message)
        }

    @GetMapping("/statistics/player/{playerName}")
    fun findStatisticsForPlayer(
        @PathVariable("playerName") playerName: String
    ): ResponseEntity<Any> =
        try {
            val playerStats = blockchainAnalyzer.findStatisticsOfPlayer(playerName)
            ResponseEntity.ok().body(playerStats)
        } catch (e: UnknownPlayerException) {
            ResponseEntity.status(NOT_FOUND).body(e.message)
        }
}
