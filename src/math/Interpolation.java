/**
 * Uninstantiable class containing static routines for interpolation.
 */
package math;

public class Interpolation{
	
	public static float bilinear(float nw, float ne, float sw, float se, float sWeight, float eWeight){
		float top = eWeight * ne + (1 - eWeight) * nw;
		float bot = eWeight * se + (1 - eWeight) * sw;
		return sWeight * bot + (1 - sWeight) * top;
	}
}
