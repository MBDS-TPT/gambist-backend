package gambist

class Bet {

    double betValue
    Date betDate
    double winningRate
    BetType betType
    Match match
    double odds
    Team team
    static belongsTo = [user: Users]
    int state

    static constraints = {
        state default: 0
        betDate default: new Date()
        betType nullable: true
        team nullable: true
        odds default: 1
    }
    static mapping = {
        id(generator: "increment")
    }
}
