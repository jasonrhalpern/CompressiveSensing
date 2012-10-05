package signals.algorithm;

import matrix.MatrixHelper;
import matrix.SignalHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

import signals.types.Image;

public class ProcessSignals {
	
	private static int TOTAL_ROWS = 4;
	private static int TOTAL_COLUMNS = 4;

	public static void main(String[] args){

		//form a testing image
		Matrix testMatrix = MatrixHelper.getDummyMatrix(TOTAL_ROWS, TOTAL_COLUMNS);
		Image img = new Image(testMatrix);

		//print the matrix representing the image
		img.printMatrix();
		System.out.println();

		//test matrix multiplication by multiplying by itself
		img.multiplyMatrix(testMatrix);
		img.printMatrix();
		System.out.println();

		//test matrix multiplication by multiplying by identity
		Matrix identityMatrix = MatrixHelper.getIdentityMatrix(TOTAL_ROWS, TOTAL_COLUMNS);
		img.multiplyMatrix(identityMatrix);
		img.printMatrix();
		
		//generate signal to test algorithm
		Matrix zeroMatrix = new SparseMatrix(SignalHelper.getSignalLength(), 1);
		zeroMatrix = MatrixHelper.fillWithZeros(zeroMatrix);
		//create row vector that is a random permutation of numbers 1 through signal_length
		Matrix randomMatrix = new SparseMatrix(1, SignalHelper.getSignalLength());
		randomMatrix = MatrixHelper.randomPermutation(randomMatrix);
		MatrixHelper.printMatrix(randomMatrix);
		
		Matrix gaussDistMatrix = new SparseMatrix(SignalHelper.getSignalSparsity(), 1);
		gaussDistMatrix = MatrixHelper.randN(gaussDistMatrix);
		MatrixHelper.printMatrix(gaussDistMatrix);

	}
}
