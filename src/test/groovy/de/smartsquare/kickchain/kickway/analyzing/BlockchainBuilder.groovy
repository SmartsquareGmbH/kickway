package de.smartsquare.kickchain.kickway.analyzing

import de.smartsquare.kickchain.kickway.Blockchain

class BlockchainBuilder {

    def games = []

    def firstPlayer
    def scoreOfFirstPlayer
    def scoreOfSecondPlayer

    def lastGame = false

    static aBlockchain() {
        return new BlockchainBuilder()
    }

    def where(name) {
        firstPlayer = name
        return this
    }

    def played(score) {
        scoreOfFirstPlayer = score
        return this
    }

    def to(score) {
        scoreOfSecondPlayer = score
        return this
    }

    def against(name) {
        games << new Blockchain.Block([
                new Blockchain.Block.Game(
                        new Blockchain.Block.Game.Team(firstPlayer instanceof String ? [firstPlayer] : firstPlayer,),
                        new Blockchain.Block.Game.Team(name instanceof String ? [name] : name),
                        new Blockchain.Block.Game.Score(scoreOfFirstPlayer, scoreOfSecondPlayer)
                )
        ])

        if (lastGame) {
            return new Blockchain(games)
        } else {
            return this
        }
    }

    def and(name) {
        firstPlayer = name
        return this
    }

    def finalizedBy(name) {
        firstPlayer = name
        lastGame = true
        return this
    }

}
