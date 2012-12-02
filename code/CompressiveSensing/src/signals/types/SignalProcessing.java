package signals.types;

import org.apache.mahout.math.Matrix;

public interface SignalProcessing {
	
	public void encode();
	public void decode();
	public void printMatrix();
	public void multiplyMatrix(Matrix m);
}
