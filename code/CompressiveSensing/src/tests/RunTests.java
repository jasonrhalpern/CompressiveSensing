/** \file
*/
package tests;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class RunTests {

	
	public static void main(String[] args){
		/**
		Run all of our tests
		*/
		TestSuite suite = new TestSuite();
		suite.addTestSuite(SignalTesting.class);
		suite.addTestSuite(TestCases.class);
		TestRunner.run(suite);
	}
}
