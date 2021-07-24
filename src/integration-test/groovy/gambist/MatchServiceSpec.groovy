package gambist

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class MatchServiceSpec extends Specification {

    MatchService matchService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Match(...).save(flush: true, failOnError: true)
        //new Match(...).save(flush: true, failOnError: true)
        //Match match = new Match(...).save(flush: true, failOnError: true)
        //new Match(...).save(flush: true, failOnError: true)
        //new Match(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //match.id
    }

    void "test get"() {
        setupData()

        expect:
        matchService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Match> matchList = matchService.list(max: 2, offset: 2)

        then:
        matchList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        matchService.count() == 5
    }

    void "test delete"() {
        Long matchId = setupData()

        expect:
        matchService.count() == 5

        when:
        matchService.delete(matchId)
        sessionFactory.currentSession.flush()

        then:
        matchService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Match match = new Match()
        matchService.save(match)

        then:
        match.id != null
    }
}
