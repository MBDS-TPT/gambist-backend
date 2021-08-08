package gambist

class Users {

    String email
    String firstname
    String password
    String lastname
    String username
    Date dayOfBirth
    boolean isAdmin
    double bankBalance
    static hasMany = [bets: Bet]
    int state

    static constraints = {
        state default: 0
        bankBalance default: 0
        isAdmin default: false
    }

    static mapping = {
        id(generator: "increment")
    }
}
