package gambist

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

@Service(Match)
@Transactional
abstract class MatchService {

    abstract Match get(Serializable id)

    Object list(Map args) {
        def max = args && args.max ? Integer.parseInt(args.max) : 10
        def offset = args && args.page ? Integer.parseInt(args.page) * max : 0
        def criteria = Match.createCriteria()
        def res =  criteria.list(max: max, offset: offset) {
            eq('state', State.CREATED)
            if(args.teamAId)
                eq('teamA.id', Long.parseLong(args.teamAId))
            if(args.teamBId)
                eq('teamB.id', Long.parseLong(args.teamBId))
            if(args.categoryId)
                eq('category.id', Long.parseLong(args.categoryId))
            if(args.date1 && args.date2)
                between('matchDate', args.date1, args.date2)
            else {
                if(args.date1)
                    gte('matchDate', args.date1)
                else if(args.date2) {
                    lte('matchDate', args.date2)
                }
            }
            order('matchDate', 'desc')
        }
        return [data: res, totalCount: res.getTotalCount()]
    }

    List getAllMatchId() {
        def criteria = Match.createCriteria();
        def matches = criteria.list {
            eq('state', State.CREATED)
        }
        def ids = []
        matches.each {
            ids.add(it.id)
        }
        return ids
    }

    List<Match> getUpcomingMatchByCategory(long categoryId) {
        def criteria = Match.createCriteria()
        return criteria.list() {
            eq('state', State.CREATED)
            eq('category.id', categoryId)
            gte('matchDate', new Date())
        }
    }

    List<Match> getLatestMatchResult(int count) {
        def criteria = Match.createCriteria()
        return criteria.list(max: count) {
            eq('state', State.MATCH_ENDED)
            lt('matchDate', new Date())
            order('matchDate', 'desc')
        }
    }

    List<Match> getTodaysMatch() {
        def criteria = Match.createCriteria()
        def calendar = GregorianCalendar.getInstance()
        calendar.setTime(new Date())
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        def date1 = calendar.getTime()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        def date2 = calendar.getTime()
        return criteria.list() {
            eq('state', State.CREATED)
            gte('matchDate', date1)
            lte('matchDate', date2)
        }
    }

    List<Match> getOnGoingMatch() {
        def match = getTodaysMatch()
        def reps = []
        match.forEach(){
            def d1 = it.matchDate.time
            def d2 = new Date().time
            if(d2 > d1)
                reps.add(it)
        }
        return reps
    }

    List<Match> getPopularMatches(int count) {
        return criteria.list(max: count) {
            eq('state', State.CREATED)
            projections {
                groupProperty('match')
                count()
                'match' {
                    gte('matchDate', new Date())
                }
            }
            order('betCount', 'desc')
        }
    }

    Map<String, List<Match>> getUpcomingMatchGroupedByCategory() {
        def categories = Category.createCriteria().list {
            eq('state', State.CREATED)
        }
        def data = [:]
        categories.each {
            data.put(it.label, this.getUpcomingMatchByCategory(it.id))
        }
        return data
    }

    Match endMatch(long id) {
        def match = Match.findById(id)
        match.state = State.MATCH_ENDED
        def bets = Bet.findAllByMatch(match)
        def teamAWin = match.scoreA > match.scoreB
        def teamBWin = match.scoreA < match.scoreB
        def draw = match.scoreA == match.scoreB
        bets.forEach { bet ->
            Users user = bet.user
            if((bet.teamId == match.teamAId && teamAWin) ||
                    (bet.teamId == match.teamBId && teamBWin) || (!bet.teamId && draw)) {
                user.bankBalance += bet.betValue * bet.odds
                user.save(flush: true)
                bet.state = State.BET_WON
            } else {
                bet.state = State.LOST_BET
            }
            bet.save(flush: true)
        }
        return match.save(flush: true)
    } 

    abstract Long count()

    abstract void delete(Serializable id)

    abstract Match save(Match match)

}