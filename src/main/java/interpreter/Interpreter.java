package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This takes in input from the interpreter which comes in JSON format.
 * It is able to take in Identifiers, Strings, and Numbers, Names, Blocks, etc.
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
     * Parses an identifier
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static Object identifier(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        Object envVariable = null;
        // get env variable from env
        for (Map<String, Object> localEnv : env) {
            if (localEnv.get(value) != null) {
                envVariable = localEnv.get(value);
            }
        }
        // make sure it exists
        if (envVariable == null) {
            throw new InterpretationException("Invalid Identifier -> " + value.toString());
        }
        try {
            return number(map, env, envVariable);
        } catch (Exception e) {
            if (envVariable.getClass().getName().equals("java.lang.String")) {
                return string(map, env, envVariable);
            } else {
                // just return it as an object
                return envVariable;
            }
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
            //System.out.println("Operator thingy!!!\n\n\n" + operations.get("Operator").toString());

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
                // make a local env
                Map<String, Object> localEnv = new HashMap<>();
                int i = 0;
                // go over parameters and add our operands as parameters
                for (Map<String, Object> parameter : parameters) {
                    localEnv.put((String)(parameter).get("Name"), operands.get(i));
                    i++;
                }
                // add to env and pop after eval
                env.add(localEnv);
                try {
                    // eval function body
                    return block(map, env, ((Map<String, Object>) operator).get("Block"));
                } finally {
                    // pop the local env
                    // it is so over
                    // local env is going to meet its maker :( 
                    env.remove(env.size() - 1);
                }
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
                // this is awful
                Boolean b = (Boolean) interp((Map<String, Object>)(((Map<String, Object>)o).get("Test")), env);
                if (b) {
                    // look at these casts, why java, why?
                    return interp((Map<String, Object>)(((Map<String, Object>)o).get("Consequent")), env);
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
        localEnv.put("" + interp((Map<String, Object>) m.get("Parameter"), env),
            interp((Map<String, Object>) m.get("Expression"), env));
        // add local env, and then get rid of it
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
     * @return an object of some sort
     * @throws InterpretationException if anything goes wrong
     */
    private static Object lambda(Map<String, Object> map, List<Map<String, Object>> env, Object value) {
        // get value, cast to map
        //Map<String, Object> subMap = (Map<String, Object>) value;
        //Map<String, Object> parameters = (Map<String, Object>) subMap.get("Parameters");
        //Map<String, Object> block = (Map<String, Object>) subMap.get("Block");

        // just return the value, 0 processing
        return value;      
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
