package tests;

import static org.junit.Assert.assertEquals;

import matrix.MatrixHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;
import org.junit.Test;

public class SignalTesting {
	
	private static final double DELTA = 1e-15;

	@Test
	//test the function that turns an entire matrix into a single column vector
	public void singleColumnTest(){
		
		Matrix testOneMatrix = new SparseMatrix(5, 4);
		testOneMatrix.set(0, 0, 3.2);
		testOneMatrix.set(0, 2, 4.1);
		testOneMatrix.set(1, 1, 9.7);
		testOneMatrix.set(2, 2, 8.8);
		testOneMatrix.set(3, 0, 6.1);
		
		Matrix testTwoMatrix = MatrixHelper.toSingleColumn(testOneMatrix);
		
		assertEquals(testOneMatrix.get(0, 0), testTwoMatrix.get(0, 0), DELTA);
		assertEquals(testOneMatrix.get(0, 2), testTwoMatrix.get(10, 0), DELTA);
		assertEquals(testOneMatrix.get(1, 1), testTwoMatrix.get(6, 0), DELTA);
		assertEquals(testOneMatrix.get(2, 2), testTwoMatrix.get(12, 0), DELTA);
		assertEquals(testOneMatrix.get(3, 0), testTwoMatrix.get(3, 0), DELTA);
	}
	
	@Test
	//test the absolute value function
	public void absValueTest(){
		
		Matrix testMatrix = new SparseMatrix(2, 2);
		testMatrix.set(0, 0, -1);
		testMatrix.set(0, 1, -2);
		testMatrix.set(1, 0, 3);
		testMatrix.set(1, 1, -4);
		
		testMatrix = MatrixHelper.getAbsMatrix(testMatrix);
		
		assertEquals(testMatrix.get(0, 0), 1, DELTA);
		assertEquals(testMatrix.get(0, 1), 2, DELTA);
		assertEquals(testMatrix.get(1, 0), 3, DELTA);
		assertEquals(testMatrix.get(1, 1), 4, DELTA);
	}
	
	@Test
	public void sortDescTest(){
		
		Matrix test = new SparseMatrix(4,4);
		test.set(0, 0, 9);
		test.set(0, 1, 2);
		test.set(0, 2, 3);
		test.set(1, 0, 1);
		test.set(1, 1, 8);
		test.set(1, 2, 7);
		test.set(2, 0, 4);
		test.set(2, 1, 5);
		test.set(3, 2, 6);
		test = MatrixHelper.sortDescending(test);
		
		assertEquals(test.get(3, 0), 9, DELTA);
		assertEquals(test.get(3, 1), 8, DELTA);
		assertEquals(test.get(3, 2), 7, DELTA);
		assertEquals(test.get(2, 2), 6, DELTA);
	}
}
