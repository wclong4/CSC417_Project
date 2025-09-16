package interpreter;

import java.util.HashMap;
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
    public static Map<String, Object> getEnv() {   
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
        return envMap;
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
     * Our main method, does all our main stuff, woaaaaahhhhh
     * @param args the args (arggggggggggg)
     * @throws Exception if something goes wrong (nothing will go wrong)
     */
    public static void main(String[] args) throws Exception {
        // take in input from stdin
        String s = takeInput();
        // get a map using Jackson
        Map<String, Object> map = Interpreter.parseJson(s);
        boolean goodExit = Interpreter.interp(map, getEnv());
        if (goodExit) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
