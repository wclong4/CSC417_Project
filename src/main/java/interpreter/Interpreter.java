package interpreter;

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
                System.out.println("\"" + value + "\"" );
                return true;
            } else if (key == "Number") {
                try {
                    int number = (int) value;
                    System.out.println(number);
                    return true;
                } catch (Exception e) {
                    System.out.println("Invalid Number");
                    return false;
                }
            } else if (key == "Identifier") {
                Object o = envMap.get(value);
                if (o == null) {
                    System.out.println("Invalid Identifier");
                    return false;
                }
                System.out.println(o);
                return true;
            }
        }
        return false;
    }

}