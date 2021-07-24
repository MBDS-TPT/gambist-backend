package gambist

import grails.converters.JSON
import grails.converters.XML
import grails.validation.ValidationException

import javax.servlet.http.HttpServletResponse
import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.converters.XML

class CategoryController {

    CategoryService categoryService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def all() {
        def teams = categoryService.list()
        response.withFormat {
            json { render teams as JSON }
            xml { render teams as XML }
        }
    }

    def add() {
        def category = new Category()
        category.label = request.JSON.label
        category = categoryService.save(category)
        render category as JSON
    }

    def edit() {
        if(!request.JSON.id || !request.JSON.label)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def category = categoryService.get(request.JSON.id)
        if(!category)
            return response.status = HttpServletResponse.SC_NOT_FOUND
        category.label = request.JSON.label
        category = categoryService.save(category);
        render category as JSON
    }

    def delete() {
        if(!request.JSON.id)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def category = categoryService.get(request.JSON.id)
        category.state = State.DELETED
        categoryService.save(category);
        render category as JSON
    }

//    def index(Integer max) {
//        params.max = Math.min(max ?: 10, 100)
//        respond categoryService.list(params), model:[categoryCount: categoryService.count()]
//    }
//
//    def show(Long id) {
//        respond categoryService.get(id)
//    }
//
//    def create() {
//        respond new Category(params)
//    }
//
//    def save(Category category) {
//        if (category == null) {
//            notFound()
//            return
//        }
//
//        try {
//            categoryService.save(category)
//        } catch (ValidationException e) {
//            respond category.errors, view:'create'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.created.message', args: [message(code: 'category.label', default: 'Category'), category.id])
//                redirect category
//            }
//            '*' { respond category, [status: CREATED] }
//        }
//    }
//
//    def edit(Long id) {
//        respond categoryService.get(id)
//    }
//
//    def update(Category category) {
//        if (category == null) {
//            notFound()
//            return
//        }
//
//        try {
//            categoryService.save(category)
//        } catch (ValidationException e) {
//            respond category.errors, view:'edit'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.updated.message', args: [message(code: 'category.label', default: 'Category'), category.id])
//                redirect category
//            }
//            '*'{ respond category, [status: OK] }
//        }
//    }
//
//    def delete(Long id) {
//        if (id == null) {
//            notFound()
//            return
//        }
//
//        categoryService.delete(id)
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.deleted.message', args: [message(code: 'category.label', default: 'Category'), id])
//                redirect action:"index", method:"GET"
//            }
//            '*'{ render status: NO_CONTENT }
//        }
//    }
//
//    protected void notFound() {
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.not.found.message', args: [message(code: 'category.label', default: 'Category'), params.id])
//                redirect action: "index", method: "GET"
//            }
//            '*'{ render status: NOT_FOUND }
//        }
//    }
}
