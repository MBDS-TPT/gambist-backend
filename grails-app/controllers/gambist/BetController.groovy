package gambist

import gambist.model.ResponseBody
import grails.converters.JSON
import grails.validation.ValidationException
import utils.DateUtil

import javax.servlet.http.HttpServletResponse
import java.sql.Timestamp

import static org.springframework.http.HttpStatus.*

class BetController {

    BetService betService

    MatchService matchService

    UserService userService

    TeamService teamService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def all() {
        def bets = betService.list()
        JSON.use("deep") {
            render bets as JSON
        }
    }

    def findByUser() {
        if(!params.userid)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def bets = betService.findByUser(Long.parseLong(params.userid))
        JSON.use('deep') {
            render bets as JSON
        }
    }

    def findByUserAndCategory() {
        if(!params.userid || !params.categoryId)
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        def bets = betService.findByUserAndCategory(Long.parseLong(params.userid), Long.parseLong(params.categoryId))
        JSON.use('deep') {
            render bets as JSON
        }
    }

    def byDate() {
        if(params.date) {
            def date = DateUtil.toDate2(params.date)
            def bets = betService.findByDate(date)
            JSON.use("deep") {
                render bets as JSON
            }
        } else {
            return response.status = HttpServletResponse.SC_BAD_REQUEST
        }
    }

    def add() {
        if(!request.getMethod().equalsIgnoreCase("POST"))
            return HttpServletResponse.SC_METHOD_NOT_ALLOWED
        def date = DateUtil.toDate(request.JSON.betDate)
        def match = matchService.get(request.JSON.matchId)
        def user = userService.get(request.JSON.userId)
        def responseBody = new ResponseBody(status: HttpServletResponse.SC_OK, result: "OK")
        Team team = null
        if(request.JSON.teamId) {
            team = teamService.get(request.JSON.teamId)
            if(!team)
                return HttpServletResponse.SC_NOT_FOUND
        }
        if(!request.JSON.betValue || !request.JSON.betDate ||
                !request.JSON.odds || !match || !date || !user)
            return HttpServletResponse.SC_BAD_REQUEST
        if(user.bankBalance < request.JSON.betValue) {
            responseBody.status = HttpServletResponse.SC_BAD_REQUEST
            responseBody.result = "KO"
            responseBody.message = "Your balance is insufficient."
            JSON.use(('deep'))  {
                render responseBody as JSON
            }
        } else {
            user.bankBalance = user.bankBalance-request.JSON.betValue
            userService.save(user)
            def bet = new Bet(
                    match: match,
                    user: user,
                    betValue: request.JSON.betValue,
                    betDate: date,
                    team: team,
                    odds: request.JSON.odds
            )
            bet = betService.save(bet)
            responseBody.data = bet
            JSON.use(('deep'))  {
                render responseBody as JSON
            }
        }

    }

    def getUserBetStatistics() {
        if(!params.userid) response.status = HttpServletResponse.SC_BAD_REQUEST
        def map = [:]
        map.won = betService.countUserBetByState(params.userid, State.BET_WON);
        map.lost = betService.countUserBetByState(params.userid, State.LOST_BET);
        def body = new ResponseBody()
        body.status = HttpServletResponse.SC_OK
        body.result = "OK"
        body.data = map
        render body as JSON
    }

//    def index(Integer max) {
//        params.max = Math.min(max ?: 10, 100)
//        respond betService.list(params), model:[betCount: betService.count()]
//    }
//
//    def show(Long id) {
//        respond betService.get(id)
//    }
//
//    def create() {
//        respond new Bet(params)
//    }
//
//    def save(Bet bet) {
//        if (bet == null) {
//            notFound()
//            return
//        }
//
//        try {
//            betService.save(bet)
//        } catch (ValidationException e) {
//            respond bet.errors, view:'create'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.created.message', args: [message(code: 'bet.label', default: 'Bet'), bet.id])
//                redirect bet
//            }
//            '*' { respond bet, [status: CREATED] }
//        }
//    }
//
//    def edit(Long id) {
//        respond betService.get(id)
//    }
//
//    def update(Bet bet) {
//        if (bet == null) {
//            notFound()
//            return
//        }
//
//        try {
//            betService.save(bet)
//        } catch (ValidationException e) {
//            respond bet.errors, view:'edit'
//            return
//        }
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.updated.message', args: [message(code: 'bet.label', default: 'Bet'), bet.id])
//                redirect bet
//            }
//            '*'{ respond bet, [status: OK] }
//        }
//    }
//
//    def delete(Long id) {
//        if (id == null) {
//            notFound()
//            return
//        }
//
//        betService.delete(id)
//
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.deleted.message', args: [message(code: 'bet.label', default: 'Bet'), id])
//                redirect action:"index", method:"GET"
//            }
//            '*'{ render status: NO_CONTENT }
//        }
//    }
//
//    protected void notFound() {
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.not.found.message', args: [message(code: 'bet.label', default: 'Bet'), params.id])
//                redirect action: "index", method: "GET"
//            }
//            '*'{ render status: NOT_FOUND }
//        }
//    }
}
