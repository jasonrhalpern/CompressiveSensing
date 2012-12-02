package signals.algorithm;

import java.io.File;

import signals.processing.Signal;

public class ProcessSignals {

	public static void main(String[] args){
		
		Signal sparseSignal = new Signal(new File("xMatrix.txt"));
		sparseSignal.runCosamp();
	}
}
