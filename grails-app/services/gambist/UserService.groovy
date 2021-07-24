package gambist

import grails.gorm.services.Service

@Service(User)
abstract class UserService {

    abstract User get(Serializable id)

    List<User> list(Map args) {
        return User.findAllByState(State.CREATED)
    }

    User login(login, password) {
        return User.findByEmailAndPassword(login, password)
    }

    abstract Long count()

    abstract void delete(Serializable id)

    abstract User save(User user)

}