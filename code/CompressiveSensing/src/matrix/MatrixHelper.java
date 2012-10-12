package matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

public class MatrixHelper {

	//create an identity matrix based on the given dimensions
	public static Matrix getIdentityMatrix(int numRows, int numColumns){

		//can only create identity on matrix with equal dimensions
		if(numRows != numColumns){
			return null;
		}

		Matrix identityMatrix = new SparseMatrix(numRows, numColumns);
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				//fill only the diagonals with 1s, all other cells with 0s
				if(row == column){
					identityMatrix.set(row, column, 1); 
				}else{
					identityMatrix.set(row, column, 0); 
				}
			}
		}

		return identityMatrix;

	}

	//get a dummy matrix for testing purposes
	public static Matrix getDummyMatrix(int numRows, int numColumns){

		Matrix dummyMatrix = new SparseMatrix(numRows, numColumns);

		dummyMatrix = MatrixHelper.fillWithZeros(dummyMatrix);

		dummyMatrix.set(1, 0, 3);
		dummyMatrix.set(1, 1, 2);
		dummyMatrix.set(1, 2, 4);
		dummyMatrix.set(1, 3, 5);

		return dummyMatrix;
	}

	//fill the entire matrix with zeros.
	//does the same thing as MATLAB's zeros(m, n) function.
	public static Matrix fillWithZeros(Matrix zeroMatrix){

		for (int row = 0; row < zeroMatrix.rowSize(); row++) {
			for (int column = 0; column < zeroMatrix.columnSize(); column++) {
				zeroMatrix.set(row, column, 0); //set cell's value to zero
			}
		}

		return zeroMatrix;
	}

	//does the same thing as MATLAB's randPerm(n) function.
	//returns a row vector containing a random permutation 
	//of the integers from 1 to the length of the signal inclusive.
	public static Matrix randomPermutation(Matrix randomMatrix){

		//there has to only be one row vector
		if(randomMatrix.rowSize() > 1)
			return null;

		//use an array list to build up list of numbers then shuffle it to get a 
		//random permutation of the list
		List<Integer> permutationList = new ArrayList<Integer>();
		for(int index = 0; index < randomMatrix.columnSize(); index++){
			permutationList.add(index + 1);
		}
		Collections.shuffle(permutationList); //shuffle

		//fill each cell in vector with a number, which will be random
		//since the list has already been shuffled.
		for(int index = 0; index < randomMatrix.columnSize(); index++){
			randomMatrix.set(0, index, permutationList.get(index));
		}

		return randomMatrix;

	}

	//very similar to Matlab's randn(m, n) function
	public static Matrix randN(Matrix randomMatrix){

		double randNum = 0;
		Random rand = new Random();

		for (int row = 0; row < randomMatrix.rowSize(); row++) {
			for (int column = 0; column < randomMatrix.columnSize(); column++) {
				//get a Gaussian random number with mean 0 and standard deviation 1
				randNum = rand.nextGaussian();
				randomMatrix.set(row, column, randNum);
			}
		}

		return randomMatrix;
	}

	//this function handles the following line from Matlab:  x(v(1:K)) = randn(K,1);
	//x is the zeroMatrix, v is our randomMatrix, the right side of the
	//equation is the gaussMatrix
	public static Matrix setCellValues(Matrix zeroMatrix, Matrix randomMatrix, 
										Matrix gaussMatrix){
		
		int cellNumber = 0;
		double cellValue = 0;
		for(int index = 0; index < SignalHelper.getSignalSparsity(); index++){
			cellNumber = (int) randomMatrix.get(0, index);
			cellValue = gaussMatrix.get(index, 0);
			zeroMatrix.set(cellNumber, 0, cellValue);
		}
		
		return zeroMatrix;
	}

	//print out the entire matrix
	public static void printMatrix(Matrix signalMatrix){

		//get the dimensions of the matrix
		int numRows = signalMatrix.rowSize();
		int numColumns = signalMatrix.columnSize();

		//loop through each cell in the matrix and print its value
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				System.out.print(signalMatrix.get(row,column) + " "); // bounds check
				//sum += matrix.getQuick(row,column); // no bounds check
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//converts the entire matrix to a single column.
	//similar to the x = x(:) operation in Matlab.
	public static Matrix toSingleColumn(Matrix mtrx){
		
		//create single column matrix with correct number of rows
		int numRows = mtrx.columnSize() * mtrx.rowSize();
		Matrix singleColumnMatrix = new SparseMatrix(numRows, 1);
		
		int rowCount = 0;
		double cellValue = 0;
		for (int column = 0; column < mtrx.columnSize(); column++) {
			for (int row = 0; row < mtrx.rowSize(); row++) {
				cellValue = mtrx.get(row, column);
				singleColumnMatrix.set(rowCount, 0, cellValue);
				rowCount++;
			}
		}
		
		return singleColumnMatrix;
		
	}
	
	//converts each element in the matrix to its absolute value
	//similar to Matlab's abs(x) function
	public static Matrix getAbsMatrix(Matrix mtrx){
		
		double cellValue = 0;
		for (int row = 0; row < mtrx.rowSize(); row++) {
			for (int column = 0; column < mtrx.columnSize(); column++) {
				//get a Gaussian random number with mean 0 and standard deviation 1
				cellValue = mtrx.get(row, column);
				cellValue = Math.abs(cellValue);
				mtrx.set(row, column, cellValue);
			}
		}
		
		return mtrx;
	}
	
	//same at Matlab's sort(matrix, 'descending')
	//sorts the elements in the specified direction, depending on the value of mode.
	public static List<Matrix> sortDescending(Matrix mtrx){
		
		Matrix indexMatrix = new SparseMatrix(mtrx.rowSize(), mtrx.columnSize());
		ArrayList<Double> sortedColumn = new ArrayList<Double>();
		ArrayList<Double> cloneList = new ArrayList<Double>();
		
		for (int column = 0; column < mtrx.columnSize(); column++) {
			//get all the values in this column
			for (int row = 0; row < mtrx.rowSize(); row++) {
				sortedColumn.add(mtrx.get(row, column));
				cloneList.add(mtrx.get(row, column));
			}
			//sort them in descending order
			Collections.sort(sortedColumn, new Comparator<Double>() {
			    public int compare(Double o1, Double o2) {
			        return o1.compareTo(o2);
			    }
			});
			//place them back in the column of the matrix.
			//we also need a matrix to track the row that the element was in 
			//before it was sorted.
			for(int i = 0; i < sortedColumn.size(); i++){
				mtrx.set(i, column, sortedColumn.get(i));
				indexMatrix.set(i, column, (cloneList.indexOf(sortedColumn.get(i))+1));
				cloneList.set(cloneList.indexOf(sortedColumn.get(i)), null);
			}
			//clear array list before moving on to next column
			sortedColumn.clear();
			cloneList.clear();
		}
		
		List<Matrix> matrixList = new ArrayList<Matrix>();
		matrixList.add(mtrx);
		matrixList.add(indexMatrix);
		
		return matrixList;
	}
	
	//check each cell in the matrix to see if it equals the test value.
	//1 if not equal, 0 if equal.
	public static Matrix notEqual(Matrix mtrx, int testValue){
		
		Matrix equalityMtrx = new SparseMatrix(mtrx.rowSize(), mtrx.columnSize());
		int cellValue = 0;
		for (int row = 0; row < equalityMtrx.rowSize(); row++) {
			for (int column = 0; column < equalityMtrx.columnSize(); column++) {
				cellValue = (int) mtrx.get(row, column);
				if(cellValue == testValue){
					equalityMtrx.set(row, column, 0);
				}
				else{
					equalityMtrx.set(row, column, 1);
				}
			}
		}
		return equalityMtrx;
	}
	
	public static Matrix getIndices(Matrix mtrx, int startIndex, int endIndex){
		
		int count = 1;
		int index = 0;
		int totalIndices = endIndex - startIndex + 1;
		int totalDimensions = mtrx.rowSize() * mtrx.columnSize();
		
		if(endIndex > totalDimensions)
			return null;
		
		Matrix indexMatrix = new SparseMatrix(1, totalIndices);
		
		for (int column = 0; column < mtrx.columnSize(); column++) {
			for (int row = 0; row < mtrx.rowSize(); row++) {
				if((startIndex <= count) && (count <= endIndex)){
					indexMatrix.set(0, index, mtrx.get(row, column));
					index++;
				}
				
				count++;
			}
		}
		
		return indexMatrix;
	}
}
