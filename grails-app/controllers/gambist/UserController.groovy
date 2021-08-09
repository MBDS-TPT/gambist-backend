package gambist

import grails.converters.JSON
import utils.DateUtil

import javax.servlet.http.HttpServletResponse

class UserController {

    UserService userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def all() {
        JSON.use('deep') {
            render userService.list() as JSON
        }
    }

    def add() {
        if(!request.JSON.id || !request.JSON.password
                || !request.JSON.username
                || !request.JSON.lastname
                || !request.JSON.dayOfBirth
                || !request.JSON.email
                || !request.JSON.firstname)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def user = new Users()
        user.firstname = request.JSON.firstname
        user.lastname = request.JSON.lastname
        user.dayOfBirth = DateUtil.toDate(request.JSON.dayOfBirth)
        user.email = request.JSON.email
        user.username = request.JSON.username
        user.password = request.JSON.password
        userService.save(user)
        JSON.use("deep") {
            render user as JSON
        }
    }

    def edit() {
        if(!request.JSON.id || !request.JSON.password
                || !request.JSON.username
                || !request.JSON.lastname
                || !request.JSON.dayOfBirth
                || !request.JSON.email
                || !request.JSON.firstname)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def user = userService.get(request.JSON.id)
        if(!user)
            return response.status = HttpServletResponse.SC_NOT_FOUND
        user.firstname = request.JSON.firstname
        user.lastname = request.JSON.lastname
        user.dayOfBirth = DateUtil.toDate(request.JSON.dayOfBirth)
        user.email = request.JSON.email
        user.username = request.JSON.username
        user.password = request.JSON.password
        userService.save(user)
        JSON.use("deep") {
            render user as JSON
        }
    }

    def delete() {
        if(!request.JSON.id)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def user = userService.get(request.JSON.id)
        user.setState(State.DELETED)
        userService.save(user)
        JSON.use("deep") {
            render user as JSON
        }
    }
//
//    def index(Integer max) {
//        params.max = Math.min(max ?: 10, 100)
//        respond userService.list(params), model:[userCount: userService.count()]
//    }
//
//    def show(Long id) {
//        respond userService.get(id)
//    }
//
//    def create() {
//        respond new User(params)
//    }
//
//    def save(User user) {
//        if (user == null) {
//            notFound()
//            return
//        }
//
//        try {
//            userService.save(user)
//        } catch (ValidationException e) {
//            respond user.errors, view:'create'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])
//                redirect user
//            }
//            '*' { respond user, [status: CREATED] }
//        }
//    }
//
//    def edit(Long id) {
//        respond userService.get(id)
//    }
//
//    def update(User user) {
//        if (user == null) {
//            notFound()
//            return
//        }
//
//        try {
//            userService.save(user)
//        } catch (ValidationException e) {
//            respond user.errors, view:'edit'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
//                redirect user
//            }
//            '*'{ respond user, [status: OK] }
//        }
//    }
//
//    def delete(Long id) {
//        if (id == null) {
//            notFound()
//            return
//        }
//
//        userService.delete(id)
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
//                redirect action:"index", method:"GET"
//            }
//            '*'{ render status: NO_CONTENT }
//        }
//    }
//
//    protected void notFound() {
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
//                redirect action: "index", method: "GET"
//            }
//            '*'{ render status: NOT_FOUND }
//        }
//    }
}
