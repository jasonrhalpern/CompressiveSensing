package matrix;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

public class SignalHelper {

	private static int SIGNAL_LENGTH = 1024;
	private static int SIGNAL_SPARSITY = 40;
	private static int NUM_MEASUREMENTS = 240;
	private static int NUM_ITERATIONS = 10;
	private static int MAX_ITERATIONS = 1000;

	public static int getSignalLength(){
		return SIGNAL_LENGTH;
	}

	public static int getSignalSparsity(){
		return SIGNAL_SPARSITY;
	}

	public static int getNumMeasurements(){
		return NUM_MEASUREMENTS;
	}

	public static int getNumIterations(){
		return NUM_ITERATIONS;
	}
	
	public static int getMaxIterations(){
		return MAX_ITERATIONS;
	}
	
	//implementation of cosamp algorithm
	public static void cosampAlgo(Matrix measurementMatrix, Matrix phiMatrix,
									int signalSparsity, int iterations){
		
		measurementMatrix = MatrixHelper.toSingleColumn(measurementMatrix);
		int numRows = phiMatrix.rowSize();
		int numColumns = phiMatrix.columnSize();
		
		Matrix xCosampMatrix = new SparseMatrix(numColumns, iterations);
		xCosampMatrix = MatrixHelper.fillWithZeros(xCosampMatrix);
		
		int count = 1;
		int verbose = 0;
		double tolerance = 0.001;
		Matrix sCosampMatrix = new SparseMatrix(numColumns, 1);
		sCosampMatrix = MatrixHelper.fillWithZeros(sCosampMatrix);
		
		Matrix intermediateMatrix = null;
		Matrix rCosampMatrix = null;
		Matrix proxyCosampMatrix = null;
		while(count <= iterations){
			//backprojection
			intermediateMatrix = phiMatrix.times(sCosampMatrix);
			rCosampMatrix = measurementMatrix.minus(intermediateMatrix);
			proxyCosampMatrix = phiMatrix.transpose().times(rCosampMatrix);
			proxyCosampMatrix = MatrixHelper.getAbsMatrix(proxyCosampMatrix);
			proxyCosampMatrix = MatrixHelper.sortDescending(proxyCosampMatrix);
			
		}
	}
}
