package de.smartsquare.kickchain.kickway.analyzing

import spock.lang.Specification

import static de.smartsquare.kickchain.kickway.analyzing.BlockchainBuilder.aBlockchain

class BlockchainAnalyzerSpecification extends Specification {

    def blockchainRepositoryMock = Mock(BlockchainRepository)
    def blockchainAnalyzer = new BlockchainAnalyzer(blockchainRepositoryMock)

    def 'find top ten soloq players'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where('deen').played(10).to(1).against('ruby')
                .and('skonair').played(10).to(1).against('deen')
                .and('alexn').played(10).to(7).against('ruby')
                .and('deen').played(3).to(10).against('danielr')
                .and('saschar').played(5).to(10).against('alexr')
                .and('marco').played(10).to(8).against('natascha')
                .and('drs').played(10).to(2).against('jensk')
                .and('deen').played(10).to(1).against('ruby')
                .and('deen').played(10).to(1).against('ruby')
                .finalizedBy('deen').played(10).to(1).against('ruby')

        expect:
        blockchainAnalyzer.findTopTenSoloQPlayers() == [
                new Player('deen', 44), new Player('ruby', 11),
                new Player('skonair', 10), new Player('alexn', 10),
                new Player('danielr', 10), new Player('alexr', 10),
                new Player('marco', 10), new Player('drs', 10),
                new Player('natascha', 8), new Player('saschar', 5)
        ]
    }

    def 'top ten duoq players'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where(['deen', 'ruby']).played(10).to(1).against(['skonair', 'drs'])
                .where(['marco', 'natascha']).played(10).to(6).against(['samy', 'lilly'])
                .where(['britta', 'kevin']).played(3).to(10).against(['deen', 'drs'])
                .where(['xander', 'jani']).played(2).to(10).against(['juri', 'tim'])
                .finalizedBy(['deen', 'ruby']).played(10).to(2).against(['skonair', 'drs'])

        expect:
        blockchainAnalyzer.findTopTenDuoQPlayers() == [
                new Player('deen', 30), new Player('ruby', 20),
                new Player('drs', 13), new Player('marco', 10),
                new Player('natascha', 10), new Player('juri', 10),
                new Player('tim', 10), new Player('samy', 6),
                new Player('lilly', 6), new Player('skonair', 3)
        ]
    }

    def 'top ten flexq players'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where(['deen', 'ruby']).played(10).to(1).against('drs')
                .and(['deen', 'saschar']).played(5).to(10).against('alexr')
                .and('xander').played(5).to(10).against(['jani', 'ruby'])
                .and(['skonair', 'kevin']).played(5).to(10).against('nats')
                .finalizedBy(['deen', 'ruby']).played(10).to(2).against('drs')

        expect:
        blockchainAnalyzer.findTopTenFlexQPlayers() == [
                new Player('ruby', 30), new Player('deen', 25),
                new Player('alexr', 10), new Player('jani', 10),
                new Player('nats', 10), new Player('saschar', 5),
                new Player('xander', 5), new Player('skonair', 5),
                new Player('kevin', 5), new Player('drs', 3)
        ]
    }

    def 'average goals per game of player'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where('deen').played(10).to(1).against('ruby')
                .finalizedBy('skonair').played(10).to(0).against('deen')

        expect:
        blockchainAnalyzer.findStatisticsOfPlayer('deen').averageGoalsPerGame == 5
    }

    def 'win rate of a player'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where('deen').played(10).to(1).against('ruby')
                .finalizedBy('skonair').played(10).to(0).against('deen')

        expect:
        blockchainAnalyzer.findStatisticsOfPlayer('deen').winRate == 0.5d
    }

    def 'crawl count of a player'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where('deen').played(10).to(1).against('ruby')
                .finalizedBy('skonair').played(10).to(0).against('deen')

        expect:
        blockchainAnalyzer.findStatisticsOfPlayer('deen').totalCrawls == 1
    }

    def 'throw exception if player is unknown'() {
        given:
        blockchainRepositoryMock.fetch() >> new Blockchain([])

        when:
        blockchainAnalyzer.findStatisticsOfPlayer('deen')

        then:
        def error = thrown(RuntimeException)
        error.message == 'The kickchain contains no games associated with the player deen'
    }

    def 'total wins'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where('deen').played(10).to(1).against('ruby')
                .finalizedBy('skonair').played(10).to(0).against('deen')

        expect:
        blockchainAnalyzer.findStatisticsOfPlayer('deen').totalWins == 1
    }

    def 'total losses'() {
        given:
        blockchainRepositoryMock.fetch() >> aBlockchain()
                .where('deen').played(10).to(1).against('ruby')
                .finalizedBy('skonair').played(10).to(0).against('deen')

        expect:
        blockchainAnalyzer.findStatisticsOfPlayer('deen').totalLosses == 1
    }

}
