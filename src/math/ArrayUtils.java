/*
 * Uninstantiated class with some useful commands for working with double arrays
 * in a manner similar to MATLAB.
 */
package math;

import java.util.Arrays;

public class ArrayUtils {
	
	
	public static double[] linspace(double low, double high, int npts){
		/**
		 * double[] linspace(double low, double high, int npts)
		 * 
		 * Creates and returns an array of npts + 1 equally spaced doubles
		 * ranging in value from low to high, inclusive.
		 */
		double[] array = new double[npts + 1];
		double delta = (high - low)/npts;
		
		double currentValue = low;
		for(int i = 0; i < npts; i++){
			array[i] = currentValue;
			currentValue += delta;
		}
		array[npts] = high;
		
		return array;
	}
	
	
	
	
	public static void test(){
		double low = 1;
		double high = 12.567;
		int npts = 25;
		System.out.println("Starting Linspace test: linspace(1, 12.567, 25)");
		double[] array = linspace(low, high, npts);
		System.out.println(Arrays.toString(array));
	}
}
