package signals.algorithm;

import java.io.File;

import matrix.MatrixHelper;

import org.apache.mahout.math.Matrix;

import signals.processing.Signal;

/**
 * Runs the cosamp algorithm on the matrix represented in the file.
 * Includes the main function for this program
 * 
 * @author Jason Halpern
 * @version 1.0 12/09/12
 *
 */
public class ProcessSignals {
	
	private static final int NUM_ITERATIONS = 10;
	private static final int MAX_ITERATIONS = 1000;
	
	/**
	 * Returns the number of iterations that the cosamp algorithm 
	 * should make
	 * 
	 * @return the number of iterations
	 */
	public static int getNumIterations(){
		return NUM_ITERATIONS;
	}

	/**
	 * Returns the maximum number of iterations for the cosamp algo
	 * 
	 * @return the maximum number of iterations
	 */
	public static int getMaxIterations(){
		return MAX_ITERATIONS;
	}

	public static void main(String[] args){
		
		//run the cosamp algorithm on the signal represented in the file
		Signal sparseSignal = new Signal(new File("nMatrix.txt"));
		long startTime = System.currentTimeMillis();
		Matrix reconstructedMatrix = sparseSignal.runCosamp(NUM_ITERATIONS);
		MatrixHelper.printMatrix(reconstructedMatrix);
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println(duration);
	}
}
