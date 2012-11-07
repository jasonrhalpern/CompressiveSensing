package signals.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import matrix.MatrixHelper;
import matrix.SignalHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

public class ProcessSignals {

	public static void main(String[] args){

		//generate signal to test algorithm
		Matrix signalMatrix = new SparseMatrix(SignalHelper.getSignalLength(), 1);
		signalMatrix = MatrixHelper.fillWithZeros(signalMatrix);
		
		//create row vector that is a random permutation of numbers 1 through signal_length
		Matrix randomMatrix = new SparseMatrix(SignalHelper.getSignalLength(), 1);
		randomMatrix = MatrixHelper.randomPermutation(randomMatrix);
		
		Matrix modifiedRandomMatrix = new SparseMatrix(SignalHelper.getSignalSparsity(), 1);
		modifiedRandomMatrix = MatrixHelper.getIndices(randomMatrix, 1, 40);
		
		//form a matrix similar to the way Matlab uses randn(x) to create a matrix
		//based on a normal distribution
		Matrix gaussDistMatrix = new SparseMatrix(SignalHelper.getSignalSparsity(), 1);
		gaussDistMatrix = MatrixHelper.randN(gaussDistMatrix);

		signalMatrix = MatrixHelper.setCellValues(signalMatrix, modifiedRandomMatrix, gaussDistMatrix);
		
		signalMatrix = MatrixHelper.matrixFromFile(signalMatrix, new File("xmatrix.txt"));
		
		//measurements
		Matrix gaussDistMatrixTwo = new SparseMatrix(SignalHelper.getNumMeasurements(), 
													SignalHelper.getSignalLength());
		gaussDistMatrixTwo = MatrixHelper.randN(gaussDistMatrixTwo);
		double x = (1 / Math.sqrt(SignalHelper.getNumMeasurements()));
		Matrix phiMatrix = gaussDistMatrixTwo.times(x);
		phiMatrix = MatrixHelper.matrixFromFile(phiMatrix, new File("phimatrix.txt"));
		
		Matrix measurementMatrix = phiMatrix.times(signalMatrix);
		
		List<Matrix> finalMatrices = new ArrayList<Matrix>();
		//reconstruct
		finalMatrices = SignalHelper.cosampAlgo(measurementMatrix, phiMatrix, 
								SignalHelper.getSignalSparsity(), 
								SignalHelper.getNumIterations());
		Matrix xHat = finalMatrices.get(0);
		Matrix trash = finalMatrices.get(1);
		//MatrixHelper.printMatrix(xHat);
		//MatrixHelper.printMatrix(trash);
		
	}
}
