package gambist

import grails.gorm.services.Service

@Service(BetType)
abstract class BetTypeService {

    abstract BetType get(Serializable id)

    List<BetType> list(Map args) {
        return BetType.findAllByState(State.CREATED)
    }

    abstract Long count()

    abstract void delete(Serializable id)

    abstract BetType save(BetType betType)

}