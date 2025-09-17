package interpreter;

/**
 * Class used for exceptions when interpreting.
 * This gets called whenever something goes wrong with Interpreter.java
 * 
 * @author Warren
 */
public class InterpretationException extends RuntimeException {
    public InterpretationException() {
    }

    public InterpretationException(String message) { 
        super(message); 
    }

    public InterpretationException(String message, Throwable cause) { 
        super(message, cause); 
    }

    public InterpretationException(Throwable cause) { 
        super(cause); 
    }
}

