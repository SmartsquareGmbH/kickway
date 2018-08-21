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
        blockchainAnalyzer.findTopTenSoloQPlayers() == ['deen', 'ruby', 'skonair', 'alexn', 'danielr', 'alexr', 'marco', 'drs', 'natascha', 'saschar']
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
        blockchainAnalyzer.findTopTenDuoQPlayers() == ['deen', 'ruby', 'drs', 'marco', 'natascha', 'juri', 'tim', 'samy', 'lilly', 'skonair']
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
        blockchainAnalyzer.findTopTenFlexQPlayers() == ['ruby', 'deen', 'alexr', 'jani', 'nats', 'saschar', 'xander', 'skonair', 'kevin', 'drs']
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
        blockchainAnalyzer.findStatisticsOfPlayer('deen').winRate == 0.5
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

}
