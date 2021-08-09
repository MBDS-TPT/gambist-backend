package gambist

import gambist.UserService
import gambist.model.ResponseBody
import grails.converters.JSON

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
}
