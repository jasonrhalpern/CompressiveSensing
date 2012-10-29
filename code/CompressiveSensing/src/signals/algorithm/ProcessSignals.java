package signals.algorithm;

import java.util.ArrayList;
import java.util.List;

import matrix.MatrixHelper;
import matrix.SignalHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

import signals.types.Image;

public class ProcessSignals {

	private static int TOTAL_ROWS = 4;
	private static int TOTAL_COLUMNS = 4;

	public static void main(String[] args){
		/*
		//form a testing image
		Matrix testMatrix = MatrixHelper.getDummyMatrix(TOTAL_ROWS, TOTAL_COLUMNS);
		Image img = new Image(testMatrix);

		//test matrix multiplication by multiplying by itself
		img.multiplyMatrix(testMatrix);

		//test matrix multiplication by multiplying by identity
		Matrix identityMatrix = MatrixHelper.getIdentityMatrix(TOTAL_ROWS, TOTAL_COLUMNS);
		img.multiplyMatrix(identityMatrix);
	*/
		//generate signal to test algorithm
		Matrix signalMatrix = new SparseMatrix(SignalHelper.getSignalLength(), 1);
		signalMatrix = MatrixHelper.fillWithZeros(signalMatrix);
		
		//create row vector that is a random permutation of numbers 1 through signal_length
		Matrix randomMatrix = new SparseMatrix(1, SignalHelper.getSignalLength());
		randomMatrix = MatrixHelper.randomPermutation(randomMatrix);
		
		//form a matrix similar to the way Matlab uses randn(x) to create a matrix
		//based on a normal distribution
		Matrix gaussDistMatrix = new SparseMatrix(SignalHelper.getSignalSparsity(), 1);
		gaussDistMatrix = MatrixHelper.randN(gaussDistMatrix);

		signalMatrix = MatrixHelper.setCellValues(signalMatrix, randomMatrix, gaussDistMatrix);
		
		//measurements
		Matrix gaussDistMatrixTwo = new SparseMatrix(SignalHelper.getNumMeasurements(), 
													SignalHelper.getSignalLength());
		gaussDistMatrixTwo = MatrixHelper.randN(gaussDistMatrixTwo);
		double x = (1 / Math.sqrt(SignalHelper.getNumMeasurements()));
		Matrix phiMatrix = gaussDistMatrixTwo.times(x);
		
		Matrix measurementMatrix = phiMatrix.times(signalMatrix);
		
		List<Matrix> finalMatrices = new ArrayList<Matrix>();
		//reconstruct
		finalMatrices = SignalHelper.cosampAlgo(measurementMatrix, phiMatrix, 
								SignalHelper.getSignalSparsity(), 
								SignalHelper.getNumIterations());
		Matrix xHat = finalMatrices.get(0);
		Matrix trash = finalMatrices.get(1);
		
	}
}
