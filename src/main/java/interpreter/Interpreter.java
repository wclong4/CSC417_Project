package interpreter;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Warren Long
 * This takes in input from the interpreter which comes in JSON format.
 * It needs to be able to take Identifiers, Strings, and Numbers (32 bit integers)
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
}