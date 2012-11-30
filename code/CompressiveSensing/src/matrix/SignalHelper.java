package matrix;

import java.util.ArrayList;
import java.util.List;

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
	
	public static void setSignalLength(int length){
		SIGNAL_LENGTH = length;
	}
	
	public static void setSignalSparsity(int sparsity){
		SIGNAL_SPARSITY = sparsity;
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
	public static List<Matrix> cosampAlgo(Matrix measurementMatrix, Matrix phiMatrix,
			int signalSparsity, int iterations){

		measurementMatrix = MatrixHelper.toSingleColumn(measurementMatrix);

		int numRows = phiMatrix.rowSize();
		int numColumns = phiMatrix.columnSize();

		Matrix xCosampMatrix = new SparseMatrix(numColumns, iterations);
		xCosampMatrix = MatrixHelper.fillWithZeros(xCosampMatrix);

		int count = 0;
		int verbose = 0;
		double tolerance = 0.001;
		Matrix sCosampMatrix = new SparseMatrix(numColumns, 1);
		sCosampMatrix = MatrixHelper.fillWithZeros(sCosampMatrix);

		Matrix intermediateMatrix = null;
		Matrix rCosampMatrix = null;
		Matrix proxyCosampMatrix = null;
		Matrix indexMatrix = null;
		Matrix equalityMatrix = null;
		Matrix indiceMatrix = null;
		Matrix findMatrix = null;
		Matrix unionMatrix = null;
		Matrix slicedPhiMatrix = null;
		Matrix slicedPhiTranspose = null;
		Matrix wCosampMatrix = null;
		Matrix trashMatrix = null;
		double res;
		Matrix tempMatrix = null;
		Matrix slicedbb2Matrix = null;
		Matrix slicedXCosampMatrixOne = new SparseMatrix(SignalHelper.getSignalLength(), 1);
		Matrix slicedXCosampMatrixTwo = new SparseMatrix(SignalHelper.getSignalLength(), 1);
		Matrix bb2Matrix = new SparseMatrix(numColumns, 1);
		double normOne = 0;
		double normTwo = 0;
		int iterz = 0;
		List<Matrix> matrixList = new ArrayList<Matrix>();
		List<Matrix> cgSolveMatrices = new ArrayList<Matrix>();
		List<Matrix> finalMatrices = new ArrayList<Matrix>();
		while(count < iterations){
			//backprojection
			intermediateMatrix = phiMatrix.times(sCosampMatrix);
			
			rCosampMatrix = measurementMatrix.minus(intermediateMatrix);
			
			proxyCosampMatrix = phiMatrix.transpose().times(rCosampMatrix);
			proxyCosampMatrix = MatrixHelper.getAbsMatrix(proxyCosampMatrix);

			matrixList = MatrixHelper.sortDescending(proxyCosampMatrix);
			trashMatrix = matrixList.get(0);
			indexMatrix = matrixList.get(1);

			equalityMatrix = MatrixHelper.notEqual(sCosampMatrix, 0.0);
			indiceMatrix = MatrixHelper.getIndices(indexMatrix, 1, (2*SignalHelper.getSignalSparsity()));

			findMatrix = MatrixHelper.findNonzero(equalityMatrix);
			unionMatrix = MatrixHelper.union(findMatrix, indiceMatrix);
			
			slicedPhiMatrix = MatrixHelper.getColumns(phiMatrix, unionMatrix);
			slicedPhiTranspose = slicedPhiMatrix.transpose();

			//estimate
			cgSolveMatrices = cgSolve(slicedPhiTranspose.times(slicedPhiMatrix), 
					slicedPhiTranspose.times(measurementMatrix),
					tolerance, SignalHelper.getMaxIterations(), verbose);
			
			wCosampMatrix = cgSolveMatrices.get(0);
			res= cgSolveMatrices.get(1).get(0, 0);
			iterz = (int)cgSolveMatrices.get(2).get(0, 0);
			

			bb2Matrix = MatrixHelper.fillWithZeros(bb2Matrix);

			bb2Matrix = MatrixHelper.setCellValues(bb2Matrix, unionMatrix, wCosampMatrix);

			//prune
			count++;
			tempMatrix = MatrixHelper.copyMatrix(bb2Matrix);
			tempMatrix = MatrixHelper.getAbsMatrix(tempMatrix);
			matrixList = MatrixHelper.sortDescending(tempMatrix);
			trashMatrix = matrixList.get(0);
			indexMatrix = matrixList.get(1);

			sCosampMatrix = bb2Matrix.times(0);

			indiceMatrix = MatrixHelper.getIndices(indexMatrix, 1, signalSparsity);
			slicedbb2Matrix = MatrixHelper.getIndices(bb2Matrix, indiceMatrix);
			sCosampMatrix = MatrixHelper.setCellValues(sCosampMatrix, indiceMatrix, slicedbb2Matrix);
			
			if(count < 10)
				xCosampMatrix = MatrixHelper.modifyColumn(xCosampMatrix, count, sCosampMatrix);
			
			if(count < 10){
				slicedXCosampMatrixOne = MatrixHelper.getColumn(xCosampMatrix, count);
				slicedXCosampMatrixTwo = MatrixHelper.getColumn(xCosampMatrix, count - 1);

				normOne = MatrixHelper.norm(slicedXCosampMatrixOne.minus(slicedXCosampMatrixTwo));
				normTwo = MatrixHelper.norm(slicedXCosampMatrixOne);

				if(normOne < (.01* normTwo)){
					break;
				}
			}
		}
		count++;
		xCosampMatrix = MatrixHelper.removeColumns(xCosampMatrix, count);

		Matrix xHatMatrix = new SparseMatrix(xCosampMatrix.rowSize(), 1);
		xHatMatrix = MatrixHelper.getLastColumn(xCosampMatrix);

		finalMatrices.add(xHatMatrix);
		finalMatrices.add(xCosampMatrix);

		return finalMatrices;
	}

	public static List<Matrix> cgSolve(Matrix firstMatrix, Matrix secondMatrix,
			double tolerance, int maxIterations, int verbose){

		List<Matrix> matrixList = new ArrayList<Matrix>();
		Matrix temp = new SparseMatrix(1, 1);

		Matrix xMatrix = new SparseMatrix(MatrixHelper.length(secondMatrix), 1);
		xMatrix = MatrixHelper.fillWithZeros(xMatrix);

		Matrix rMatrix = secondMatrix;
		Matrix dMatrix = rMatrix;

		Matrix deltaMatrix = rMatrix.transpose().times(rMatrix);
		double delta = deltaMatrix.get(0, 0);
		Matrix deltaZeroMatrix = secondMatrix.transpose().times(secondMatrix);
		double deltaZero = deltaZeroMatrix.get(0, 0);

		int numIters = 0;

		Matrix bestXMatrix = xMatrix;
		double bestRes = Math.sqrt(delta/deltaZero);
		Matrix bestResMatrix = new SparseMatrix(1, 1);;
		Matrix qMatrix = null;
		double alpha;
		double deltaOld;
		double beta;
		while((numIters < maxIterations) && (delta > (Math.pow(tolerance, 2) * deltaZero))){
			qMatrix = firstMatrix.times(dMatrix);
			alpha= delta/(dMatrix.transpose().times(qMatrix).get(0, 0));
			xMatrix = xMatrix.plus((dMatrix.times(alpha)));
	
			if(((numIters+1)%50) == 0){
				rMatrix = secondMatrix.minus(firstMatrix.times(xMatrix));
			}
			else{
				rMatrix = rMatrix.minus(qMatrix.times(alpha));
			}
			deltaOld = delta;
			deltaMatrix = rMatrix.transpose().times(rMatrix);
			delta = deltaMatrix.get(0, 0);
			beta = delta/deltaOld;
			dMatrix = rMatrix.plus(dMatrix.times(beta));
			numIters++;
			if(Math.sqrt(delta/deltaZero) < bestRes){
				bestXMatrix = xMatrix;
				bestRes = Math.sqrt(delta/deltaZero) ;
			}

		}
		temp.set(0, 0, numIters);
		bestResMatrix.set(0, 0, bestRes);
		matrixList.add(bestXMatrix);
		matrixList.add(bestResMatrix);
		matrixList.add(temp);


		return matrixList;
	}
}
