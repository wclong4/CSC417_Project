package interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This takes in input from the interpreter which comes in JSON format.
 * It is able to take in Identifiers, Strings, and Numbers (32 bit integers)
 * @author Warren Long (wclong4)
 * 
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
    public static boolean identifier(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        Object o = envMap.get(value);
        if (o == null) {
            System.out.println("Invalid Identifier");
            return false;
        }
        try {
            int number = (int) o;
            System.out.println(number);
            return true;
        } catch (Exception e) {
            System.out.println("\"" + o + "\"");
            return true;
        }
    }

    /**
     * Parses a Number
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static boolean number(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        try {
            int number = (int) value;
            System.out.println(number);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid Number");
            return false;
        }
    }

    /**
     * Parses a String
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    public static boolean string(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        System.out.println("\"" + value + "\"" );
        return true;
    }

    /**
     * Parses an Application
     * 
     * Does a crazy amount of unchecked casts and is overall a disgusting function.
     * 
     * If anything fails, it just returns false, so if those casts fail, it just returns false
     * 
     * @param map the map for the parameters
     * @param envMap the map for the environment variables
     * @param value the value gotten from the key
     * @return a boolean 
     */
    @SuppressWarnings("unchecked")
    public static boolean application(Map<String, Object> map, Map<String, Object> envMap, Object value) {
        try {
            // when we get the value it should have an operator and operands
            Map<String, Object> operations = (Map<String, Object>) value;
            //System.out.println("OPERATIONS!!!\n\n\n" + operations.toString());
            // get the operator
            //System.out.println("Operator thingy!!!\n\n\n" + operations.get("Operator").toString());

            String operator = envMap.get(((Map<String, Object>)operations.get("Operator")).get("Identifier")).toString();
            //System.out.println("Operator!!!\n\n\n" + operator.toString());

            // make operands list
            List<Integer> operands = new ArrayList<Integer>();

            // go through operands and put them in list
            for (Object o : (List<Object>) operations.get("Operands")) {
                operands.add((Integer) (((Map<String, Object>) o).get("Number")));
            }
            //System.out.println("Operands!!!\n\n\n" + operands.toString());

            // apply operator
            if (operator.equals("+")) {
                Integer output = operands.get(0) + operands.get(1);
                System.out.println(output);
                return true;
            } else if (operator.equals("-")) {
                Integer output = operands.get(0) - operands.get(1);
                System.out.println(output);
                return true;
            } else if (operator.equals("*")) {
                Integer output = operands.get(0) * operands.get(1);
                System.out.println(output);
                return true;
            } else if (operator.equals("/")) {
                Integer output = operands.get(0) / operands.get(1);
                System.out.println(output);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
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
    public static boolean interp(Map<String, Object> map, Map<String, Object> envMap) {
        // go thru the map and find associations
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + ": " + entry.getValue());

            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == "String") {
                return string(map, envMap, value);
            } else if (key == "Number") {
                return number(map, envMap, value);
            } else if (key == "Identifier") {
                return identifier(map, envMap, value);
            } else if (key == "Application") {
                return application(map, envMap, value);
            }
        }
        return false;
    }

}