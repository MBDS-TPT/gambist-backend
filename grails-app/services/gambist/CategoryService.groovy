package gambist

import grails.gorm.services.Service

@Service(Category)
abstract class CategoryService {

    abstract Category get(Serializable id)

//    abstract List<Category> list(Map args)

    List<Category> list() {
        return Category.findAllByState(State.CREATED)
    }

    abstract Long count()

    abstract void delete(Serializable id)

    abstract Category save(Category category)

}