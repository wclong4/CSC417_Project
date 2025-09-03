package interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Run {
    
    public static Map<String, Object> getEnv() {
        // set up initial environment
        Map<String, Object> envMap = new HashMap<String, Object>();
        envMap.put("x", (Integer) 10);
        envMap.put("v", (Integer) 5);
        envMap.put("i", (Integer) 1);
        envMap.put("author", "wclong4");
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

    public static boolean interp(Map<String, Object> map) {
        Map<String, Object> envMap = getEnv();
        // go thru the map and find associations
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());

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
    /**
     * Our main method, does all our main stuff, woaaaaahhhhh
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // take in input from stdin
        String s = takeInput();
        // get a map using Jackson
        Map<String, Object> map = Interpreter.parseJson(s);
        boolean goodExit = interp(map);
        if (goodExit) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
