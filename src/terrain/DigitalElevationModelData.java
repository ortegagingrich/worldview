/*
 * Class of DEM files; one instance per file, which keeps track of the file location, etc.
 */
package terrain;


import io.File;
import terrain.DEMType;

public class DigitalElevationModelData{
	
	private double low_lat, high_lat, low_lon, high_lon;
	private DEMType type;
	private String filename;
	private String filepath;
	
	private float[][] dataArray = null;
	private boolean isLoaded = false;
	
	
	public static void test(){
		System.out.println("Starting DEM test");
		
		DigitalElevationModelData testDEM = new DigitalElevationModelData(DEMType.ARCSECOND, 48, -123);
		
		float[][] data = testDEM.getData();
		System.out.println(data[0][1]);
	}
	
	
	public DigitalElevationModelData(DEMType t, int lat, int lon){
		type = t;
		
		if(type == DEMType.ARCSECOND){
			String filelabel = generateFileLabel(t, lat, lon);
			filepath = File.DEM_ARCSECOND_PATH + "/";
			filename = filelabel + ".elev";
			
			//assume standard range
			high_lat = lat;
			high_lon = lon + 1.0;
			low_lat = lat - 1.0;
			low_lon = lon;
			
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
	
	
}
