package signals.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import matrix.MatrixHelper;
import matrix.SignalHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

public class ProcessSignals {

	public static Matrix runCosamp(File matrixFile){

		int columnLength = MatrixHelper.getColumnLength(matrixFile);
		//generate signal to test algorithm
		Matrix signalMatrix = new SparseMatrix(SignalHelper.getSignalLength(), columnLength);
		signalMatrix = MatrixHelper.fillWithZeros(signalMatrix);
		signalMatrix = MatrixHelper.matrixFromFile(signalMatrix, matrixFile);
		Matrix finalMatrix = new SparseMatrix(signalMatrix.rowSize(), signalMatrix.columnSize());
		Matrix slicedMatrix = null;

		SignalHelper.setSignalLength(finalMatrix.rowSize());

		for(int i = 0; i < columnLength; i++){
			
			SignalHelper.setSignalSparsity(MatrixHelper.sparsityMatrix[i]);

			//create column vector that is a random permutation of numbers 1 through signal_length
			Matrix randomMatrix = new SparseMatrix(SignalHelper.getSignalLength(), 1);
			randomMatrix = MatrixHelper.randomPermutation(randomMatrix);

			Matrix modifiedRandomMatrix = new SparseMatrix(SignalHelper.getSignalSparsity(), 1);
			modifiedRandomMatrix = MatrixHelper.getIndices(randomMatrix, 1, SignalHelper.getSignalSparsity());

			//form a matrix similar to the way Matlab uses randn(x) to create a matrix
			//based on a normal distribution
			Matrix gaussDistMatrix = new SparseMatrix(SignalHelper.getSignalSparsity(), 1);
			gaussDistMatrix = MatrixHelper.randN(gaussDistMatrix);

			//signalMatrix = MatrixHelper.setCellValues(signalMatrix, modifiedRandomMatrix, gaussDistMatrix);

			slicedMatrix = MatrixHelper.getColumn(signalMatrix, i);

			long startTime = System.currentTimeMillis();

			//measurements
			Matrix gaussDistMatrixTwo = new SparseMatrix(SignalHelper.getNumMeasurements(), 
					SignalHelper.getSignalLength());
			gaussDistMatrixTwo = MatrixHelper.randN(gaussDistMatrixTwo);
			double x = (1 / Math.sqrt(SignalHelper.getNumMeasurements()));
			Matrix phiMatrix = gaussDistMatrixTwo.times(x);

			Matrix measurementMatrix = phiMatrix.times(slicedMatrix);

			List<Matrix> finalMatrices = new ArrayList<Matrix>();
			//reconstruct
			finalMatrices = SignalHelper.cosampAlgo(measurementMatrix, phiMatrix, 
					SignalHelper.getSignalSparsity(), 
					SignalHelper.getNumIterations());
			Matrix xHat = finalMatrices.get(0);
			Matrix trash = finalMatrices.get(1);

			long endTime = System.currentTimeMillis();

			long duration = endTime - startTime;

			System.out.println("RUNNING TIME OF ALGORITHM IS " + duration + " milliseconds");

			finalMatrix = MatrixHelper.fillColumn(finalMatrix, xHat ,i);
		}

		MatrixHelper.printMatrix(finalMatrix);
		//MatrixHelper.printMatrix(trash);

		for(int i = 0; i < MatrixHelper.sparsityMatrix.length; i++){
			System.out.println(MatrixHelper.sparsityMatrix[i]);
		}
		
		return finalMatrix;
	}

	public static void main(String[] args){
		runCosamp(new File("xMatrix.txt"));
	}
}
