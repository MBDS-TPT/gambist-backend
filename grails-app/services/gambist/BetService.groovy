package gambist

import grails.gorm.services.Service

@Service(Bet)
abstract class BetService {

    abstract Bet get(Serializable id)

    List<Bet> findByDate(Date date) {
        def criteria = Bet.createCriteria()
        def res = criteria.list {
            eq('betDate', date)
        }
        return res
    }

    List<Bet> findByUser(Long userId) {
        def criteria = Bet.createCriteria()
        def res = criteria.list {
            eq('user.id', userId)
        }
        return res
    }

    List<Bet> findByUserAndCategory(Long userId, Long categoryId) {
        def criteria = Bet.createCriteria()
        def res = criteria.list {
            eq('user.id', userId)
            'match' {
                eq('category.id', categoryId)
                gte('matchDate', new Date())
            }
            order('betDate', 'desc')
        }
        return res
    }

    int countUserBetByState(userid, state) {
        Users u = Users.findById(userid)
        return Bet.countByUserAndState(u, state)
    }

    abstract List<Bet> list(Map args)

    abstract Long count()

    abstract void delete(Serializable id)

    abstract Bet save(Bet bet)

}