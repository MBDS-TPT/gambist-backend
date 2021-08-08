package gambist

import groovy.transform.CompileStatic

@CompileStatic
class State {
    public final static int CREATED = 0
    public final static int DELETED = -1
    public final static int MATCH_ENDED = 5

    public final static int BET_WON = 6
    public final static int LOST_BET = 7
}
