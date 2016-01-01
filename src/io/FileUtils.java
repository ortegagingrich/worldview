/*
 * Basic file input/output utilities
 */
package io;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.ArrayList;

public class FileUtils{
	
	//May be modified later
	public static final String FS_PATH = System.getenv("FS") + "/";
	
	//Asset Path
	public static final String ASSET_PATH = FS_PATH + "render/assets/";
	
	//Data Paths
	public static final String DATA_PATH = FS_PATH + "data/";
	public static final String DEM_ARCSECOND_PATH = DATA_PATH + "dem/1_arc-second/";
	
	
	//for reading numpy array (32 bit)
	public static float[][] loadNumpyArrayFloat32(String filepath, int size_i, int size_j) throws IOException {
		float[][] array;
		try{
			array = new float[size_i][size_j];
			
			//get all bytes from the file and load them into a buffer
			byte[] bytes = Files.readAllBytes(Paths.get(filepath));
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			
			//populate data array
			for(int i = 0; i < size_i; i++){
				for(int j = 0; j < size_j; j++){
					array[i][j] = buffer.getFloat();
				}
			}
			
		}catch(IOException ex){
			array = null;
			throw ex;
		}
		
		return array;
	}
	
	private static String binary(byte b){
		return Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
	}
	
	
	public static String[] listFilesBase(String directory, String extension){
		/*
		 * Returns an array with all basenames (i.e. without extensions) of
		 * files in the specified directory with the specified extension
		 */
		 try{
		 	File dir = new File(directory);
		 	File[] files = dir.listFiles();
		 	
		 	ArrayList<String> matchingBases = new ArrayList<String>();
		 	
		 	for(File file : files){
		 		String name = file.getName();
		 		
		 		String[] parts = name.split("\\.");
		 		
		 		if(parts.length == 2){ 
		 			if(parts[1].equals(extension)){
		 				matchingBases.add(parts[0]);
		 			}
		 		}
		 		
		 	}
		 	
		 	return matchingBases.toArray(new String[matchingBases.size()]);
		 	
		 }catch(Exception ex){
		 	ex.printStackTrace();
		 	return new String[]{};
		 }
	}
	
	
	
	
	
	//temporary for testing
	public static void test(){
		String filepath = "/home/jog/scratch/simple.elev";
		try{
			float[][] array = loadNumpyArrayFloat32(filepath, 2, 2);
			System.out.println(Arrays.deepToString(array));
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
