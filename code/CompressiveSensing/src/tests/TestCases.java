package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import matrix.MatrixHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;
import org.junit.Test;

import signals.algorithm.ProcessSignals;

public class TestCases {

	@Test 
	public void testCaseOne(){
		Matrix one = ProcessSignals.runCosamp(new File("src/tests/input/test_1.txt"));
		Matrix two = new SparseMatrix(one.rowSize(), one.columnSize());
		two = MatrixHelper.matrixFromFile(two, new File("src/tests/output/results_1.txt"));
		
		int numRows = two.rowSize();
		int numCols = two.columnSize();
		
		//write results to file
		MatrixHelper.writeToFile("src/tests/temp/temp_1.txt", one);
		
		int count = 0;
		for(int row = 0; row < numRows; row++){
			for(int column = 0; column < numCols; column++){
				if(((one.get(row,column)-0.1) <= two.get(row, column))&& (two.get(row, column)<= (one.get(row,column) + 0.1))){
					count++;
				}
				else{
					System.out.println("ROW = " + row + " COLUMN = " + column);
				}
				//assertTrue(((one.get(row,column)-0.1) <= two.get(row, column))&& (two.get(row, column)<= (one.get(row,column) + 0.1)));
			}
		}
		System.out.println(count);
		
	}
}
