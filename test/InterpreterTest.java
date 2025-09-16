package edu.ncsu.csc411.public_test_cases;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc411.ps02.simulation.RunSimulation;

/**
 * Tests the interpreter class
 * @author Warren Long
 */
public class InterpreterTest {
    /**
	 * tests interp
	 */
	@Test
	public void testInterp() {
		for (int i = 1; i < 6; i++) {
			if (!testEnvironment(i)) {
				assertTrue(false);
			}
		}
		assertTrue(true);
	}
}
