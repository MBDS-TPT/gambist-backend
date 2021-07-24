package gambist

import groovy.transform.CompileStatic

@CompileStatic
class State {
    public static int CREATED = 0
    public static int DELETED = -1
    public static int MATCH_ENDED = 5
}
