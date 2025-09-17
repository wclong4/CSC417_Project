package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This takes in input from the interpreter which comes in JSON format.
 * It is able to take in Identifiers, Strings, and Numbers (32 bit integers)
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
    public static Object identifier(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        Object o = envMap.get(value);
        if (o == null) {
            return false;
        }
        try {
            return number(map, envMap, o);
        } catch (Exception e) {
            return string(map, envMap, o);
        }
    }

    /**
     * Parses a Number
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static Object number(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        try {
            int number = (int) value;
            //System.out.println(number);
            return number;
        } catch (Exception e) {
            throw new InterpretationException("Invalid Number");
        }
    }

    /**
     * Parses a String
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static Object string(Map<String, Object> map, Map<String, Object> envMap, Object value) {
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
    public static Object name(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        //System.out.println("\"" + value + "\"" );
        return "" + value;
    }

    /**
     * Parses an Application
     * 
     * Does a crazy amount of unchecked casts and is overall a disgusting function.
     * 
     * If anything fails, it just throws an error, so if a cast fails, it just returns false
     * 
     * Also, it has some crazy recursive nonsense where it calls interp, which then calls application
     * 
     * This is the worst code written by all mankind for all time.
     * 
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    @SuppressWarnings("unchecked")
    public static Object application(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        try {
            // when we get the value it should have an operator and operands
            Map<String, Object> operations = (Map<String, Object>) value;
            //System.out.println("OPERATIONS!!!\n\n\n" + operations.toString());
            // get the operator
            //System.out.println("Operator thingy!!!\n\n\n" + operations.get("Operator").toString());

            String operator = envMap.get(((Map<String, Object>)operations.get("Operator")).get("Identifier")).toString();
            //System.out.println("Operator!!!\n\n\n" + operator.toString());

            // make operands list
            List<Object> operands = new ArrayList<Object>();

            // go through operands and put them in list
            // oh my god, this function has so many arbitrary casts
            // and it has recursion
            // literally the worst code I have ever written
            // lord help whoever edits this code next
            // wait, that's me! NOOOOOOOOO
            for (Object o : (List<Object>) operations.get("Operands")) {
                operands.add((Object) interp((Map<String, Object>)o,envMap));
            }
            //System.out.println("Operands!!!\n\n\n" + operands.toString());

            // apply operator
            if (operator.equals("+")) {
                Integer output = (Integer) operands.get(0) + (Integer) operands.get(1);
                //System.out.println(output);
                return output;
            } else if (operator.equals("-")) {
                Integer output = (Integer) operands.get(0) - (Integer) operands.get(1);
                //System.out.println(output);
                return output;
            } else if (operator.equals("*")) {
                Integer output = (Integer) operands.get(0) * (Integer) operands.get(1);
                //System.out.println(output);
                return output;
            } else if (operator.equals("/")) {
                Integer output = (Integer) operands.get(0) / (Integer) operands.get(1);
                //System.out.println(output);
                return output;
            } else if (operator.equals("==")) {
                boolean output = operands.get(0).equals(operands.get(1));
                //System.out.println(output);
                return output;
            }
            throw new InterpretationException("Invalid Application");
        } catch (Exception e) {
            throw new InterpretationException("Invalid Application");
        }
    }

    /**
     * Evaluates a conditional
     * @return an object of some sort
     */
    @SuppressWarnings("unchecked")
    private static Object conditional(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        try {
            // cast our conditional
            List<Object> conditional = (List<Object>) value;
            for (Object o : conditional) {
                // this is awful
                Boolean b = (Boolean) interp((Map<String, Object>)(((Map<String, Object>)o).get("Test")), envMap);
                if (b) {
                    // look at these casts, why java, why?
                    return interp((Map<String, Object>)(((Map<String, Object>)o).get("Consequent")), envMap);
                }
            }
            return null;
        } catch (Exception e) {
            throw new InterpretationException("Invalid Conditional");
        }
    }

    /**
     * Evaluates a boolean
     * @return an object of some sort
     */
    private static Object bool(Map<String, Object> map, Map<String, Object> envMap, Object value) {
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
     */
    @SuppressWarnings("unchecked")
    private static Object let(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        // cast to map
        Map<String, Object> m = (Map<String, Object>)((Map<String, Object>) value);
        // add to env
        envMap.put("" + interp((Map<String, Object>) m.get("Parameter"), envMap), 
            interp((Map<String, Object>) m.get("Expression"), envMap));

        return block(map, envMap, m.get("Block"));
    }

    /**
     * Evaluates a block
     * @return an object of some sort
     */
    @SuppressWarnings("unchecked")
    private static Object block(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        // get parameter, cast to map
        List<Object> block = (List<Object>) value;
        // add to env
        Object finalResult = null;
        for (Object o : block) {
           finalResult = interp((Map<String, Object>) o, envMap);
        }
        return finalResult;
    }

    /**
     * Does basic interpretation of Identifiers, Strings, and Numbers (32 bit ints) 
     * given a map and an environment.
     * 
     * @param map The map which represents the JSON input from ./parse
     * @param envMap The environment variables map
     * @return If an identifier does not exist, or a number is outside the range of a 
     * 32 bit int, otherwise it returns true
     */
    public static Object interp(Map<String, Object> map, Map<String, Object> envMap) {
        // go thru the map and find associations
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + ": " + entry.getValue());

            String key = entry.getKey();
            Object value = entry.getValue();
            
            try {
                if (key.equals("String")) {
                    return string(map, envMap, value);
                } else if (key.equals("Number")) {
                    return number(map, envMap, value);
                } else if (key.equals("Identifier")) {
                    return identifier(map, envMap, value);
                } else if (key.equals("Application")) {
                    return application(map, envMap, value);
                } else if (key.equals("Cond")) {
                    return conditional(map, envMap, value);
                } else if (key.equals("Boolean")) {
                    return bool(map, envMap, value);
                } else if (key.equals("Let")) {
                    return let(map, envMap, value);
                } else if (key.equals("Block")) {
                    return block(map, envMap, value);
                } else if (key.equals("Name")) {
                    return name(map, envMap, value);
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