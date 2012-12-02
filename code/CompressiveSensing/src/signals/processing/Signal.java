package signals.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import matrix.MatrixHelper;
import matrix.SignalHelper;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

public class Signal {

	protected Matrix signalMatrix;
	protected int[] sparsityMatrix;
	private int SIGNAL_LENGTH = 1024;
	private int SIGNAL_SPARSITY = 40;
	private int NUM_MEASUREMENTS = 240;
	private int NUM_ITERATIONS = 10;
	private int MAX_ITERATIONS = 1000;

	public Signal(File matrixFile){

		int rowLength = MatrixHelper.getRowLength(matrixFile);
		signalMatrix = new SparseMatrix(getSignalLength(), rowLength);
		signalMatrix = MatrixHelper.fillWithZeros(signalMatrix);
		this.matrixFromFile(matrixFile);
	}

	public Matrix getMatrix(){
		return signalMatrix;
	}

	public int getSignalLength(){
		return SIGNAL_LENGTH;
	}

	public void setSignalLength(int length){
		SIGNAL_LENGTH = length;
	}

	public void setSignalSparsity(int sparsity){
		SIGNAL_SPARSITY = sparsity;
	}

	public int getSignalSparsity(){
		return SIGNAL_SPARSITY;
	}

	public int getNumMeasurements(){
		return NUM_MEASUREMENTS;
	}

	public int getNumIterations(){
		return NUM_ITERATIONS;
	}

	public int getMaxIterations(){
		return MAX_ITERATIONS;
	}

	public int getSparsityMatrix(int arrayNum){
		return sparsityMatrix[arrayNum];
	}
	
	public Matrix getMeasurements(){

		Matrix gaussDistMatrix = new SparseMatrix(getNumMeasurements(), getSignalLength());
		//form a matrix similar to the way Matlab uses randn(x) to create a matrix
		//based on a normal distribution
		gaussDistMatrix = MatrixHelper.randN(gaussDistMatrix);
		double x = (1 / Math.sqrt(getNumMeasurements()));

		return gaussDistMatrix.times(x);
	}

	public Matrix runCosamp(){

		Matrix signalMatrix = this.getMatrix();
		Matrix finalMatrix = new SparseMatrix(signalMatrix.rowSize(), signalMatrix.columnSize());

		this.setSignalLength(finalMatrix.rowSize());

		//reconstruct one column vector at a time
		for(int i = 0; i < signalMatrix.columnSize(); i++){

			//set the sparsity for this column in the matrix
			this.setSignalSparsity(this.getSparsityMatrix(i));

			//create column vector that is a random permutation of numbers 1 through signal_length
			Matrix randomMatrix = new SparseMatrix(this.getSignalLength(), 1);
			randomMatrix = MatrixHelper.randomPermutation(randomMatrix);

			Matrix slicedMatrix = MatrixHelper.getColumn(signalMatrix, i);

			//measurements
			Matrix phiMatrix = this.getMeasurements();
			Matrix measurementMatrix = phiMatrix.times(slicedMatrix);

			//reconstruct using the cosamp algorithm
			Matrix xHat = SignalHelper.cosampAlgo(this, measurementMatrix, phiMatrix, 
					this.getSignalSparsity(), this.getNumIterations());

			//set column in the final matrix to reflect reconstructed vector
			finalMatrix = MatrixHelper.fillColumn(finalMatrix, xHat ,i);
		}

		return finalMatrix;
	}

	public void matrixFromFile(File fileName){
		int row = 0;
		int count = 0;

		try {
			String currentLine;
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while ((currentLine = br.readLine()) != null) {
				String[] columnValues = currentLine.split("\t");

				//initialize sparsity matrix on the first turn
				if(count == 0){
					sparsityMatrix = new int[columnValues.length];
				}
				count++;

				//build the signal matrix from the values in the file
				if(columnValues.length == 1){
					signalMatrix.set(row, 0, Double.parseDouble(columnValues[0]));
					if(signalMatrix.get(row, 0) != 0){
						sparsityMatrix[0]++;
					}
				}	
				else{
					for(int i = 0; i < signalMatrix.columnSize(); i++){
						signalMatrix.set(row, i, Double.parseDouble(columnValues[i]));
						if(signalMatrix.get(row, i) != 0){
							sparsityMatrix[i]++;
						}
					}
				}
				row++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
