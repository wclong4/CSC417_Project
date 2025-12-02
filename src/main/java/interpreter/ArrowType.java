package interpreter;

import java.util.List;

/**
 * Represents an arrow type
 * 
 * Parameter types and return type may be either
 * base types (Type) or nested ArrowType
 * 
 * There is a special case for a variadic parameter list (used for print),
 * see https://en.wikipedia.org/wiki/Variadic_function
 * 
 * This lets us have any number of parameters we want for print
 * 
 * @author Warren Long (wclong4)
 */
public class ArrowType {
    private final List<Object> paramTypes; // elements should be Type or ArrowType, I love objects
    private final Object returnType;       // Type or ArrowType
    private final boolean variadicAny;     // true for print operator, our loophole

    public ArrowType(List<Object> paramTypes, Object returnType) {
        this(paramTypes, returnType, false);
    }

    public ArrowType(List<Object> paramTypes, Object returnType, boolean variadicAny) {
        this.paramTypes = paramTypes;
        this.returnType = returnType;
        this.variadicAny = variadicAny;
    }

    public List<Object> getParamTypes() {
        return paramTypes;
    }

    public Object getReturnType() {
        return returnType;
    }

    public boolean isVariadicAny() {
        return variadicAny;
    }

    @Override
    public String toString() {
        String s = "(";
        for (int i = 0; i < paramTypes.size(); i++) {
            s = s + paramTypes.get(i).toString();
            if (i < paramTypes.size() - 1 || (i == paramTypes.size() - 2 && paramTypes.size() == 2)) {
               s += ", ";
            }
        }
        s += " -> ";
        s += returnType.toString();
        s += ")";
        return s;
    }
}
