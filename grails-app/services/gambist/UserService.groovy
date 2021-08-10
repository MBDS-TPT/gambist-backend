package gambist

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

@Service(Users)
@Transactional
abstract class UserService {

    abstract Users get(Serializable id)

    List<Users> list(Map args) {
        return Users.findAllByState(State.CREATED)
    }

    Users login(login, password, isAdmin) {
        return Users.findByEmailAndPasswordAndIsAdmin(login, password.sha256(), isAdmin)
    }

    Users editProfil(userId, firstName, lastname) {
        Users u = Users.findById(userId)
        if(u) {
            u.firstname = firstName
            u.lastname = lastname
            u.save(flush: true)
        }
        return u
    }

    Users changePassword(userId, password, newPassword) {
        Users u = Users.findByIdAndPassword(userId, password.sha256())
        if(u) {
            u.password = newPassword.sha256()
            u = u.save(flush: true)
        }
        return u
    }


    Users updateSolde(userId, password, montant) {
        Users u = Users.findByIdAndPassword(userId, password.sha256())
        if(u) {
            u.bankBalance += montant
            u = u.save(flush: true)
        }
        return u
    }

    abstract Long count()

    abstract void delete(Serializable id)

    abstract Users save(Users user)

}