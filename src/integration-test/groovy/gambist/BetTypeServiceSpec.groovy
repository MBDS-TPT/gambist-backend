package gambist

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BetTypeServiceSpec extends Specification {

    BetTypeService betTypeService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new BetType(...).save(flush: true, failOnError: true)
        //new BetType(...).save(flush: true, failOnError: true)
        //BetType betType = new BetType(...).save(flush: true, failOnError: true)
        //new BetType(...).save(flush: true, failOnError: true)
        //new BetType(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //betType.id
    }

    void "test get"() {
        setupData()

        expect:
        betTypeService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<BetType> betTypeList = betTypeService.list(max: 2, offset: 2)

        then:
        betTypeList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        betTypeService.count() == 5
    }

    void "test delete"() {
        Long betTypeId = setupData()

        expect:
        betTypeService.count() == 5

        when:
        betTypeService.delete(betTypeId)
        sessionFactory.currentSession.flush()

        then:
        betTypeService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        BetType betType = new BetType()
        betTypeService.save(betType)

        then:
        betType.id != null
    }
}
