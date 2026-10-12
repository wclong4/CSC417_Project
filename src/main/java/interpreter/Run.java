package interpreter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Used to run the interpreter
 * 
 * @author Warren Long (wclong4)
 */
public class Run {
    /**
     * Gets the environment variables.
     * @return the environment variables
     */
    public static List<Map<String, Object>> getEnv() {   
        // set up initial environment
        Map<String, Object> envMap = new HashMap<String, Object>();
        envMap.put("x", (Integer) 10);
        envMap.put("v", (Integer) 5);
        envMap.put("i", (Integer) 1);
        envMap.put("author", "wclong4");
        envMap.put("add", "+");
        envMap.put("sub", "-");
        envMap.put("mul", "*");
        envMap.put("div", "/");
        envMap.put("==", "==");
        envMap.put(">", ">");
        envMap.put("<", "<");
        envMap.put("print", "print");
        envMap.put("pow", "pow");
        envMap.put("for", "for");
        envMap.put("while", "while");
        envMap.put("sqrt", "sqrt");
        envMap.put("%", "%");
        List<Map<String, Object>> envList = new LinkedList<>();
        envList.add(envMap);
        return envList;
    }

    /**
     * Takes in input from stdin
     */
    public static String takeInput() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();
        return input;
    }

    /**
     * Handles interpretation using Interpreter.interp
     * @param map the map to pass in to interpret
     */
    public static void interpOut(Map<String, Object> map) {
        Object o = Interpreter.interp(map, getEnv());
        if (o == null) {
            System.out.println("null");
            System.exit(0);
        } else if (o instanceof Integer) {
            System.out.println((Integer) o );
            System.exit(0);
        } else if (o instanceof Boolean) {
            System.out.println(o);
            System.exit(0);
        } else if (o instanceof String) {
            System.out.println("\"" + (String) o + "\"");
            System.exit(0);
        } 
    }
    
    /**
     * Our main method, does all our main stuff, woaaaaahhhhh
     * @param args the args (arggggggggggg)
     * @throws Exception if something goes wrong (nothing will go wrong)
     */
    public static void main(String[] args) throws Exception {
        // take in input from stdin
        String s = takeInput();
        // get a map using Jackson

        Map<String, Object> map = Interpreter.parseJson(s);

      
        
        try {
            interpOut(map);
        } catch (Exception e) {
            System.out.println("Interpretation Error -> " + e.getMessage());
            System.exit(1);
        }
    }
}
