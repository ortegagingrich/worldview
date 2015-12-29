/*
 * Basic file input/output utilities
 */
package io;

public class File{
	
	//May be modified later
	public static final String FS_PATH = System.getenv("FS") + "/";
	
	//Data Paths
	public static final String DATA_PATH = FS_PATH + "data/";
	public static final String DEM_ARCSECOND_PATH = DATA_PATH + "dem/1_arc-second/";
	
	
	//for reading numpy array (32 bit)
	public static float[][] loadNumpyArrayFloat32(String filepath){
		float[][] array = new float[][]{};
		
		return array;
	}
}
