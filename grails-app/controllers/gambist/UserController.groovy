package gambist

import gambist.model.ResponseBody
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
        def body = new ResponseBody()
        body.result = "OK"
        body.status = HttpServletResponse.SC_OK
        def tmp = Users.findByEmail(request.JSON.email)
        if(!tmp) {
            def user = new Users()
            user.firstname = request.JSON.firstname
            user.lastname = request.JSON.lastname
            user.dayOfBirth = DateUtil.toDate(request.JSON.dayOfBirth)
            user.email = request.JSON.email
            user.username = request.JSON.username
            user.password = request.JSON.password.sha256()
            userService.save(user)
            body.data = user
        } else {
            body.status = HttpServletResponse.SC_CONFLICT
            body.message = "This email adress is already in use"
            body.result = "KO"
        }
        JSON.use("deep") {
            render body as JSON
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

    def changePassword() {
        if(!request.JSON.id || !request.JSON.password || !request.JSON.newPassword)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        Users u = userService.changePassword(Long.parseLong(request.JSON.id+""), request.JSON.password, request.JSON.newPassword)
        ResponseBody body = new ResponseBody()
        body.message = 'Success'
        body.result = "OK"
        body.status = HttpServletResponse.SC_OK
        if(u) {
            body.data = u
        } else {
            body.status = HttpServletResponse.SC_NOT_FOUND
            body.message = "Wrong password!"
            body.result = "KO"
        }
        JSON.use('deep') {
            render body as JSON
        }
    }

    def creditAccount() {
        if(!request.JSON.id || !request.JSON.password || !request.JSON.montant)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        Users u = userService.updateSolde(Long.parseLong(request.JSON.id+""), request.JSON.password, Double.parseDouble(request.JSON.montant))
        ResponseBody body = new ResponseBody()
        if(u) {
            body.data = u
            body.message = 'Success'
            body.status = HttpServletResponse.SC_OK
        } else {
            body.status = HttpServletResponse.SC_NOT_FOUND
            body.message = "Wrong password!"
        }
        JSON.use('deep') {
            render body as JSON
        }
    }

    def editProfile() {
        if(!request.JSON.id || !request.JSON.firstname || !request.JSON.lastname)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        Users u = userService.editProfil(Long.parseLong(request.JSON.id+""), request.JSON.firstname, request.JSON.lastname)
        ResponseBody body = new ResponseBody()
        if(u) {
            body.data = u
            body.message = 'Success'
            body.status = HttpServletResponse.SC_OK
        } else {
            body.status = HttpServletResponse.SC_NOT_FOUND
            body.message = "Wrong password!"
        }
        JSON.use('deep') {
            render body as JSON
        }
    }

    def getUserById() {
        if(!params.userid)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def userid = params.userid
        def user = userService.getUserById(userid)
        JSON.use('deep') {
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
