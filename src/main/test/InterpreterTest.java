import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import interpreter.Interpreter;

/**
 * Tests the interpreter class
 * @author Warren Long
 */
public class InterpreterTest {
	/**
	 * our env map
	 */
	List<Map<String, Object>> env;

	/**
	 * 2,147,483,648
	 */
	String s1BadInt = "{\"Number\": 2147483648}";

	/**
	 * -2,147,483,649
	 */
	String s2BadInt = "{\"Number\": -2147483649}";

	/**
	 * 2,147,483,647
	 */
	String s1GoodInt = "{\"Number\": 2147483647}";

	/**
	 * -2,147,483,648
	 */
	String s2GoodInt = "{\"Number\": -2147483648}";


	/**
	 * add(1,2)
	 */
	String s1Fxn = "{\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Number\": 1}, {\"Number\": 2}]}}";

	/**
	 * add(x, 1)
	 */
	String s2Fxn = "{\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Identifier\": \"x\"}, {\"Number\": 1}]}}";

	/**
	 * div(mul(add(100,2), 3), 2)
	 */
	String s3Fxn = "{\"Application\": {\"Operator\": {\"Identifier\": \"div\"}, \"Operands\": [{\"Application\": {\"Operator\": {\"Identifier\": \"mul\"}, \"Operands\": [{\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Number\": 100}, {\"Number\": 2}]}}, {\"Number\": 3}]}}, {\"Number\": 2}]}}";

	/**
	 * mul(div(100,3), 3)
	 */
	String s4Fxn = "{\"Application\": {\"Operator\": {\"Identifier\": \"mul\"}, \"Operands\": [{\"Application\": {\"Operator\": {\"Identifier\": \"div\"}, \"Operands\": [{\"Number\": 100}, {\"Number\": 3}]}}, {\"Number\": 3}]}}";

	/**
	 * ==(5, add(2,3))
	 */
	String s5Fxn = "{\"Application\": {\"Operator\": {\"Identifier\": \"==\"}, \"Operands\": [{\"Number\": 5}, {\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Number\": 2}, {\"Number\": 3}]}}]}}";

	/**
	 * ==(add(5, 5), add(2,3))
	 */
	String s6Fxn = "{\"Application\": {\"Operator\": {\"Identifier\": \"==\"}, \"Operands\": [{\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Number\": 5}, {\"Number\": 5}]}}, {\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Number\": 2}, {\"Number\": 3}]}}]}}";

	/**
	 * if ==(add(5,5), add(2,3)) then "Not equal" else "Equal"
	 */
	String s1Cond = "{\"Cond\": [{\"Test\": {\"Application\": {\"Operator\": {\"Identifier\": \"==\"}, \"Operands\": [{\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Number\": 5}, {\"Number\": 5}]}}, {\"Application\": {\"Operator\": {\"Identifier\": \"add\"}, \"Operands\": [{\"Number\": 2}, {\"Number\": 3}]}}]}}, \"Consequent\": {\"String\": \"Not equal\"}}, {\"Test\": {\"Boolean\": \"true\"}, \"Consequent\": {\"String\": \"Equal\"}}]}";

	/**
	 * mul(if ==(5,5) then 100 else 10, 3)
	 */
	String s2Cond = "{\"Application\": {\"Operator\": {\"Identifier\": \"mul\"}, \"Operands\": [{\"Cond\": [{\"Test\": {\"Application\": {\"Operator\": {\"Identifier\": \"==\"}, \"Operands\": [{\"Number\": 5}, {\"Number\": 5}]}}, \"Consequent\": {\"Number\": 100}}, {\"Test\": {\"Boolean\": \"true\"}, \"Consequent\": {\"Number\": 10}}]}, {\"Number\": 3}]}}";

	/**
	 * {let a = 123; mul(a,2)}
	 */
	String s1Block = "{\"Block\": [{\"Let\": {\"Parameter\": {\"Name\": \"a\", \"Type\": \":untyped\"}, \"Expression\": {\"Number\": 123}, \"Block\": [{\"Application\": {\"Operator\": {\"Identifier\": \"mul\"}, \"Operands\": [{\"Identifier\": \"a\"}, {\"Number\": 2}]}}]}}]}";

	/**
	 * {let a = 123; mul(a,b); let b = 2}  (Should report an error!)
	 */
	String s2Block = "{\"Block\": [{\"Let\": {\"Parameter\": {\"Name\": \"a\", \"Type\": \":untyped\"}, \"Expression\": {\"Number\": 123}, \"Block\": [{\"Application\": {\"Operator\": {\"Identifier\": \"mul\"}, \"Operands\": [{\"Identifier\": \"a\"}, {\"Identifier\": \"b\"}]}}, {\"Let\": {\"Parameter\": {\"Name\": \"b\", \"Type\": \":untyped\"}, \"Expression\": {\"Number\": 2}, \"Block\": []}}]}}]}";

	/**
	 * {let a = 123; let b = 2; mul(a,b)}
	 */
	String s3Block = "{\"Block\": [{\"Let\": {\"Parameter\": {\"Name\": \"a\", \"Type\": \":untyped\"}, \"Expression\": {\"Number\": 123}, \"Block\": [{\"Let\": {\"Parameter\": {\"Name\": \"b\", \"Type\": \":untyped\"}, \"Expression\": {\"Number\": 2}, \"Block\": [{\"Application\": {\"Operator\": {\"Identifier\": \"mul\"}, \"Operands\": [{\"Identifier\": \"a\"}, {\"Identifier\": \"b\"}]}}]}}]}}]}";

	/**
	 * Citation, used stack overflow
	 * https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
	 */
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@After
	public void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}
	/**
	 * our setup, sets up initial env
	 */
	@Before
    public void setup() {   
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
        envMap.put("print", "print");
        env = new LinkedList<>();
        env.add(envMap);
    }
	/**
	 * Helps parse a file
	 * @param file the file to parse
	 * @return the parsed file string
	 */
	public String parseFileHelper(String file) {
		try {
			ProcessBuilder pb = new ProcessBuilder("./parse", "-f", file);
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			String output = "";
			while ((line = reader.readLine()) != null) {
				output += line;
			}
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return "failed";

	}
    /**
	 * tests basic fxn 1
	 */
	@Test
	public void testFxns1() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s1Fxn);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(3, (int) o);
	}

	/**
	 * tests basic fxn 2
	 */
	@Test
	public void testFxns2() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s2Fxn);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(11, (int) o);
	}

	/**
	 * tests basic fxn 3
	 */
	@Test
	public void testFxns3() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s3Fxn);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(153, (int) o);
	}

	/**
	 * tests basic fxn 4
	 */
	@Test
	public void testFxns4() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s4Fxn);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(99, (int) o);
	}

	/**
	 * tests basic fxn 5
	 */
	@Test
	public void testFxns5() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s5Fxn);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(true, (boolean) o);
	}

	/**
	 * tests basic fxn 6
	 */
	@Test
	public void testFxns6() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s6Fxn);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(false, (boolean) o);
	}

	/**
	 * tests basic cond 1
	 */
	@Test
	public void testCond1() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s1Cond);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals("Equal", (String) o);
	}

	/**
	 * tests basic cond 2
	 */
	@Test
	public void testCond2() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s2Cond);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(300, (int) o);
	}

	/**
	 * tests basic block 1
	 */
	@Test
	public void testBlock1() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s1Block);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(246, (int) o);
	}

	/**
	 * tests basic block 2
	 */
	@Test
	public void testBlock2() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s2Block);
			Interpreter.interp(map, env);
		} catch (Exception e) {
			e.printStackTrace();
			// it should fail here, error message doesn't really matter
			return;
		}
		// it should not get here
        fail();
	}

	/**
	 * tests basic block 3
	 */
	@Test
	public void testBlock3() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s3Block);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(246, (int) o);
	}

	/**
	 * tests bad int 1
	 */
	@Test
	public void testBadInt1() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s1BadInt);
			Interpreter.interp(map, env);
		} catch (Exception e) {
			e.printStackTrace();
			// it should fail here, error message doesn't really matter
			return;
		}
		// it should not get here
        fail();
	}

	/**
	 * tests bad int 2
	 */
	@Test
	public void testBadInt2() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s2BadInt);
			Interpreter.interp(map, env);
		} catch (Exception e) {
			e.printStackTrace();
			// it should fail here, error message doesn't really matter
			return;
		}
		// it should not get here
        fail();
	}

	/**
	 * tests good int 1
	 */
	@Test
	public void testGoodInt1() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s1GoodInt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(2147483647, (int) o);
	}

	/**
	 * tests good int 2
	 */
	@Test
	public void testGoodInt2() {
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(s2GoodInt);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(-2147483648, (int) o);
	}

	/**
	 * tests dynamic factorial
	 * @throws IOException 
	 */
	@Test
	public void testDynamicFxn1() throws IOException {
		String output = parseFileHelper("fact-let.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
			Interpreter.interp(map, env);
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * tests dynamic scope
	 * @throws IOException 
	 */
	@Test
	public void testDynamicFxn2() throws IOException {
		String output = parseFileHelper("lexical-scope.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(10005, (int) o);
	}

	/**
	 * tests dynamic fib
	 * @throws IOException 
	 */
	@Test
	public void testDynamicFxn3() throws IOException {
		String output = parseFileHelper("fib_13.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
			Interpreter.interp(map, env);
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * tests dynamic fib
	 * @throws IOException 
	 */
	@Test
	public void testDynamicFxn4() throws IOException {
		String output = parseFileHelper("fib_20.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
			Interpreter.interp(map, env);
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * tests dynamic fib
	 * @throws IOException 
	 */
	@Test
	public void testDynamicFxn5() throws IOException {
		String output = parseFileHelper("fact-cp3.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		assertEquals(479001600, (int) o);
	}

	/**
	 * tests assignment
	 * @throws IOException 
	 */
	@Test
	public void testAssignment1() throws IOException {
		String output = parseFileHelper("testAssignment1.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(2, (int) o);
	}

	/**
	 * tests assignment
	 * @throws IOException 
	 */
	@Test
	public void testAssignment2() throws IOException {
		String output = parseFileHelper("testAssignment2.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(4, (int) o);
	}

	/**
	 * tests assignment
	 * @throws IOException 
	 */
	@Test
	public void testAssignment3() throws IOException {
		String output = parseFileHelper("testAssignment3.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
			Interpreter.interp(map, env);
			// should not get here
			fail();
		} catch (Exception e) {
			String actual = e.getMessage();
			assertEquals("Invalid Assignment", actual);

		}
	}

	/**
	 * tests def
	 * @throws IOException 
	 */
	@Test
	public void testDef1() throws IOException {
		String output = parseFileHelper("testDef1.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(1, (int) o);
	}

	/**
	 * tests def
	 * @throws IOException 
	 */
	@Test
	public void testDef2() throws IOException {
		String output = parseFileHelper("testDef2.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(4, (int) o);
	}

	/**
	 * tests def
	 * @throws IOException 
	 */
	@Test
	public void testDef3() throws IOException {
		String output = parseFileHelper("testDef3.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(233, (int) o);
	}

	/**
	 * tests def
	 * @throws IOException 
	 */
	@Test
	public void testDef4() throws IOException {
		String output = parseFileHelper("testDef4.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(720, (int) o);
	}

	/**
	 * tests def
	 * @throws IOException 
	 */
	@Test
	public void testDef5() throws IOException {
		String output = parseFileHelper("testDef5.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(false, (boolean) o);
	}

	/**
	 * tests def
	 * @throws IOException 
	 */
	@Test
	public void testDef6() throws IOException {
		String output = parseFileHelper("testDef6.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(true, (boolean) o);
	}

	/**
	 * tests testAssignFail
	 * @throws IOException 
	 */
	@Test
	public void testAssignFail() throws IOException {
		String output = parseFileHelper("assign-fail.417");
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
			Interpreter.interp(map, env);
			// should not get here
			fail();
		} catch (Exception e) {
		}
	}

	/**
	 * tests assign outer
	 * @throws IOException 
	 */
	@Test
	public void testAssignOuter() throws IOException {
		String output = parseFileHelper("assign-outer.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(2, (int) o);
	}

	/**
	 * tests assign outer 2
	 * @throws IOException 
	 */
	@Test
	public void testAssignOuter2() throws IOException {
		String output = parseFileHelper("assign-outer2.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(1, (int) o);
	}

	/**
	 * tests counter
	 * @throws IOException 
	 */
	@Test
	public void testCounter() throws IOException {
		String output = parseFileHelper("counter.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Interpreter.interp(map, env);
    	assertEquals("1\n2\n3\n4\n5\n", outContent.toString());
	}

	/**
	 * tests even-odd
	 * @throws IOException 
	 */
	@Test
	public void testEvenOdd() throws IOException {
		String output = parseFileHelper("even-odd.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Interpreter.interp(map, env);
    	assertEquals("Is 10 odd?\nfalse\nIs 20 even?\ntrue\n" + //
						"", outContent.toString());
	}

	/**
	 * tests fact-let
	 * @throws IOException 
	 */
	@Test
	public void testFactLet() throws IOException {
		String output = parseFileHelper("fact=let.417");
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
			Interpreter.interp(map, env);
			// should not get here
			fail();
		} catch (Exception e) {
		}
	}

	/**
	 * tests fact
	 * @throws IOException 
	 */
	@Test
	public void testFact() throws IOException {
		String output = parseFileHelper("fact.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(720, (int) o);
	}

	/**
	 * tests for
	 * @throws IOException 
	 */
	@Test
	public void testFor() throws IOException {
		String output = parseFileHelper("forLoops.417");
		// parse the Json from whatever string we want for this test
		Map<String, Object> map = null;
		try {
			map = Interpreter.parseJson(output);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
        Object o = Interpreter.interp(map, env);
		System.out.println(o);
		assertEquals(null, o);
	}
}
