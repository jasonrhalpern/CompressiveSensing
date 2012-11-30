package tests;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class RunTests {

	public static void main(String[] args){
		TestSuite suite = new TestSuite();
		suite.addTestSuite(SignalTesting.class);
		TestRunner.run(suite);
	}
}
