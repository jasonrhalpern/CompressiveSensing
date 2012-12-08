package signals.algorithm;

import java.io.File;

import signals.processing.Signal;

public class ProcessSignals {
	
	private static final int NUM_ITERATIONS = 10;
	private static final int MAX_ITERATIONS = 1000;
	
	public static int getNumIterations(){
		return NUM_ITERATIONS;
	}

	public static int getMaxIterations(){
		return MAX_ITERATIONS;
	}

	public static void main(String[] args){
		
		//run the cosamp algorithm on the signal represented in the file
		Signal sparseSignal = new Signal(new File("xMatrix.txt"));
		sparseSignal.runCosamp(NUM_ITERATIONS);
	}
}
