package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This takes in input from the interpreter which comes in JSON format.
 * It is able to take in Identifiers, Strings, and Numbers, Names, Blocks, Functions, etc.
 * It has lexical scope, which is implemented with closure.
 * That means we store the environment of the function when the function was made with the function.
 * 
 * Also, application is really bloated now, so maybe I should refactor.
 * I'll just leave that for future me.
 * 
 * @author Warren Long (wclong4)
 */
public class Interpreter {

    /**
     * Parses JSON as a map
     * @param json json to parse
     * @return a mapified version of the json string
     * @throws Exception
     */
    public static Map<String, Object> parseJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // get a map which is similar to JSON
        Map<String, Object> map = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        return map;
    }

    /**
     * Gets an object from env
     * @param env the env to look in
     * @param key the key to look for
     * @return the value associated with the key
     */
    public static Object getFromEnv(List<Map<String, Object>> env, Object key) {
        Object envVariable = null;
        // get env variable from env
        for (int i = env.size(); i > 0; i-- ) {
            Map<String, Object> localEnv = env.get(i - 1);
            if (localEnv.get(key) != null) {
                envVariable = localEnv.get(key);
            }
        }
        // make sure it exists
        if (envVariable == null) {
            throw new InterpretationException("Undefined Identifier -> " + key.toString());
        }   
        return envVariable;
    }
    /**
     * Parses an identifier
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static Object identifier(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        Object envVariable = getFromEnv(env, value);
        // is it a number?
        if (envVariable instanceof Integer) {
            return number(map, env, envVariable);
        } else if (envVariable instanceof Boolean) {
            return bool(map, env, envVariable);
        } else if (envVariable instanceof String) {
            return string(map, env, envVariable);
        } else {
            // just return it as an object
            return envVariable;
        }
    }

    /**
     * Parses a Number
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static Object number(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        try {
            int number = (int) value;
            //System.out.println(number);
            return number;
        } catch (Exception e) {
            throw new InterpretationException("Invalid Number -> " + value.toString());
        }
    }

    /**
     * Parses a String
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static Object string(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        //System.out.println("\"" + value + "\"" );
        return "" + value;
    }

    /**
     * Parses a name
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a name (literally just a string really) 
     */
    public static Object name(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        //System.out.println("\"" + value + "\"" );
        return string(map, env, value);
    }

    /**
     * Parses an Application
     * 
     * Does a crazy amount of unchecked casts and is overall a disgusting function.
     * 
     * If anything fails, it just throws an error, so if a cast fails, it just returns false
     * 
     * Also, it has some recursive nonsense where it calls interp, which then calls application
     * 
     * This is the worst code written by all mankind for all time.
     * 
     * I just really don't like recursion, but it makes this very simple.
     * 
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @throws InterpretationException if anything goes wrong
     * @return a boolean 
     */
    @SuppressWarnings("unchecked")
    public static Object application(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        try {
            // when we get the value it should have an operator and operands
            Map<String, Object> operations = (Map<String, Object>) value;
            //System.out.println("OPERATIONS!!!\n\n\n" + operations.toString());
            // get the operator
            Object operator = identifier(map, env, ((Map<String, Object>)operations.get("Operator")).get("Identifier"));
            //System.out.println("Operator!!!\n\n\n" + operator.toString());

            // make operands list
            List<Object> operands = new ArrayList<Object>();

            // go through operands and put them in list
            // oh my god, this function has so many arbitrary casts
            // and it has recursion
            // literally the worst code I have ever written
            // lord help whoever edits this code next
            // wait, that's me! NOOOOOOOOO
            try {
                for (Object o : (List<Object>) operations.get("Operands")) {
                    operands.add((Object) interp((Map<String, Object>)o,env));
                }
            } catch (Exception e) {
                throw new InterpretationException("Invalid Operand -> " + e.getLocalizedMessage());
            }
            //System.out.println("Operator type!!!\n\n\n" + operator.getClass().getName());
            //System.out.println("Operator!!!\n\n\n" + operator.toString());

            //System.out.println("Operands!!!\n\n\n" + operands.toString());

            // parse with applicable fxn and apply operator
            if (operator.equals("+")) {
                Integer output = (Integer) number(map, env, operands.get(0)) + (Integer) number(map, env, operands.get(1));
                //System.out.println(output);
                return output;
            } else if (operator.equals("-")) {
                Integer output = (Integer) number(map, env, operands.get(0)) - (Integer) number(map, env, operands.get(1));
                //System.out.println(output);
                return output;
            } else if (operator.equals("*")) {
                Integer output = (Integer) number(map, env, operands.get(0)) * (Integer) number(map, env, operands.get(1));
                //System.out.println(output);
                return output;
            } else if (operator.equals("/")) {
                Integer output = (Integer) number(map, env, operands.get(0)) / (Integer) number(map, env, operands.get(1));
                //System.out.println(output);
                return output;
            } else if (operator.equals("==")) {
                boolean output = operands.get(0).equals(operands.get(1));
                //System.out.println(output);
                return output;
            } else if (operator.equals(">")) {
                boolean output = (Integer) number(map, env, operands.get(0)) > (Integer) number(map, env, operands.get(1));
                //System.out.println(output);
                return output;
            } else if (operator.equals("<")) {
                boolean output = (Integer) number(map, env, operands.get(0)) < (Integer) number(map, env, operands.get(1));
                //System.out.println(output);
                return output;
            } else if (operator.equals("print")) {
                // print takes any number of args and prints them all out
                for (Object o : operands) {
                    System.out.println(o.toString());
                }
                return null;
            }  else if (operator != null) {
                // get our parameters
                List<Map<String, Object>> parameters = (List<Map<String, Object>>) ((Map<String, Object>) operator).get("Parameters");
                // make a local env for arguments
                Map<String, Object> localEnv = new HashMap<>();
                // oh my god, closure https://en.wikipedia.org/wiki/Closure_(computer_programming)
                // use closure from fxn
                List<Map<String, Object>> closureEnv = (List<Map<String, Object>>) ((Map<String, Object>) operator).get("ClosureEnv");
                // fall back to dynamic scope if no closureEnv
                List<Map<String, Object>> baseEnv = null;
                if (closureEnv != null) {
                    baseEnv = closureEnv;
                } else {
                    baseEnv = env;
                }
                // make new call environment as a fresh list as to not change closureEnv
                List<Map<String, Object>> callEnv = new ArrayList<>(baseEnv);

                int i = 0;
                // go over parameters and add our operands as parameters
                for (Map<String, Object> parameter : parameters) {
                    localEnv.put((String)(parameter).get("Name"), operands.get(i));
                    i++;
                }

                // extend environment for call with new parameters and eval block
                callEnv.add(localEnv);
                return block(map, callEnv, ((Map<String, Object>) operator).get("Block"));
               
            } else {
                // we are not at a base case
                // this is dead code, but java won't compile without it
                // so it stays here
                throw new InterpretationException("Invalid Operator");
            }
        } catch (Exception e) {
            throw new InterpretationException("Invalid Function Application -> " + e.getLocalizedMessage());
        }
    }

    /**
     * Evaluates a conditional
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    @SuppressWarnings("unchecked")
    private static Object conditional(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        try {
            // cast our conditional
            List<Object> conditional = (List<Object>) value;
            for (Object o : conditional) {
                // Pull out the test expression
                // This can be a map
                // It can also be a list with 1 item, which I genuinely disgust
                Object testNode = ((Map<String, Object>) o).get("Test");

                Object testResult;
                if (testNode instanceof Map) {
                    testResult = interp((Map<String, Object>) testNode, env);
                } else if (testNode instanceof List) {
                    testResult = block(map, env, testNode);
                } else {
                    throw new InterpretationException("Invalid Conditional Test");
                }

                if ((boolean) testResult) {
                    // look at these casts, why java, why?
                    return interp((Map<String, Object>) (((Map<String, Object>) o).get("Consequent")), env);
                }
            }
            return null;
        } catch (Exception e) {
            throw new InterpretationException("Invalid Conditional -> " + e.getLocalizedMessage());
        }
    }

    /**
     * Evaluates a boolean
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    private static Object bool(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            throw new InterpretationException("Invalid Boolean");
        }
    }

    /**
     * Evaluates a name and adds to localEnv
     * 
     * For some reason, there is always a block at the end of a let.
     * 
     * We are counting on that.  If it isn't there, THIS WILL BREAK!
     * 
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    @SuppressWarnings("unchecked")
    private static Object let(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        // cast to map
        Map<String, Object> m = (Map<String, Object>)((Map<String, Object>) value);
        Map<String, Object> localEnv = new HashMap<>();
        // get our parameter
        Object paramName = interp((Map<String, Object>) m.get("Parameter"), env);
        // get the expression value
        Object exprVal = interp((Map<String, Object>) m.get("Expression"), env);
        // if dealing with a lambda, use lexical env
        if (exprVal instanceof Map && ((Map<?, ?>) exprVal).get("Parameters") != null && ((Map<?, ?>) exprVal).get("Block") != null) {
            Map<String, Object> fxnVal = new HashMap<>((Map<String, Object>) exprVal);
            // put a shallow copy of the current env list in for lexical scope
            fxnVal.put("ClosureEnv", new ArrayList<>(env));
            exprVal = fxnVal;
        }
        localEnv.put("" + paramName, exprVal);

        // push local env for the block and ensure it is popped
        env.add(localEnv);
        try {
            return block(map, env, m.get("Block"));
        } finally {
            // it's over :( 
            // locaEnv must die
            env.remove(env.size() - 1);
        }
    }

    /**
     * Evaluates a block
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    @SuppressWarnings("unchecked")
    private static Object block(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        // get parameter, cast to map
        List<Object> block = (List<Object>) value;
        // add to env
        Object finalResult = null;
        for (Object o : block) {
           finalResult = interp((Map<String, Object>) o, env);
        }
        return finalResult;
    }

    /**
     * Evaluates a lambda
     * Does closure stuff, woahhhh
     * https://en.wikipedia.org/wiki/Closure_(computer_programming)
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    @SuppressWarnings("unchecked")
    private static Object lambda(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        try {
            // return a fxn value and attach current env
            if (value instanceof Map) {
                Map<String, Object> fxnVal = new HashMap<>((Map<String, Object>) value);
                fxnVal.put("ClosureEnv", new ArrayList<>(env));
                return fxnVal;
            }
            // return value if for some reason value isn't a map??????
            // maybe this should just throw an exception?
            return value;  
        } catch (Exception e) {
            throw new InterpretationException("Invalid Lambda");
        }

    }

    /**
     * Evaluates a def
     * 
     * Currently this will just put x into whatever local environment 
     * it is supposed to go.  It will not look up and see if anything
     * is called x in a higher env.
     * 
     * will result in 1 x in the environment
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    @SuppressWarnings("unchecked")
    private static Object def(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        try {
            Object envVariable = null;
            Object key = ((Map<String, Object>)((Map<String, Object>)value).get("Parameter")).get("Name");
            
            // run this thru interp to get it down to some base case for storage
            Object newValue = interp((Map<String, Object>)((Map<String, Object>)value).get("Expression"), env);

            // put x in
            if (envVariable == null) {
                env.get(env.size() - 1).put("" + key, newValue);
            }   

            return null;
        } catch (Exception e) {
            throw new InterpretationException("Invalid Definition");
        }

    }

    /**
     * Evaluates an assignment
     *
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    @SuppressWarnings("unchecked")
    private static Object assignment(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        try {
            Object envVariable = null;
            Object key = ((Map<String, Object>)value).get("Identifier");
            
            // run this thru interp to get it down to some base case for storage
            Object newValue = interp((Map<String, Object>)((Map<String, Object>)value).get("Expression"), env);
            // get env variable from env
            for (int i = env.size(); i > 0; i-- ) {
                Map<String, Object> localEnv = env.get(i - 1);
                if (localEnv.get(key) != null) {
                    // update the value with the new one
                    localEnv.put("" + key, newValue);
                    envVariable = localEnv.get(key);
                }
            }

            // make sure it exists
            if (envVariable == null) {
                throw new InterpretationException("Undefined Identifier -> " + key.toString());
            }   

            return null;
        } catch (Exception e) {
            throw new InterpretationException("Invalid Assignment");
        }

    }

    /**
     * Does basic interpretation of Identifiers, Strings, and Numbers (32 bit ints), Assignment (Let), Conditionals, Booleans,
     * Blocks, Names, and Functions (Lambdas)
     * Given a map and an environment.
     * 
     * @param map The map which represents the JSON input from ./parse
     * @param envMap The environment variables map list
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    public static Object interp(Map<String, Object> map, List<Map<String, Object>> env) {
        // go thru the map and find associations
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + ": " + entry.getValue());

            String key = entry.getKey();
            Object value = entry.getValue();
            
            try {
                if (key.equals("String")) {
                    return string(map, env, value);
                } else if (key.equals("Number")) {
                    return number(map, env, value);
                } else if (key.equals("Identifier")) {
                    return identifier(map, env, value);
                } else if (key.equals("Application")) {
                    return application(map, env, value);
                } else if (key.equals("Cond")) {
                    return conditional(map, env, value);
                } else if (key.equals("Boolean")) {
                    return bool(map, env, value);
                } else if (key.equals("Let")) {
                    return let(map, env, value);
                } else if (key.equals("Block")) {
                    return block(map, env, value);
                } else if (key.equals("Name")) {
                    return name(map, env, value);
                } else if (key.equals("Def")) {
                    return def(map, env, value);
                } else if (key.equals("Assignment")) {
                    return assignment(map, env, value);
                } else if (key.equals("Lambda")) {
                    return lambda(map, env, value);
                } else {
                    throw new InterpretationException("Invalid Type");
                }
            } catch (InterpretationException e) {
                throw e;
            }
        }
        return false;
    }
}
