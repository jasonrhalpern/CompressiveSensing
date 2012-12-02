package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.TestCase;

import matrix.MatrixHelper;

import org.apache.mahout.math.Matrix;
import org.junit.Test;

import signals.algorithm.ProcessSignals;
import signals.processing.Signal;

//Test cases include various matrices and column vectors to make sure that 
//the algorithm is functioning properly.
public class TestCases extends TestCase{

	@Test 
	public void testCaseOne(){

		//grab all the test input files
		File folder = new File("src/tests/input");
		File[] listOfFiles = folder.listFiles();
		//run the matrix or vector from each file through the algorithm and 
		//compare with the results to make sure it is correct
		for(File f : listOfFiles){
			Signal sparse = new Signal(f);
			Matrix one = sparse.runCosamp();
			String[] s = f.toString().split("_");
			char number = s[1].charAt(0);
			System.out.println(number);
			String resultsFile = "src/tests/output/results_" + number + ".txt";
			String tempFile = "src/tests/temp/temp_" + number + ".txt";

			Signal sparseTwo = new Signal(new File(resultsFile));
			Matrix two = sparseTwo.getMatrix();

			int numRows = two.rowSize();
			int numCols = two.columnSize();

			//write results to file
			MatrixHelper.writeToFile(tempFile, one);

			for(int row = 0; row < numRows; row++){
				for(int column = 0; column < numCols; column++){
					assertTrue(((one.get(row,column)-0.1) <= two.get(row, column)) && 
							(two.get(row, column)<= (one.get(row,column) + 0.1)));
				}
			}
		}	
	}
}
