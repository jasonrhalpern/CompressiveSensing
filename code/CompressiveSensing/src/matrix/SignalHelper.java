package matrix;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.math.DiagonalMatrix;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.QRDecomposition;
import org.apache.mahout.math.SparseMatrix;
import org.apache.mahout.math.Vector;

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
		Matrix indexMatrix = null;
		Matrix equalityMatrix = null;
		Matrix indiceMatrix = null;
		Matrix findMatrix = null;
		Matrix unionMatrix = null;
		Matrix slicedPhiMatrix = null;
		Matrix slicedPhiTranspose = null;
		Matrix wCosampMatrix = null;
		Matrix resMatrix = null;
		Matrix tempMatrix = null;
		Matrix slicedbb2Matrix = null;
		Matrix slicedXCosampMatrixOne = null;
		Matrix slicedXCosampMatrixTwo = null;
		Matrix bb2Matrix = new SparseMatrix(numColumns, 1);
		double normOne = 0;
		double normTwo = 0;
		int iter = 0;
		List<Matrix> matrixList = new ArrayList<Matrix>();
		List<Matrix> cgSolveMatrices = new ArrayList<Matrix>();
		while(count <= iterations){
			//backprojection
			intermediateMatrix = phiMatrix.times(sCosampMatrix);
			rCosampMatrix = measurementMatrix.minus(intermediateMatrix);
			proxyCosampMatrix = phiMatrix.transpose().times(rCosampMatrix);
			proxyCosampMatrix = MatrixHelper.getAbsMatrix(proxyCosampMatrix);
			
			matrixList = MatrixHelper.sortDescending(proxyCosampMatrix);
			proxyCosampMatrix = matrixList.get(0);
			indexMatrix = matrixList.get(1);
			equalityMatrix = MatrixHelper.notEqual(sCosampMatrix, 0);
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
			resMatrix = cgSolveMatrices.get(1);
			iter = (int)cgSolveMatrices.get(2).get(0, 0);
			
			bb2Matrix = MatrixHelper.fillWithZeros(bb2Matrix);
			bb2Matrix = MatrixHelper.setCellValues(bb2Matrix, unionMatrix, wCosampMatrix);
			
			//prune
			count++;
			tempMatrix = MatrixHelper.getAbsMatrix(bb2Matrix);
			matrixList = MatrixHelper.sortDescending(tempMatrix);
			proxyCosampMatrix = matrixList.get(0);
			indexMatrix = matrixList.get(1);
			sCosampMatrix = bb2Matrix.times(0);
			
			indiceMatrix = MatrixHelper.getIndices(indexMatrix, 1, signalSparsity);
			slicedbb2Matrix = MatrixHelper.getIndices(bb2Matrix, indiceMatrix);
			sCosampMatrix = MatrixHelper.setCellValues(sCosampMatrix, indiceMatrix, slicedbb2Matrix);
			xCosampMatrix = MatrixHelper.modifyColumn(xCosampMatrix, count, sCosampMatrix);
			slicedXCosampMatrixOne = MatrixHelper.getColumn(xCosampMatrix, count);
			slicedXCosampMatrixTwo = MatrixHelper.getColumn(xCosampMatrix, count - 1);
			normOne = MatrixHelper.norm(slicedXCosampMatrixOne.minus(slicedXCosampMatrixTwo));
			normTwo = MatrixHelper.norm(slicedXCosampMatrixOne);
			if(normOne < (.01* normTwo)){
				break;
			}
		}
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
		Matrix deltaZeroMatrix = secondMatrix.transpose().times(secondMatrix);
		
		int numIters = 0;
		
		Matrix bestXMatrix = xMatrix;
		Matrix bestResMatrix = MatrixHelper.squareRoot(deltaMatrix/deltaZeroMatrix);
		Matrix qMatrix = null;
		Matrix alphaMatrix = null;
		Matrix deltaOldMatrix = null;
		Matrix betaMatrix = null;
		while((numIters < maxIterations) && (deltaMatrix > (Math.pow(tolerance, 2) * deltaZeroMatrix))){
			qMatrix = firstMatrix.times(dMatrix);
			alphaMatrix = deltaMatrix/(dMatrix.transpose().times(qMatrix));
			xMatrix = xMatrix.plus((alphaMatrix.times(dMatrix)));
			if(((numIters+1)%50) == 0){
				rMatrix = secondMatrix.minus(firstMatrix.times(xMatrix));
			}
			else{
				rMatrix = rMatrix.minus(alphaMatrix.times(qMatrix));
			}
			deltaOldMatrix = deltaMatrix;
			deltaMatrix = rMatrix.transpose().times(rMatrix);
			betaMatrix = deltaMatrix/deltaOldMatrix;
			dMatrix = rMatrix.plus(betaMatrix.times(dMatrix));
			numIters++;
			if(MatrixHelper.squareRoot(delta/delta0) < bestResMatrix){
				bestXMatrix = xMatrix;
				bestResMatrix = MatrixHelper.squareRoot(delta/delta0) ;
			}
		}
		temp.set(0, 0, numIters);
		matrixList.add(bestXMatrix);
		matrixList.add(bestResMatrix);
		matrixList.add(temp);
		
		
		return matrixList;
	}
}
