package gambist

import grails.converters.JSON
import grails.validation.ValidationException

import javax.servlet.http.HttpServletResponse

import static org.springframework.http.HttpStatus.*

class BetTypeController {

    BetTypeService betTypeService
    CategoryService categoryService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def findById(int id) {
        def betType = betTypeService.get(id)
        JSON.use("deep") {
            render betType as JSON
        }
    }

    def all() {
        def betTypes = betTypeService.list()
        JSON.use("deep") {
            render betTypes as JSON
        }
    }

    def add() {
        def betType = new BetType()
        betType.label = request.JSON.label
        betType.description = request.JSON.description
        betType.currentWinningRate = Double.parseDouble(request.JSON.currentWinningRate)
        betType.category = categoryService.get(request.JSON.categoryId)
        betType = betTypeService.save(betType)
        JSON.use("deep") {
            render betType as JSON
        }
    }

    def edit() {
        if(!request.JSON.id || !request.JSON.label || !request.JSON.categoryId)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def betType = betTypeService.get(request.JSON.id)
        if(!betType)
            return response.status = HttpServletResponse.SC_NOT_FOUND
        betType.label = request.JSON.label
        betType.description = request.JSON.description
        betType.currentWinningRate = Double.parseDouble(request.JSON.currentWinningRate)
        betType.category = categoryService.get(request.JSON.categoryId)
        if(!betType.category)
            return response.status = HttpServletResponse.SC_NOT_FOUND
        betType = betTypeService.save(betType);
        JSON.use("deep") {
            render betType as JSON
        }
    }

    def delete() {
        if(!request.JSON.id)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def betType = betTypeService.get(request.JSON.id)
        betType.state = State.DELETED
        betTypeService.save(betType);
        render betType as JSON
    }
//    def index(Integer max) {
//        params.max = Math.min(max ?: 10, 100)
//        respond betTypeService.list(params), model:[betTypeCount: betTypeService.count()]
//    }
//
//    def show(Long id) {
//        respond betTypeService.get(id)
//    }
//
//    def create() {
//        respond new BetType(params)
//    }
//
//    def save(BetType betType) {
//        if (betType == null) {
//            notFound()
//            return
//        }
//
//        try {
//            betTypeService.save(betType)
//        } catch (ValidationException e) {
//            respond betType.errors, view:'create'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.created.message', args: [message(code: 'betType.label', default: 'BetType'), betType.id])
//                redirect betType
//            }
//            '*' { respond betType, [status: CREATED] }
//        }
//    }
//
//    def edit(Long id) {
//        respond betTypeService.get(id)
//    }
//
//    def update(BetType betType) {
//        if (betType == null) {
//            notFound()
//            return
//        }
//
//        try {
//            betTypeService.save(betType)
//        } catch (ValidationException e) {
//            respond betType.errors, view:'edit'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.updated.message', args: [message(code: 'betType.label', default: 'BetType'), betType.id])
//                redirect betType
//            }
//            '*'{ respond betType, [status: OK] }
//        }
//    }
//
//    def delete(Long id) {
//        if (id == null) {
//            notFound()
//            return
//        }
//
//        betTypeService.delete(id)
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.deleted.message', args: [message(code: 'betType.label', default: 'BetType'), id])
//                redirect action:"index", method:"GET"
//            }
//            '*'{ render status: NO_CONTENT }
//        }
//    }
//
//    protected void notFound() {
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.not.found.message', args: [message(code: 'betType.label', default: 'BetType'), params.id])
//                redirect action: "index", method: "GET"
//            }
//            '*'{ render status: NOT_FOUND }
//        }
//    }
}
