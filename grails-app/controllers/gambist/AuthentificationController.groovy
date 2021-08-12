package gambist

import gambist.UserService
import gambist.model.ResponseBody
import grails.converters.JSON
import utils.DateUtil

import javax.servlet.http.HttpServletResponse

class AuthentificationController {

    UserService userService

    def login() {
        def login = request.JSON.username
        def password = request.JSON.password
        def isAdmin = request.JSON.isAdmin
        if(!isAdmin) isAdmin = false
        if(!login || !password)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def user = userService.login(login, password, isAdmin)
        def body = new ResponseBody()
        if(user) {
            user.password = '???'
            body.data = user
            body.status = HttpServletResponse.SC_OK
            body.message = 'OK'
            JSON.use('deep') {
                render body as JSON
            }
        } else {
            body.message = 'Unable to authenticate, please verify your username/password and try again'
            body.status = HttpServletResponse.SC_NOT_FOUND
            render body as JSON
        }
    }

    def registration() {
        if(!request.JSON.password
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
}
