package matrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

/**
 * Includes all the functions that were written to do matrix manipulation, 
 * many of which are written to simulate Matlab functions (i.e. randN(), randPerm()).
 * I also wrote additional matrix manipulation and helper functions as well to fully
 * implement the cosamp algorithm.
 * 
 * @author Jason Halpern
 * @version 1.0 12/09/12
 */

public class MatrixHelper {

	/**
	 * Create an identity matrix based on the given dimensions.
	 * 
	 * @param numRows - number of rows the identity matrix should have
	 * @param numColumns - number of columns the identity matrix should have
	 * @return an identity matrix with the specific number of rows and columns
	 */
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

	/**
	 * Create a dummy matrix for testing purposes
	 * 
	 * @param numRows - number of rows the dummy matrix should have
	 * @param numColumns - number of columns the dummy matrix should have
	 * @return the dummy matrix
	 */
	public static Matrix getDummyMatrix(int numRows, int numColumns){

		Matrix dummyMatrix = new SparseMatrix(numRows, numColumns);

		dummyMatrix = MatrixHelper.fillWithZeros(dummyMatrix);

		dummyMatrix.set(1, 0, 3);
		dummyMatrix.set(1, 1, 2);
		dummyMatrix.set(1, 2, 4);
		dummyMatrix.set(1, 3, 5);

		return dummyMatrix;
	}

	/**
	 * Fill an entire matrix with zeros.
	 * Does the same thing as MATLAB's zeros(m, n) function.
	 * 
	 * @param zeroMatrix - the matrix that should be filled with zeros
	 * @return the matrix with all zero values
	 */
	public static Matrix fillWithZeros(Matrix zeroMatrix){

		for (int row = 0; row < zeroMatrix.rowSize(); row++) {
			for (int column = 0; column < zeroMatrix.columnSize(); column++) {
				zeroMatrix.set(row, column, 0); //set cell's value to zero
			}
		}

		return zeroMatrix;
	}

	/**
	 * Does the same thing as Matlab's randPerm(n) function.
	 * Returns a column vector containing a random permutation 
	 * of the integers from 1 to the length of the signal inclusive.
	 * 
	 * @param randomMatrix - contains all values between 1 and the length of the signal
	 * in a random order
	 * @return the column vector containing the random permutation of the numbers in the
	 * randomMatrix parameter
	 */
	public static Matrix randomPermutation(Matrix randomMatrix){

		//there has to only be one column vector
		if(randomMatrix.columnSize() > 1)
			return null;

		//use an array list to build up list of numbers then shuffle it to get a 
		//random permutation of the list
		List<Integer> permutationList = new ArrayList<Integer>();
		for(int index = 0; index < randomMatrix.rowSize(); index++){
			permutationList.add(index + 1);
		}
		Collections.shuffle(permutationList); //shuffle

		//fill each cell in vector with a number, which will be random
		//since the list has already been shuffled.
		for(int index = 0; index < randomMatrix.rowSize(); index++){
			randomMatrix.set(index, 0, permutationList.get(index));
		}

		return randomMatrix;

	}

	/**
	 * Very similar to Matlab's randn(m, n) function
	 * 
	 * @param randomMatrix - this matrix with be filled with Gaussian random numbers
	 * @return a matrix containing pseudorandom values drawn from the 
	 * standard normal distribution. 
	 */
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

	/**
	 * This function handles the following line from Matlab:  x(v(1:K)) = randn(K,1).
	 * x is the zeroMatrix, v is our randomMatrix, the right side of the equation is the gaussMatrix.
	 * 
	 * @param zeroMatrix
	 * @param randomMatrix
	 * @param gaussMatrix
	 * @return a matrix in which the cell numbers (determined by values in the random matrix) of the 
	 * zero matrix are set to the values in the gaussMatrix.
	 */
	public static Matrix setCellValues(Matrix zeroMatrix, Matrix randomMatrix, 
			Matrix gaussMatrix){

		int cellNumber = 0;
		double cellValue = 0;
		for(int index = 0; index < randomMatrix.rowSize(); index++){
			cellNumber = (int) randomMatrix.get(index, 0) - 1;
			cellValue = gaussMatrix.get(index, 0);
			zeroMatrix.set(cellNumber, 0, cellValue);
		}

		return zeroMatrix;
	}

	/**
	 * Create a copy of a matrix.
	 * 
	 * @param mtrx - the matrix to be copied
	 * @return the new matrix that is identical to the parameter
	 */
	public static Matrix copyMatrix(Matrix mtrx){

		Matrix tempMatrix = new SparseMatrix(mtrx.rowSize(), mtrx.columnSize());

		for (int row = 0; row < mtrx.rowSize(); row++) {
			for (int column = 0; column < mtrx.columnSize(); column++) {
				tempMatrix.set(row, column, mtrx.get(row, column));
			}
		}

		return tempMatrix;
	}

	/**
	 * Similar to Matlab's norm() function.
	 * It squares each value in the matrix, adds those values together, then
	 * takes the square root of that sum.
	 * 
	 * @param mtrx
	 * @return a value determined by squaring each value in the parameter matrix, adding 
	 * those values together, then taking the square root of that sum.
	 */
	public static double norm(Matrix mtrx){

		double sum = 0;
		for(int i = 0; i < mtrx.rowSize(); i++){
			for(int j = 0; j < mtrx.columnSize(); j++){
				sum += Math.pow(mtrx.get(i, j), 2);
			}
		}
		return Math.sqrt(sum);
	}

	/**
	 * Takes the values from the first column in currentMatrix and sets the values
	 * in the given column of the finalMatrix to those values.
	 * 
	 * @param finalMatrix - a column in this matrix will be filled with values from currentMatrix
	 * @param currentMatrix - values from the first column of this matrix are retrieved and then set in 
	 * the given column of finalMatrix.
	 * @param colNum - the column in finalMatrix that should be filled
	 * @return
	 */
	public static Matrix fillColumn(Matrix finalMatrix, Matrix currentMatrix, int colNum){

		for(int row = 0; row < currentMatrix.rowSize(); row++){
			finalMatrix.set(row, colNum, currentMatrix.get(row,  0));
		}

		return finalMatrix;
	}

	/**
	 * Get the specific column from the given matrix.
	 * 
	 * @param mtrx - retrieve the column from this matrix
	 * @param columnNum - the column number to retrieve
	 * @return the column vector
	 */
	public static Matrix getColumn(Matrix mtrx, int columnNum){

		Matrix tempMatrix = new SparseMatrix(mtrx.rowSize(), 1);
		double cellNumber;
		int column = columnNum;
		for(int row = 0; row < mtrx.rowSize(); row++){
			cellNumber = mtrx.get(row, column);
			tempMatrix.set(row, 0, cellNumber);
		}
		return tempMatrix;
	}

	/**
	 * Modify the given column of the originalMatrix to match the values in mtrx, which is a column vector
	 * 
	 * @param originalMtrx - a matrix that will be manipulated
	 * @param columnNumber - the specific column that will be modified
	 * @param mtrx - a column vector that includes values that will be set in originalMatrix
	 * @return a modified matrix
	 */
	public static Matrix modifyColumn(Matrix originalMtrx, int columnNumber, Matrix mtrx){

		int colNumber = columnNumber;
		double cellValue;

		Matrix tempMatrix = originalMtrx;
		for(int row = 0; row < mtrx.rowSize(); row++){
			cellValue = mtrx.get(row, 0);
			tempMatrix.set(row, colNumber, cellValue);
		}

		return tempMatrix;
	}

	/**
	 * Print out the entire matrix.
	 * 
	 * @param signalMatrix
	 */
	public static void printMatrix(Matrix signalMatrix){

		//get the dimensions of the matrix
		int numRows = signalMatrix.rowSize();
		int numColumns = signalMatrix.columnSize();

		//loop through each cell in the matrix and print its value
		for (int row = 0; row < numRows; row++) {
			System.out.print("ROW " + (row+1) + " = ");
			for (int column = 0; column < numColumns; column++) {
				System.out.print(signalMatrix.get(row,column) + " "); // bounds check
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * Write a given matrix to a file.
	 * 
	 * @param fileName - name of file that the matrix will be written to
	 * @param signalMatrix - the matrix that will be added to the file
	 */
	public static void writeToFile(String fileName, Matrix signalMatrix){

		try {

			File file = new File(fileName);

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			//get the dimensions of the matrix
			int numRows = signalMatrix.rowSize();
			int numColumns = signalMatrix.columnSize();

			//loop through each cell in the matrix and write its value
			for (int row = 0; row < numRows; row++) {
				for (int column = 0; column < numColumns; column++) {
					bw.write(signalMatrix.get(row,column) + " "); 
				}
				bw.newLine();
			}
			bw.newLine();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the entire matrix to a single column. 
	 * This basically works by stacking each subsequent column under the previous one
	 * until there is only a single column.
	 * Similar to the x = x(:) operation in Matlab.
	 * 
	 * @param mtrx - the matrix that will be transformed into a single column vector
	 * @return
	 */
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

	/**
	 * Converts each element in the matrix to its absolute value.
	 * Similar to Matlab's abs(x) function.
	 * 
	 * @param mtrx - will get the absolute values of this matrix
	 * @return a matrix with the absolute values of all the numbers in the parameter
	 */
	public static Matrix getAbsMatrix(Matrix mtrx){

		double cellValue = 0;
		for (int row = 0; row < mtrx.rowSize(); row++) {
			for (int column = 0; column < mtrx.columnSize(); column++) {
				cellValue = mtrx.get(row, column);
				cellValue = Math.abs(cellValue);
				mtrx.set(row, column, cellValue);
			}
		}

		return mtrx;
	}

	/**
	 * Same at Matlab's sort(matrix, 'descending').
	 * Sorts the elements in the specified direction, depending on the value of mode (i.e descending).
	 * 
	 * @param mtrx - the matrix to be sorted
	 * @return a list with two matrices. The first matrix is the matrix with the sorted values.
	 * The second matrix includes the rows that the values were originally in before they were sorted.
	 */
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
					return o2.compareTo(o1);
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

	/**
	 * Check each cell in the matrix to see if it equals the test value.
	 * 1 if not equal, 0 if equal.
	 * 
	 * @param mtrx - matrix with the values we are comparing to the test value
	 * @param testValue - the value that we are comparing to.
	 * @return a matrix that included the result from whether or not the values in the 
	 * mtrx parameter equal the testValue.
	 */
	public static Matrix notEqual(Matrix mtrx, double testValue){

		Matrix equalityMtrx = new SparseMatrix(mtrx.rowSize(), mtrx.columnSize());
		double cellValue = 0;
		for (int row = 0; row < equalityMtrx.rowSize(); row++) {
			for (int column = 0; column < equalityMtrx.columnSize(); column++) {
				cellValue = mtrx.get(row, column);
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

	/**
	 * Grab all values in the given indices from the matrix.
	 * 
	 * @param mtrx - matrix that we will be obtaining the values from.
	 * @param startIndex - the start index
	 * @param endIndex - the end index
	 * @return a matrix that includes all values between the start and end index of the mtrx.
	 */
	public static Matrix getIndices(Matrix mtrx, int startIndex, int endIndex){

		int count = 1;
		int index = 0;
		int totalIndices = endIndex - startIndex + 1;
		int totalDimensions = mtrx.rowSize() * mtrx.columnSize();

		if(endIndex > totalDimensions)
			return null;

		Matrix indexMatrix = new SparseMatrix(totalIndices, 1);

		for (int column = 0; column < mtrx.columnSize(); column++) {
			for (int row = 0; row < mtrx.rowSize(); row++) {
				if((startIndex <= count) && (count <= endIndex)){
					indexMatrix.set(index, 0, mtrx.get(row, column));
					index++;
				}

				count++;
			}
		}

		return indexMatrix;
	}

	/**
	 * Loop through the index matrix to get a specific index, then grab
	 * the value at that index from the matrix.
	 * 
	 * @param mtrx - matrix that we are obtaining values from.
	 * @param indexMatrix - all the indices that we need to obtain from mtrx.
	 * @return a new matrix that includes all the values at the indices mentioned.
	 */
	public static Matrix getIndices(Matrix mtrx, Matrix indexMatrix){

		int index = 0;

		Matrix tempMatrix = new SparseMatrix(indexMatrix.rowSize(), 1);
		Matrix columnMatrix = MatrixHelper.toSingleColumn(mtrx);
		int cellValue;
		double newValue;

		for (int cell = 0; cell < indexMatrix.rowSize(); cell++) {
			cellValue = (int)indexMatrix.get(cell, 0) - 1;
			newValue = columnMatrix.get(cellValue, 0);
			tempMatrix.set(index, 0, newValue);
			index++;
		}

		return tempMatrix;
	}

	/**
	 * Similar to Matlab's find(x) function.
	 * Locates all nonzero elements of array X, 
	 * and returns the linear indices of those elements. 
	 * If X is a row vector, then the returned tempMtrx is a row vector; otherwise, it is a column vector.
	 * 
	 * @param equalityMtrx
	 * @return the matrix that indicates the nonzero elements of the equalityMatrix.
	 */ 
	public static Matrix findNonzero(Matrix equalityMtrx){

		List<Integer> nonZeroCells = new ArrayList<Integer>();

		boolean rowVector = false;
		if(equalityMtrx.rowSize() == 1){
			rowVector = true;
		}

		int count = 1;

		for (int column = 0; column < equalityMtrx.columnSize(); column++) {
			for (int row = 0; row < equalityMtrx.rowSize(); row++) {
				if(equalityMtrx.get(row, column) != 0){
					nonZeroCells.add(count);
				}
				count++;
			}
		}

		Matrix tempMtrx = null;
		count -= 1;
		if(rowVector && (nonZeroCells.size() != 0)){
			tempMtrx = new SparseMatrix(1, nonZeroCells.size());
			for(int i = 0; i < nonZeroCells.size(); i++){
				tempMtrx.set(0, i, nonZeroCells.get(i));
			}
		}
		else if (nonZeroCells.size() != 0){
			tempMtrx = new SparseMatrix(nonZeroCells.size(), 1);
			for(int i = 0; i < nonZeroCells.size(); i++){
				tempMtrx.set(i, 0, nonZeroCells.get(i));
			}
		}
		else{
			tempMtrx = new SparseMatrix(0, 0);
		}
		return tempMtrx;
	}

	/**
	 * Union of two matrices. Doesn't include dupes.
	 * 
	 * @param findMatrix - the first matrix in the union
	 * @param indiceMatrix - the second matrix in the union
	 * @return a matrix that is the union of the two parameters.
	 */
	public static Matrix union(Matrix findMatrix, Matrix indiceMatrix){

		ArrayList<Double> commonValues = new ArrayList<Double>();
		for (int row = 0; row < findMatrix.rowSize(); row++) {
			for (int column = 0; column < findMatrix.columnSize(); column++) {
				if(!commonValues.contains(findMatrix.get(row, column)))
					commonValues.add(findMatrix.get(row, column));
			}
		}

		for (int row = 0; row < indiceMatrix.rowSize(); row++) {
			for (int column = 0; column < indiceMatrix.columnSize(); column++) {
				if(!commonValues.contains(indiceMatrix.get(row, column)))
					commonValues.add(indiceMatrix.get(row, column));
			}
		}

		Collections.sort(commonValues,new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});

		Matrix tempMtrx = new SparseMatrix(commonValues.size(), 1);
		for(int i = 0; i < commonValues.size(); i++){
			tempMtrx.set(i, 0, commonValues.get(i));
		}
		return tempMtrx;
	}

	public static Matrix getColumns(Matrix phiMatrix, Matrix unionMatrix){
		Matrix tempMatrix = new SparseMatrix(phiMatrix.rowSize(), unionMatrix.rowSize());
		int column;
		for (int index = 0; index < unionMatrix.rowSize(); index++) {
			column = (int)unionMatrix.get(index, 0) - 1;
			for(int row = 0; row < phiMatrix.rowSize(); row++){
				if(column >= 0){
					tempMatrix.set(row, index, phiMatrix.get(row, column));
				}
			}
		}

		return tempMatrix;
	}

	/**
	 * Find the length of a matrix. This is the max of either the row length or column length.
	 * 
	 * @param mtrx - finding the length of this matrix
	 * @return the length of the matrix
	 */
	public static int length(Matrix mtrx){

		if(mtrx.rowSize() > mtrx.columnSize()){
			return mtrx.rowSize() ;
		}
		else{
			return mtrx.columnSize();
		}

	}

	/**
	 * Take the square root of all the values in a matrix.
	 * 
	 * @param mtrx - calculate the square root of all values in this matrix.
	 * @return a matrix that includes all values that are square roots of the 
	 * original matrix.
	 */
	public static Matrix squareRoot(Matrix mtrx){

		Matrix sqRootMatrix = new SparseMatrix(mtrx.rowSize(), mtrx.columnSize());
		for (int row = 0; row < sqRootMatrix.rowSize(); row++) {
			for (int column = 0; column < sqRootMatrix.columnSize(); column++) {
				sqRootMatrix.set(row, column, Math.sqrt(mtrx.get(row, column)));
			}
		}
		return sqRootMatrix;

	}

	/**
	 * Remove all columns after and including the final column parameter
	 * 
	 * @param mtrx - remove the columns from this matrix
	 * @param finalColumn - columns after and including this column should
	 * be removed from the mtrx.
	 * @return the modified matrix
	 */
	public static Matrix removeColumns(Matrix mtrx, int finalColumn){

		Matrix tempMatrix = new SparseMatrix(mtrx.rowSize(), finalColumn-1);
		for (int row = 0; row < mtrx.rowSize(); row++) {
			for (int column = 0; column < mtrx.columnSize(); column++) {
				if((column+1) < finalColumn){
					tempMatrix.set(row, column, mtrx.get(row, column));
				}
			}
		}
		return tempMatrix;

	}

	/**
	 * Return a matrix that only contains the last column of the matrix that is passed in
	 * 
	 * @param mtrx - obtain the last column of this matrix
	 * @return the column vector
	 */
	public static Matrix getLastColumn(Matrix mtrx){

		int lastColumn = mtrx.columnSize() - 1;

		Matrix tempMatrix = new SparseMatrix(mtrx.rowSize(), 1);
		for (int row = 0; row < mtrx.rowSize(); row++) {
			tempMatrix.set(row, 0, mtrx.get(row, lastColumn));
		}
		return tempMatrix;
	}
}
