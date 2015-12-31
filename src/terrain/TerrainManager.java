/**
 * Class of terrain managers; Normally, there is only one instance per application,
 * but it is still instantiated, just in case.  DEM objects live here and are not
 * copied or transfered anywhere else.
 */
package terrain;

import java.lang.Math;
import java.util.HashMap;

import io.FileUtils;
import terrain.DigitalElevationModelData;
import terrain.DEMType;

public class TerrainManager {
	
	//DEM hashmaps
	private HashMap<String, DigitalElevationModelData> DEM_1_as, DEM_2_as, DEM_ETOPO;
	
	
	public TerrainManager(){
		reloadDEMs();
	}
	
	
	public void reloadDEMs(){
		/**
		 * Reloads DEM files, taking changes that may have occurred since last loading
		 */
		
		//TODO: Reorganize and generalize this method; currently very quick and dirty
		
		DEM_1_as = new HashMap<String, DigitalElevationModelData>();
		DEM_2_as = new HashMap<String, DigitalElevationModelData>();
		DEM_ETOPO = new HashMap<String, DigitalElevationModelData>();
		
		//reload 1-arcsecond data
		String[] filenames = FileUtils.listFilesBase(FileUtils.DEM_ARCSECOND_PATH,"elev");
		
		for(String name : filenames){
			String[] parts = name.split("w");
			int lat = Integer.parseInt(parts[0].substring(1));
			int lon = -Integer.parseInt(parts[1]); //negative because these are west longitude
			
			DigitalElevationModelData dem = new DigitalElevationModelData(DEMType.ARCSECOND, lat, lon);
			DEM_1_as.put(name, dem);
		}
		
		System.out.format("Found %d DEM files at 1 arc-second resolution.\n", DEM_1_as.size());
	}
	
	
	public float getElevation(double lat, double lon){
		/**
		 * float getElevation(double lat, double lon)
		 * 
		 * Returns the best approximate elevation at the specified coordinates.
		 * This approximation is obtained by interpolating the finest DEM
		 * covering the area.
		 */
		//TODO: Generalize this to allow for other hemispheres
		
		DigitalElevationModelData dem;
		
		//for now, we just look for the matching one arc-second data
		int latBlock = (int) Math.ceil(lat);
		int lonBlock = (int) Math.ceil(-lon);
		String key = String.format("n%dw%d", latBlock, lonBlock);
		
		//check if arcsecond DEM is available
		dem = DEM_1_as.get(key);
		if(dem != null){
			return dem.getElevation(lat, lon);
		}
		
		return 0.0f;
	}
	
	
	
	//tests
	public static void test(){
		TerrainManager tm = new TerrainManager();
		
		double testLat = 33.765219;
		double testLon = -118.364572;
		float testelev = tm.getElevation(testLat, testLon);
		System.out.println(testelev * 3.28084);
		
		
		float trials = 1e6f;
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < trials; i++){
			testelev = tm.getElevation(testLat, testLon);
		}
		long endTime = System.currentTimeMillis();
		System.out.format("Timing data (elevation:%f): \n", trials);
		System.out.println((endTime - startTime)/1000.0);
	}
}
