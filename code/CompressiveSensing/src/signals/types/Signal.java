/** \file
*/
package signals.types;

import org.apache.mahout.math.Matrix;

public interface Signal {
	
	public void encode();
	public void decode();
	public void printMatrix();
	public void multiplyMatrix(Matrix m);
}
