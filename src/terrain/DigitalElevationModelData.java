/*
 * Class of DEM files; one instance per file, which keeps track of the file location, etc.
 */
package terrain;

import java.lang.Math;

import io.File;
import terrain.DEMType;
import math.Interpolation;

public class DigitalElevationModelData{
	
	private double lowLat, highLat, lowLon, highLon;
	private double dlat, dlon; //in degrees
	private int nlat, nlon; //not including overlapping cells
	private DEMType type;
	private String filename;
	private String filepath;
	
	private float[][] dataArray = null;
	private boolean isLoaded = false;
	
	
	public static void test(){
		System.out.println("Starting DEM test");
		
		DigitalElevationModelData testDEM = new DigitalElevationModelData(DEMType.ARCSECOND, 35, -119);
		
		float[][] data = testDEM.getData();
		System.out.println(data[6][6]);
		System.out.println(data[6][7]);
		
		float delta = 0.5f/3600;
		System.out.println(testDEM.getElevation(35.0-delta,-119.0+3.0*delta));
		
		//quick timing test
		float trials = 1e8f;
		float dummy;
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < trials; i++){
			dummy = testDEM.getElevation(34.5, -118.5);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Timing data: ");
		System.out.println((endTime - startTime)/1000.0);
	}
	
	
	public DigitalElevationModelData(DEMType t, int lat, int lon){
		type = t;
		
		if(type == DEMType.ARCSECOND){
			String filelabel = generateFileLabel(t, lat, lon);
			filepath = File.DEM_ARCSECOND_PATH + "/";
			filename = filelabel + ".elev";
			
			//assume standard range
			highLat = lat;
			highLon = lon + 1.0;
			lowLat = lat - 1.0;
			lowLon = lon;
			nlat = 3600;//interior cells
			nlon = 3600;
			dlat = 1.0/3600.0;
			dlon = 1.0/3600.0;
			
		}else if(type == DEMType.TWO_ARCSECOND){
			//TODO: Fill me in
		}else if(type == DEMType.ETOPO){
			//TODO: Fill me in.
		}
	}
	
	private String generateFileLabel(DEMType t,int lat, int lon){
		String label;
		
		label = "n" + Integer.toString(lat) + "w" + Integer.toString(Math.abs(lon));
		
		return label;
	}
	
	
	private void loadDataFromFile(){
		try{
			dataArray = File.loadNumpyArrayFloat32(filepath + filename, 3612, 3612);
			
			isLoaded = true;
		}catch(Exception ex){
			System.out.println("Failed to load DEM from file: " + filepath + filename);
		}
		
		isLoaded = true;
	}
	
	private void unloadData(){
		dataArray = null;
		isLoaded = false;
	}
	
	//retrieve array containing DEM data
	public float[][] getData(){
		if(!isLoaded){
			loadDataFromFile();
		}
		
		return dataArray;
	}
	
	
	public float getElevation(double lat, double lon){
		/**
		 * float getElevation(double lat, double lon)
		 * 
		 * Returns the interpolated elevation at the specified coordinates if
		 * they lie within the domain of this DEM.  Otherwise, 0.0f is returned.
		 */
		if(!isLoaded){
			loadDataFromFile();
		}
		
		//First, compute offset fractions; i,j = 0 represents northwest corner.
		double lonFrac = lon - lowLon;
		double latFrac = highLat - lat;
		
		//if outside of range, just return the default value
		if(lonFrac < -0.00111 || latFrac < -0.00111 || lonFrac > 1.00111 || latFrac > 1.00111){
			return 0.0f;
		}
		
		//lower indices to be used for interpolation
		int lonIndex = 5 + (int) Math.round(nlon * lonFrac); //West
		int latIndex = 5 + (int) Math.round(nlat * latFrac); //North
		
		//retrieve values for interpolation
		float valNW = dataArray[latIndex][lonIndex];
		float valNE = dataArray[latIndex][lonIndex + 1];
		float valSW = dataArray[latIndex + 1][lonIndex];
		float valSE = dataArray[latIndex + 1][lonIndex + 1];
		
		//get weights for interpolation
		float eWeight = ((float) (lonFrac/dlon)) + 5.5f - lonIndex;
		float sWeight = ((float) (latFrac/dlat)) + 5.5f - latIndex;
		
		
		return Interpolation.bilinear(valNW, valNE, valSW, valSE, sWeight, eWeight);
	}
	
	
}
