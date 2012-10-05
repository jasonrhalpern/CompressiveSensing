package matrix;

public class SignalHelper {

	private static int SIGNAL_LENGTH = 1024;
	private static int SIGNAL_SPARSITY = 40;
	private static int NUM_MEASUREMENTS = 240;
	private static int NUM_ITERATIONS = 10;
	
	public static int getSignalLength(){
		return SIGNAL_LENGTH;
	}
	
	public static int getSignalSparsity(){
		return SIGNAL_SPARSITY;
	}
	
	public static int getNumMeasurements(){
		return NUM_MEASUREMENTS;
	}
	
	public static int getIterations(){
		return NUM_ITERATIONS;
	}
}
