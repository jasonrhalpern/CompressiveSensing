package signals.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import matrix.MatrixHelper;
import matrix.SignalHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

/**
 * This encapsulates all the important information for the signal.
 * It includes the matrix that represents the signal, an array that represents
 * the sparsity of each column in the matrix, the length of the signal and the
 * number of measurements to take for reconstructing each column vector in the signal.
 * 
 * @author Jason Halpern
 * @version 1.0 12/09/12
 */

public class Signal {

	protected Matrix signalMatrix;
	protected int[] sparsityMatrix; //holds the sparsity of each column vector
	private int SIGNAL_LENGTH = 1024; //number of rows in the signal
	private final int NUM_MEASUREMENTS = 240;

	/**
	 * Public constructor to create a Signal object from the signal
	 * represented by the matrix in the file.
	 */
	public Signal(File matrixFile){

		int numColumns = getNumColumns(matrixFile);
		int numRows = getNumRows(matrixFile);
		signalMatrix = new SparseMatrix(numRows, numColumns);
		signalMatrix = MatrixHelper.fillWithZeros(signalMatrix);
		matrixFromFile(matrixFile);
	}

	/**
	 * Return the matrix that represents the signal
	 * 
	 * @return the signal matrix
	 */
	public Matrix getSignalMatrix(){
		return signalMatrix;
	}

	/**
	 * @return the length of the signal
	 */
	public int getSignalLength(){
		return SIGNAL_LENGTH;
	}

	/**
	 * Set the length of the signal to the given value
	 * 
	 * @param length
	 */
	public void setSignalLength(int length){
		SIGNAL_LENGTH = length;
	}

	/**
	 * Return the number of measurements that should be taken to
	 * reconstruct a column vector of the signal.
	 * 
	 * @return the number of measurements to be taken
	 */
	public int getNumMeasurements(){
		return NUM_MEASUREMENTS;
	}

	/**
	 * Return the sparsity of the given column.
	 * 
	 * @param columnNum
	 * @return the sparsity of that column
	 */
	public int getSparsityMatrix(int columnNum){
		return sparsityMatrix[columnNum];
	}

	/**
	 * Create a matrix to eventually be used in taking the measurements of the signal that we 
	 * want to reconstruct.
	 * 
	 * @return a matrix to be used for measurements
	 */
	public Matrix getMeasurements(){

		Matrix gaussDistMatrix = new SparseMatrix(getNumMeasurements(), getSignalLength());
		//form a matrix similar to the way Matlab uses randn(x) to create a matrix
		//based on a normal distribution
		gaussDistMatrix = MatrixHelper.randN(gaussDistMatrix);
		double x = (1 / Math.sqrt(getNumMeasurements()));

		return gaussDistMatrix.times(x);
	}

	/**
	 * Run the cosamp algorithm on the signal object to reconstruct it.
	 * 
	 * @param numIterations
	 * @return the reconstructed matrix
	 */
	public Matrix runCosamp(int numIterations){

		Matrix signalMatrix = getSignalMatrix();
		Matrix finalMatrix = new SparseMatrix(signalMatrix.rowSize(), signalMatrix.columnSize());

		setSignalLength(finalMatrix.rowSize());

		//reconstruct one column vector at a time
		for(int i = 0; i < signalMatrix.columnSize(); i++){

			//create column vector that is a random permutation of numbers 1 through signal_length
			Matrix randomMatrix = new SparseMatrix(this.getSignalLength(), 1);
			randomMatrix = MatrixHelper.randomPermutation(randomMatrix);

			Matrix slicedMatrix = MatrixHelper.getColumn(signalMatrix, i);

			//measurements
			Matrix phiMatrix = getMeasurements();
			Matrix measurementMatrix = phiMatrix.times(slicedMatrix);

			//reconstruct using the cosamp algorithm
			Matrix xHat = SignalHelper.cosampAlgo(this, measurementMatrix, phiMatrix, 
					getSparsityMatrix(i), numIterations);

			//set column in the final matrix to reflect reconstructed vector
			finalMatrix = MatrixHelper.fillColumn(finalMatrix, xHat ,i);
		}

		return finalMatrix;
	}

	/**
	 * Create a matrix from the signal represented in the file.
	 * The sparsity is also determined as we read from the file.
	 * 
	 * @param fileName - file that contains the matrix.
	 */
	public void matrixFromFile(File fileName){
		int rowNumber = 0;

		try {
			String currentLine;
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while ((currentLine = br.readLine()) != null) {
				String[] columnValues = currentLine.split("\t");

				//initialize sparsity matrix on the first turn
				if(rowNumber == 0){
					sparsityMatrix = new int[columnValues.length];
				}

				//build the signal matrix from the values in the file
				if(columnValues.length == 1){
					signalMatrix.set(rowNumber, 0, Double.parseDouble(columnValues[0]));
					if(signalMatrix.get(rowNumber, 0) != 0){
						sparsityMatrix[0]++;
					}
				}	
				else{
					for(int i = 0; i < signalMatrix.columnSize(); i++){
						signalMatrix.set(rowNumber, i, Double.parseDouble(columnValues[i]));
						if(signalMatrix.get(rowNumber, i) != 0){
							sparsityMatrix[i]++;
						}
					}
				}
				rowNumber++;
			}
			setSignalLength(rowNumber); //set the signal length for this matrix
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finds the number of columns in a row,
	 * this is important to create matrices with the correct dimensions.
	 * 
	 * @param matrixFile - file that includes the matrix
	 * @return the number of columns in the matrix
	 */
	public static int getNumColumns(File matrixFile){
		int count = 0;

		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(matrixFile));
			while ((currentLine = br.readLine()) != null) {
				String[] columnValues = currentLine.split("\t");

				return columnValues.length;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return count;
	}
	
	/**
	 * Finds the number of columns in a row,
	 * this is important to create matrices with the correct dimensions.
	 * 
	 * @param matrixFile - file that includes the matrix
	 * @return the number of rows in the matrix
	 */
	public static int getNumRows(File matrixFile){
		int count = 0;

		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(matrixFile));
			while ((currentLine = br.readLine()) != null) {
				count++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return count;
	}

}
