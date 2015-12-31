/**
 * Just a small class for lattitude/longitude coordinates
 */
package math;

public class CoordinateLatLon{
	public double lat, lon;
	
	public CoordinateLatLon(double la, double lo){
		lat = la;
		lon = lo;
	}
	
	public void print(){
		System.out.format("Coordinate: (%f, %f) \n", lat, lon);
	}
}
