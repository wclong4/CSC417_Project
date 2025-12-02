package interpreter;

/**
 * The types consist of:
 * :int, :bool, :str, :unit
 * 
 * @author Warren Long (wclong4)
 */
public enum Type {
    INT {
        @Override
            public String toString() {
                return ":int";
            }
    },
    BOOL {
        @Override
            public String toString() {
                return ":bool";
            }
    },
    STR {
        @Override
            public String toString() {
                return ":str";
            }
    },
    UNIT  {
        @Override
            public String toString() {
                return ":unit";
            }
    }
}
