package signals.types;

import org.apache.mahout.math.Matrix;

/**
 * This interface can eventually be used to for all different types of 
 * signals - image, light, sound. It should be possible to encode and decode
 * all these different types of signals using our recovery algorithm.
 * 
 * @author Jason Halpern
 * @version 1.0 12/09/12
 *
 */
public interface SignalProcessing {
	
	public void encode();
	public void decode();
	public void printMatrix();
	public void multiplyMatrix(Matrix m);
}
