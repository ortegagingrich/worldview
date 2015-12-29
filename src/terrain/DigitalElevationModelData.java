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
	
	public static void test(){
		System.out.println("Starting DEM test");
		
		DigitalElevationModelData testDEM = new DigitalElevationModelData(DEMType.ARCSECOND, 48, -123);
		
		float[][] data = testDEM.getData();
		System.out.println(data.length);
	}
	
	
	public DigitalElevationModelData(DEMType t, int lat, int lon){
		type = t;
		
		if(type == DEMType.ARCSECOND){
			String filelabel = generateFileLabel(t, lat, lon);
			filepath = File.DEM_ARCSECOND_PATH + filelabel + "/";
			filename = "img" + filelabel + "_1.img";
			
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
	
	
	//retrieve array containing DEM data
	public float[][] getData(){
		float[][] data = new float[][]{};
		
		//first load the file
		//TODO:
		
		return data;
	}
	
	
}
