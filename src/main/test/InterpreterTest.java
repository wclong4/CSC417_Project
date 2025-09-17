import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;


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
	Map<String, Object> envMap;

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
	 * our setup, sets up initial env
	 */
	@Before
	public void setup() {
		envMap = new HashMap<String, Object>();
        // set up initial environment
        envMap.put("x", (Integer) 10);
        envMap.put("v", (Integer) 5);
        envMap.put("i", (Integer) 1);
        envMap.put("author", "wclong4");
        envMap.put("add", "+");
        envMap.put("sub", "-");
        envMap.put("mul", "*");
        envMap.put("div", "/");
        envMap.put("==", "==");
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
        Object o = Interpreter.interp(map, envMap);
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
			Interpreter.interp(map, envMap);
		} catch (Exception e) {
			e.printStackTrace();
			// it should fail here, error message doesn't really matter
			return;
		}
		// it should not hget here
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
        Object o = Interpreter.interp(map, envMap);
		assertEquals(246, (int) o);
	}
}
