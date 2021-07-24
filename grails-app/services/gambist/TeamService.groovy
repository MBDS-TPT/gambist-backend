package gambist

import grails.gorm.services.Service

@Service(Team)
abstract class TeamService {

    abstract Team get(Serializable id)

//    abstract List<Team> list(Map args)

    Object list(Map args) {
        def max = args && args.max ? Integer.parseInt(args.max) : 10
        def offset = args && args.page ? Integer.parseInt(args.page) * max : 0
        def criteria = Team.createCriteria()
        def res =  criteria.list(max: max, offset: offset) {
            eq('state', State.CREATED)
            if(args.categoryId)
                eq('category.id', Long.parseLong(args.categoryId))
            if(args.name)
                sqlRestriction ("upper(name) like '%${args.name.toUpperCase()}%'")
            order('id', 'desc')
        }
        return [data: res, totalCount: res.getTotalCount()]
    }

    abstract Long count()

    abstract void delete(Serializable id)

    abstract Team save(Team team)

}