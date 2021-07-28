package gambist

import grails.gorm.services.Service

@Service(Users)
abstract class UserService {

    abstract Users get(Serializable id)

    List<Users> list(Map args) {
        return Users.findAllByState(State.CREATED)
    }

    Users login(login, password) {
        return Users.findByEmailAndPassword(login, password)
    }

    abstract Long count()

    abstract void delete(Serializable id)

    abstract Users save(Users user)

}