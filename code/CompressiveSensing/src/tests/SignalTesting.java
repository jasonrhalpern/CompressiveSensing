package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
		
		List<Matrix> list = new ArrayList<Matrix>();
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
		list = MatrixHelper.sortDescending(test);
		test = list.get(0);
		
		assertEquals(test.get(0, 0), 9, DELTA);
		assertEquals(test.get(0, 1), 8, DELTA);
		assertEquals(test.get(0, 2), 7, DELTA);
		assertEquals(test.get(1, 2), 6, DELTA);
	}
	
	@Test
	public void notEqualTest(){
		
		Matrix test = new SparseMatrix(7,1);
		test.set(0, 0, 9);
		test.set(1, 0, 2);
		test.set(2, 0, 3);
		test.set(3, 0, 1);
		test.set(4, 0, 1);
		test.set(5, 0, 7);
		test.set(6, 0, 1);
		test = MatrixHelper.notEqual(test, 1);
		
		assertEquals(test.get(0, 0), 1, DELTA);
		assertEquals(test.get(1, 0), 1, DELTA);
		assertEquals(test.get(2, 0), 1, DELTA);
		assertEquals(test.get(3, 0), 0, DELTA);
		assertEquals(test.get(4, 0), 0, DELTA);
		assertEquals(test.get(5, 0), 1, DELTA);
		assertEquals(test.get(6, 0), 0, DELTA);
	}
	
	@Test
	public void indexTest(){
		
		Matrix test = new SparseMatrix(4,4);
		test.set(0, 0, 9);
		test.set(0, 1, 2);
		test.set(0, 2, 3);
		test.set(0, 3, 1);
		test.set(1, 0, 1);
		test.set(1, 1, 7);
		test.set(1, 2, 9);
		test.set(1, 3, 6);
		test.set(2, 0, 14);
		test.set(2, 1, 0);
		test.set(2, 2, 4);
		test.set(2, 3, 7);
		test.set(3, 0, 3);
		test.set(3, 1, 3);
		test.set(3, 2, 6);
		test.set(3, 3, 5);
		test = MatrixHelper.getIndices(test, 4, 11);
		
		assertEquals(test.get(0, 0), 3, DELTA);
		assertEquals(test.get(1, 0), 2, DELTA);
		assertEquals(test.get(2, 0), 7, DELTA);
		assertEquals(test.get(3, 0), 0, DELTA);
		assertEquals(test.get(4, 0), 3, DELTA);
		assertEquals(test.get(5, 0), 3, DELTA);
		assertEquals(test.get(6, 0), 9, DELTA);
		assertEquals(test.get(7, 0), 4, DELTA);
	}
	
	@Test
	public void findNonzerosTest(){
		
		Matrix test = new SparseMatrix(4,4);
		test.set(0, 0, 9);
		test.set(0, 1, 0);
		test.set(0, 2, 3);
		test.set(0, 3, 1);
		test.set(1, 0, 1);
		test.set(1, 1, 0);
		test.set(1, 2, 9);
		test.set(1, 3, 6);
		test.set(2, 0, 14);
		test.set(2, 1, 0);
		test.set(2, 2, 0);
		test.set(2, 3, 7);
		test.set(3, 0, 3);
		test.set(3, 1, 3);
		test.set(3, 2, 6);
		test.set(3, 3, 5);
		test = MatrixHelper.findNonzero(test);
		
		assertEquals(test.get(0, 0), 1, DELTA);
		assertEquals(test.get(1, 0), 2, DELTA);
		assertEquals(test.get(2, 0), 3, DELTA);
		assertEquals(test.get(3, 0), 4, DELTA);
		assertEquals(test.get(4, 0), 8, DELTA);
		assertEquals(test.get(5, 0), 9, DELTA);
		assertEquals(test.get(6, 0), 10, DELTA);
		assertEquals(test.get(7, 0), 12, DELTA);
		assertEquals(test.get(8, 0), 13, DELTA);
		assertEquals(test.get(9, 0), 14, DELTA);
		assertEquals(test.get(10, 0), 15, DELTA);
		assertEquals(test.get(11, 0), 16, DELTA);
		
		Matrix testTwo = new SparseMatrix(1,5);
		testTwo.set(0, 0, 9);
		testTwo.set(0, 1, 0);
		testTwo.set(0, 2, 3);
		testTwo.set(0, 3, 1);
		testTwo.set(0, 4, 1);
		testTwo = MatrixHelper.findNonzero(testTwo);
		
		assertEquals(testTwo.get(0, 0), 1, DELTA);
		assertEquals(testTwo.get(0, 1), 3, DELTA);
		assertEquals(testTwo.get(0, 2), 4, DELTA);
		assertEquals(testTwo.get(0, 3), 5, DELTA);
		
	}
	
	@Test
	public void unionTest(){
		
		Matrix test = new SparseMatrix(1,4);
		test.set(0, 0, 9);
		test.set(0, 1, 0);
		test.set(0, 2, 3);
		test.set(0, 3, 1);
		
		Matrix testTwo = new SparseMatrix(1,4);
		testTwo.set(0, 0, 7);
		testTwo.set(0, 1, -1);
		testTwo.set(0, 2, 6);
		testTwo.set(0, 3, 3);
		
		Matrix unionMatrix = MatrixHelper.union(test, testTwo);
		
		assertEquals(unionMatrix.get(0, 0), -1, DELTA);
		assertEquals(unionMatrix.get(1, 0), 0, DELTA);
		assertEquals(unionMatrix.get(2, 0), 1, DELTA);
		assertEquals(unionMatrix.get(3, 0), 3, DELTA);
		assertEquals(unionMatrix.get(4, 0), 6, DELTA);
		assertEquals(unionMatrix.get(5, 0), 7, DELTA);
		assertEquals(unionMatrix.get(6, 0), 9, DELTA);
	}
	
	@Test
	public void getColumnsTest(){
		
		Matrix test = new SparseMatrix(2,4);
		test.set(0, 0, 9);
		test.set(0, 1, 0);
		test.set(0, 2, 3);
		test.set(0, 3, 1);
		test.set(1, 0, 3);
		test.set(1, 1, 7);
		test.set(1, 2, 4);
		test.set(1, 3, 8);
		
		Matrix testTwo = new SparseMatrix(2, 1);
		testTwo.set(0, 0, 1);
		testTwo.set(1, 0, 2);
		
		Matrix joinedMatrix = MatrixHelper.getColumns(test, testTwo);
		
		assertEquals(joinedMatrix.get(0, 0), 9, DELTA);
		assertEquals(joinedMatrix.get(0, 1), 0, DELTA);
		assertEquals(joinedMatrix.get(1, 0), 3, DELTA);
		assertEquals(joinedMatrix.get(1, 1), 7, DELTA);
	}
	
	@Test
	public void lengthTest(){
		
		Matrix test = new SparseMatrix(2,4);
		int lengthOne = MatrixHelper.length(test);
		
		Matrix testTwo = new SparseMatrix(7, 5);
		int lengthTwo = MatrixHelper.length(testTwo);
		
		assertEquals(lengthOne, 4);
		assertEquals(lengthTwo, 7);
	}
	
	@Test 
	public void squareRootTest(){
		Matrix test = new SparseMatrix(2,2);
		test.set(0, 0, 1);
		test.set(0, 1, 4);
		test.set(1, 0, 9);
		test.set(1, 1, 16);
		test = MatrixHelper.squareRoot(test);
		
		assertEquals(0, 0, 1);
		assertEquals(0, 1, 2);
		assertEquals(1, 0, 3);
		assertEquals(1, 1, 4);
	}
	
	@Test
	public void getIndicesTest(){
		Matrix test = new SparseMatrix(3,3);
		test.set(0, 0, 1);
		test.set(0, 1, 4);
		test.set(0, 2, 5);
		test.set(1, 0, 9);
		test.set(1, 1, 16);
		test.set(1, 2, 3);
		test.set(2, 0, 8);
		test.set(2, 1, 0);
		test.set(2, 2, 2);
		
		Matrix indexMatrix = new SparseMatrix(3,1);
		indexMatrix.set(0, 0, 2);
		indexMatrix.set(1, 0, 7);
		indexMatrix.set(2, 0, 5);
		
		Matrix tempMatrix = MatrixHelper.getIndices(test, indexMatrix);
		
		assertEquals(tempMatrix.get(0,0), 9, DELTA);
		assertEquals(tempMatrix.get(1,0), 5, DELTA);
		assertEquals(tempMatrix.get(2,0), 16, DELTA);
	}
	
	@Test 
	public void modifyColumnTest(){
		
		Matrix test = new SparseMatrix(3,3);
		test.set(0, 0, 1);
		test.set(0, 1, 4);
		test.set(0, 2, 5);
		test.set(1, 0, 9);
		test.set(1, 1, 16);
		test.set(1, 2, 3);
		test.set(2, 0, 8);
		test.set(2, 1, 0);
		test.set(2, 2, 2);
		
		Matrix indexMatrix = new SparseMatrix(3,1);
		indexMatrix.set(0, 0, 2);
		indexMatrix.set(1, 0, 7);
		indexMatrix.set(2, 0, 5);
		
		Matrix newMatrix = MatrixHelper.modifyColumn(test, 3, indexMatrix);
		assertEquals(newMatrix.get(0,0), 1, DELTA);
		assertEquals(newMatrix.get(1,0), 9, DELTA);
		assertEquals(newMatrix.get(2,0), 8, DELTA);
		assertEquals(newMatrix.get(0,1), 4, DELTA);
		assertEquals(newMatrix.get(1,1), 16, DELTA);
		assertEquals(newMatrix.get(2,1), 0, DELTA);
		assertEquals(newMatrix.get(0,2), 2, DELTA);
		assertEquals(newMatrix.get(1,2), 7, DELTA);
		assertEquals(newMatrix.get(2,2), 5, DELTA);
		
	}
	
	@Test
	public void getColumnTest(){
		
		Matrix test = new SparseMatrix(3,3);
		test.set(0, 0, 1);
		test.set(0, 1, 4);
		test.set(0, 2, 5);
		test.set(1, 0, 9);
		test.set(1, 1, 16);
		test.set(1, 2, 3);
		test.set(2, 0, 8);
		test.set(2, 1, 0);
		test.set(2, 2, 2);
		
		Matrix columnMatrix = MatrixHelper.getColumn(test, 2);
		assertEquals(columnMatrix.get(0,0), 5, DELTA);
		assertEquals(columnMatrix.get(1,0), 3, DELTA);
		assertEquals(columnMatrix.get(2,0), 2, DELTA);
		
		Matrix columnMatrixTwo = MatrixHelper.getColumn(test, 1);
		assertEquals(columnMatrixTwo.get(0,0), 4, DELTA);
		assertEquals(columnMatrixTwo.get(1,0), 16, DELTA);
		assertEquals(columnMatrixTwo.get(2,0), 0, DELTA);
	}
	
	@Test
	public void normTest(){
		
		Matrix test = new SparseMatrix(3,3);
		test.set(0, 0, 1);
		test.set(0, 1, 4);
		test.set(0, 2, 5);
		test.set(1, 0, 9);
		test.set(1, 1, 16);
		test.set(1, 2, 3);
		test.set(2, 0, 8);
		test.set(2, 1, 0);
		test.set(2, 2, 2);
		
		double sum = MatrixHelper.norm(test);
		assertEquals(sum, Math.sqrt(456), DELTA);
	}
	
	@Test
	public void removeColumnsTest(){
		Matrix test = new SparseMatrix(3,3);
		test.set(0, 0, 1);
		test.set(0, 1, 4);
		test.set(0, 2, 5);
		test.set(1, 0, 9);
		test.set(1, 1, 16);
		test.set(1, 2, 3);
		test.set(2, 0, 8);
		test.set(2, 1, 0);
		test.set(2, 2, 2);
	
		
		Matrix newMatrix = MatrixHelper.removeColumns(test, 3);
		assertEquals(newMatrix.get(0,0), 1, DELTA);
		assertEquals(newMatrix.get(0,1), 4, DELTA);
		assertEquals(newMatrix.get(1,0), 9, DELTA);
		assertEquals(newMatrix.get(1,1), 16, DELTA);
		assertEquals(newMatrix.get(2,0), 8, DELTA);
		assertEquals(newMatrix.get(2,1), 0, DELTA);
	}
	
	@Test
	public void getLastColumnTest(){
		Matrix test = new SparseMatrix(3,3);
		test.set(0, 0, 1);
		test.set(0, 1, 4);
		test.set(0, 2, 5);
		test.set(1, 0, 9);
		test.set(1, 1, 16);
		test.set(1, 2, 3);
		test.set(2, 0, 8);
		test.set(2, 1, 0);
		test.set(2, 2, 2);
	
		
		Matrix newMatrix = MatrixHelper.getLastColumn(test);
		assertEquals(newMatrix.get(0,0), 5, DELTA);
		assertEquals(newMatrix.get(1,0), 3, DELTA);
		assertEquals(newMatrix.get(2,0), 2, DELTA);
	}
}
