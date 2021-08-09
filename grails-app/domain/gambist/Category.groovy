package gambist

class Category {

    String label
    int state

    static constraints = {
        state default: 0
    }

    static mapping = {
        id(generator: "increment")
    }
}
