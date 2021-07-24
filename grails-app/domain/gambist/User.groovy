package gambist

class User {

    String email
    String firstname
    String password
    String lastname
    String username
    Date dayOfBirth
    static hasMany = [bets: Bet]
    int state

    static constraints = {
        state default: 0
    }
}
