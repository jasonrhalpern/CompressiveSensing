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
		
		Matrix testOneMatrix = new SparseMatrix(4, 4);
		testOneMatrix.set(0, 0, 3.2);
		testOneMatrix.set(0, 2, 4.1);
		testOneMatrix.set(1, 1, 9.7);
		testOneMatrix.set(2, 2, 8.8);
		testOneMatrix.set(3, 0, 6.1);
		
		Matrix testTwoMatrix = MatrixHelper.toSingleColumn(testOneMatrix);
		
		assertEquals(testOneMatrix.get(0, 0), testTwoMatrix.get(0, 0), DELTA);
		assertEquals(testOneMatrix.get(0, 2), testTwoMatrix.get(8, 0), DELTA);
		assertEquals(testOneMatrix.get(1, 1), testTwoMatrix.get(5, 0), DELTA);
		assertEquals(testOneMatrix.get(2, 2), testTwoMatrix.get(10, 0), DELTA);
		assertEquals(testOneMatrix.get(3, 0), testTwoMatrix.get(3, 0), DELTA);
	}
}
