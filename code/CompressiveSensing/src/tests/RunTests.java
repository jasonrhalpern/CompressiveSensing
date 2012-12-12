package tests;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This kicks of all the tests for the program.
 * 
 * @author Jason Halpern
 * @version 1.0 12/09/12
 *
 */

public class RunTests {

	
	public static void main(String[] args){
		//Run all of our tests
		TestSuite suite = new TestSuite();
		suite.addTestSuite(SignalTesting.class);
		suite.addTestSuite(TestCases.class);
		TestRunner.run(suite);
	}
}
