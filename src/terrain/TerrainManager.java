/**
 * Class of terrain managers; Normally, there is only one instance per application,
 * but it is still instantiated, just in case.  DEM objects live here and are not
 * copied or transfered anywhere else.
 */
package terrain;

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
		/*
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
	
	
	
	
	
	//tests
	public static void test(){
		TerrainManager tm = new TerrainManager();
	}
}
