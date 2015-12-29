/*
 * Basic file input/output utilities
 */
package io;

import java.io.IOException;
import java.nio.*;
import java.nio.file.*;
import java.util.ArrayList;

public class File{
	
	//May be modified later
	public static final String FS_PATH = System.getenv("FS") + "/";
	
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
			
			System.out.println(bytes.length);
			for(byte b : bytes){
				System.out.println(binary(b));
			}
			
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			for(int i = 0; i < 3; i++){
				System.out.println(buffer.getFloat());
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
	
	
	//temporary for testing
	public static void test(){
		String filepath = "/home/jog/scratch/simple.elev";
		try{
			float[][] shouldbepi = loadNumpyArrayFloat32(filepath, 1, 1);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
