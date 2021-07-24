package gambist

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BetServiceSpec extends Specification {

    BetService betService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Bet(...).save(flush: true, failOnError: true)
        //new Bet(...).save(flush: true, failOnError: true)
        //Bet bet = new Bet(...).save(flush: true, failOnError: true)
        //new Bet(...).save(flush: true, failOnError: true)
        //new Bet(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //bet.id
    }

    void "test get"() {
        setupData()

        expect:
        betService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Bet> betList = betService.list(max: 2, offset: 2)

        then:
        betList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        betService.count() == 5
    }

    void "test delete"() {
        Long betId = setupData()

        expect:
        betService.count() == 5

        when:
        betService.delete(betId)
        sessionFactory.currentSession.flush()

        then:
        betService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Bet bet = new Bet()
        betService.save(bet)

        then:
        bet.id != null
    }
}
