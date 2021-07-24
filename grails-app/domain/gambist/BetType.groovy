package gambist

class BetType {

    String label
    String description
    double currentWinningRate
    Category category
    int state

    static constraints = {
        state default: 0
        currentWinningRate default: 200
        description blank: true
    }
}
