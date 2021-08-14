package gambist

import gambist.model.ResponseBody
import utils.DateUtil

import javax.servlet.http.HttpServletResponse
import java.sql.Timestamp

import grails.converters.JSON

class MatchController {

    MatchService matchService
    TeamService teamService
    CategoryService categoryService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]


    def get() {
        if(params.id) {
            def match = matchService.get(params.id)
            if(match) {
                JSON.use('deep') {
                    render match as JSON
                }
            }
        }
        return response.status = HttpServletResponse.SC_NOT_FOUND
    }

    def endMatch() {
        if(!request.JSON.matchId) return HttpServletResponse.SC_BAD_REQUEST
        long matchId = Long.parseLong(request.JSON.matchId+"")
        def match = matchService.endMatch(matchId)
        def responseBody = new ResponseBody(
                status: HttpServletResponse.SC_OK,
                message: "Match closed",
                result: "OK",
                data: match
        )
        render responseBody as JSON
    }

    def all() {
        if(params.date1) {
            def d = DateUtil.toDate(params.date1)
            if(d) {
                params.date1 = new Timestamp(d.getTime())
            } else params.date1 = null
        }
        if(params.date2) {
            def d = DateUtil.toDate(params.date2)
            if(d) {
                params.date2 = new Timestamp(d.getTime())
            } else params.date2 = null
        }
        def matches = matchService.list(params)
        JSON.use('deep') {
            render matches as JSON
        }
    }

    def getAllMatchId() {
        def ids = matchService.getAllMatchId()
        render ids as JSON
    }

    def getUpcomingMatchByCategory() {
        if(params.categoryId) {
            int categoryId = Long.parseLong(params.categoryId)
            def matches = matchService.getUpcomingMatchByCategory(categoryId)
            JSON.use('deep') {
                render matches as JSON
            }
        }
        return response.status = HttpServletResponse.SC_NOT_FOUND
    }

    def getUpcomingMatchGroupedByCategory() {
        def matches = matchService.getUpcomingMatchGroupedByCategory()
        JSON.use('deep') {
            render matches as JSON
        }
    }

    def getLatestMatchResult() {
        int count = params.count && params.count > 0 ? Integer.parseInt(params.count) : 3
        def matches = matchService.getLatestMatchResult(count)
        JSON.use('deep') {
            render matches as JSON
        }
    }

    def getPopularMatches() {
        int count = params.count && params.count > 0 ? Integer.parseInt(params.count) : 3
        def matches = matchService.getPopularMatches(count)
        JSON.use('deep') {
            render matches as JSON
        }
    }

    def add() {
        if(!request.getMethod().equalsIgnoreCase("POST"))
            return HttpServletResponse.SC_METHOD_NOT_ALLOWED
        def date = new Timestamp(DateUtil.toDate(request.JSON.matchDate).getTime())
        if(!request.JSON.teamAId ||
                !request.JSON.teamBId ||
                !request.JSON.categoryId || !date ||
                !request.JSON.oddsA || !request.JSON.oddsB)
            return HttpServletResponse.SC_BAD_REQUEST
        def match = new Match(
                category: categoryService.get(request.JSON.categoryId),
                teamA: teamService.get(request.JSON.teamAId),
                teamB: teamService.get(request.JSON.teamBId),
                oddsA: request.JSON.oddsA,
                oddsB: request.JSON.oddsB,
                oddsNul: request.JSON.oddsNul,
                matchDate: date
        )
        match = matchService.save(match)
        JSON.use(('deep'))  {
            render match as JSON
        }
    }

    def getTodaysMatch() {
        def match = matchService.getTodaysMatch()
        JSON.use('deep') {
            render match as JSON
        }
    }

    def getOnGoingMatch() {
        def match = matchService.getOnGoingMatch()
        JSON.use('deep') {
            render match as JSON
        }
    }

    def edit() {
        if(!request.getMethod().equalsIgnoreCase("PUT"))
            return HttpServletResponse.SC_METHOD_NOT_ALLOWED
        def date = new Timestamp(DateUtil.toDate(request.JSON.matchDate).getTime())
        if(!request.JSON.teamAId || !request.JSON.teamBId || !request.JSON.categoryId || !date)
            return HttpServletResponse.SC_BAD_REQUEST
        def match = matchService.get(request.JSON.id)
        if(!match)
            return response.status = HttpServletResponse.SC_NOT_FOUND
        match.category = categoryService.get(request.JSON.categoryId)
        match.teamA = teamService.get(request.JSON.teamAId)
        match.teamB = teamService.get(request.JSON.teamBId)
        match.matchDate = date
        matchService.save(match)
        JSON.use("deep") {
            render match as JSON
        }
    }

    def updateScore() {
        if(!request.getMethod().equalsIgnoreCase("PUT"))
            return HttpServletResponse.SC_METHOD_NOT_ALLOWED
        if(!request.JSON.id || request.JSON.scoreA<0 || request.JSON.scoreB<0)
            return HttpServletResponse.SC_BAD_REQUEST
        def endMatch = request.JSON.endMatch
        def match = matchService.get(request.JSON.id)
        if(!match)
            return response.status = HttpServletResponse.SC_NOT_FOUND
        match.scoreA = Integer.parseInt((request.JSON.scoreA+""))
        match.scoreB = Integer.parseInt((request.JSON.scoreB+""))
        if(endMatch) {
            match.state = State.MATCH_ENDED
        }
        match = matchService.save(match)
        JSON.use("deep") {
            render match as JSON
        }
    }

    def delete() {
        if(!request.getMethod().equalsIgnoreCase("DELETE"))
            return HttpServletResponse.SC_METHOD_NOT_ALLOWED
        if(!request.JSON.id)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def match = matchService.get(request.JSON.id)
        match.state = State.DELETED
        matchService.save(match);
        JSON.use("deep") {
            render match as JSON
        }
    }
}
