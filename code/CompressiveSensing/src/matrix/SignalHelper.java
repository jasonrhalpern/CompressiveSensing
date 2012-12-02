package matrix;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;

import signals.processing.Signal;

public class SignalHelper {

	//implementation of cosamp algorithm, follows cosamp.m from the Rice Compressive Sensing toolbox
	public static Matrix cosampAlgo(Signal sparse, Matrix measurementMatrix, Matrix phiMatrix,
			int signalSparsity, int iterations){

		measurementMatrix = MatrixHelper.toSingleColumn(measurementMatrix);

		Matrix xCosampMatrix = new SparseMatrix(phiMatrix.columnSize(), iterations);
		xCosampMatrix = MatrixHelper.fillWithZeros(xCosampMatrix);

		int count = 0;
		int verbose = 0;
		double tolerance = 0.001;
		Matrix sCosampMatrix = new SparseMatrix(phiMatrix.columnSize(), 1);
		sCosampMatrix = MatrixHelper.fillWithZeros(sCosampMatrix);
		Matrix bb2Matrix = new SparseMatrix(phiMatrix.columnSize(), 1);

		while(count < iterations){

			//backprojection
			Matrix unionMatrix = backProjection(phiMatrix, sCosampMatrix, 
					measurementMatrix, sparse.getSignalSparsity());

			Matrix slicedPhiMatrix = MatrixHelper.getColumns(phiMatrix, unionMatrix);
			Matrix slicedPhiTranspose = slicedPhiMatrix.transpose();

			//estimate using the cgSolve function
			Matrix wCosampMatrix = cgSolve(slicedPhiTranspose.times(slicedPhiMatrix), 
					slicedPhiTranspose.times(measurementMatrix),
					tolerance, sparse.getMaxIterations(), verbose);

			bb2Matrix = MatrixHelper.fillWithZeros(bb2Matrix);
			bb2Matrix = MatrixHelper.setCellValues(bb2Matrix, unionMatrix, wCosampMatrix);

			//prune
			count++;
			Matrix tempMatrix = MatrixHelper.copyMatrix(bb2Matrix);
			tempMatrix = MatrixHelper.getAbsMatrix(tempMatrix);
			Matrix indexMatrix = MatrixHelper.sortDescending(tempMatrix).get(1);

			sCosampMatrix = bb2Matrix.times(0);

			Matrix indiceMatrix = MatrixHelper.getIndices(indexMatrix, 1, signalSparsity);
			Matrix slicedbb2Matrix = MatrixHelper.getIndices(bb2Matrix, indiceMatrix);
			sCosampMatrix = MatrixHelper.setCellValues(sCosampMatrix, indiceMatrix, slicedbb2Matrix);

			if(count < 10){
				xCosampMatrix = MatrixHelper.modifyColumn(xCosampMatrix, count, sCosampMatrix);
				
				if(testBreakpoint(xCosampMatrix, sparse.getSignalLength(), count)){
					break;
				}
			}
		}
		count++;
		xCosampMatrix = MatrixHelper.removeColumns(xCosampMatrix, count);

		Matrix xHatMatrix = new SparseMatrix(xCosampMatrix.rowSize(), 1);
		xHatMatrix = MatrixHelper.getLastColumn(xCosampMatrix);

		return xHatMatrix;
	}
	
	public static boolean testBreakpoint(Matrix xCosampMatrix, int signalLength, int count){
		
		Matrix slicedXCosampMatrixOne = new SparseMatrix(signalLength, 1);
		Matrix slicedXCosampMatrixTwo = new SparseMatrix(signalLength, 1);
		
		slicedXCosampMatrixOne = MatrixHelper.getColumn(xCosampMatrix, count);
		slicedXCosampMatrixTwo = MatrixHelper.getColumn(xCosampMatrix, count - 1);

		double normOne = MatrixHelper.norm(slicedXCosampMatrixOne.minus(slicedXCosampMatrixTwo));
		double normTwo = MatrixHelper.norm(slicedXCosampMatrixOne);

		if(normOne < (.01* normTwo)){
			return true;
		}
		return false;
	}

	public static Matrix backProjection(Matrix phiMatrix, Matrix sCosampMatrix, 
			Matrix measurementMatrix, int sparsity){

		Matrix intermediateMatrix = phiMatrix.times(sCosampMatrix);
		Matrix rCosampMatrix = measurementMatrix.minus(intermediateMatrix);

		Matrix proxyCosampMatrix = phiMatrix.transpose().times(rCosampMatrix);
		proxyCosampMatrix = MatrixHelper.getAbsMatrix(proxyCosampMatrix);
		Matrix indexMatrix = MatrixHelper.sortDescending(proxyCosampMatrix).get(1);

		Matrix equalityMatrix = MatrixHelper.notEqual(sCosampMatrix, 0.0);
		Matrix indiceMatrix = MatrixHelper.getIndices(indexMatrix, 1, (2*sparsity));

		Matrix findMatrix = MatrixHelper.findNonzero(equalityMatrix);

		return MatrixHelper.union(findMatrix, indiceMatrix);
	}

	//corresponds to the function in cgsolve.m
	public static Matrix cgSolve(Matrix firstMatrix, Matrix secondMatrix,
			double tolerance, int maxIterations, int verbose){

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
		while((numIters < maxIterations) && (delta > (Math.pow(tolerance, 2) * deltaZero))){
			Matrix qMatrix = firstMatrix.times(dMatrix);
			double alpha= delta/(dMatrix.transpose().times(qMatrix).get(0, 0));
			xMatrix = xMatrix.plus((dMatrix.times(alpha)));

			if(((numIters+1) % 50) == 0){
				rMatrix = secondMatrix.minus(firstMatrix.times(xMatrix));
			}
			else{
				rMatrix = rMatrix.minus(qMatrix.times(alpha));
			}
			double deltaOld = delta;
			deltaMatrix = rMatrix.transpose().times(rMatrix);
			delta = deltaMatrix.get(0, 0);
			double beta = delta/deltaOld;
			dMatrix = rMatrix.plus(dMatrix.times(beta));
			numIters++;
			if(Math.sqrt(delta/deltaZero) < bestRes){
				bestXMatrix = xMatrix;
				bestRes = Math.sqrt(delta/deltaZero) ;
			}

		}
		temp.set(0, 0, numIters);
		bestResMatrix.set(0, 0, bestRes);

		return bestXMatrix;
	}
}
