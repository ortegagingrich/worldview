/**
 * Class of Stereographic projections; parameters of the projection are pre-
 * generated and stored when the projection object is created (i.e. projection
 * is defined)
 */
 package math;
 
 import java.lang.Math;
 
 import com.jme3.math.Vector2f;
 
 import math.CoordinateLatLon;
 import math.Projection;
 
 public class ProjectionStereographic extends Projection {
 	
 	private final double deg2rad = 0.017453292519943295;
 	private final double rad2deg = 57.29577951308232;
 	
 	//Basic parameters for defining the projection transform
 	private double phi1, lambda0; // the center of the projection
 	private double planetRadius;
 	
 	//Pre-computed values
 	private double sinphi1, cosphi1, scaleFactor;
 	
 	
 	//Creation of the projection
 	public ProjectionStereographic(CoordinateLatLon center){
 		setup(center.lat, center.lon);
 	}
 	
 	public ProjectionStereographic(double centerLat, double centerLon){
 		setup(centerLat, centerLon);
 	}
 	
 	private void setup(double centerLat, double centerLon){
 		//First convert to radians
 		phi1 = deg2rad * centerLat;
 		lambda0 = deg2rad * centerLon;
 		planetRadius = 6371000.0; //in meters
 		
 		//precompute values
 		sinphi1 = Math.sin(phi1);
 		cosphi1 = Math.cos(phi1);
 		scaleFactor = 1.0/(2*planetRadius);
 	}
 	
 	
 	//get transform parameters
 	public CoordinateLatLon getCenter(){
 		double latCenter = rad2deg * phi1;
 		double lonCenter = rad2deg * lambda0;
 		return new CoordinateLatLon(latCenter, lonCenter);
 	}
 	
 	
 	//Overridden transform methods
 	@Override
 	protected Vector2f forward(double lat, double lon){
 		//first convert to radians
 		double phi = deg2rad * lat;
 		double lambda = deg2rad * lon;
 		
 		//some precomputed values
 		double sinphi = Math.sin(phi);
 		double cosphi = Math.cos(phi);
 		double coscoslamdiff = cosphi * Math.cos(lambda - lambda0);
 		
 		//do the transform
 		double k = (2.0*planetRadius)/(1.0 + sinphi1*sinphi + cosphi1*coscoslamdiff);
 		double x = k * cosphi * Math.sin(lambda - lambda0);
 		double y = k * (cosphi1*sinphi - sinphi1*coscoslamdiff);
 		
 		//cast to float and return vector
 		return new Vector2f((float) x, (float) y);
 	}
 	
 	
 	@Override
 	protected CoordinateLatLon inverse(float x, float y){
 		//if near the center
 		double rho = Math.sqrt(x*x + y*y);
 		if(Math.abs(rho) < 1e-3){
 			return getCenter();
 		}
 		//Otherwise, do the inverse transform
 		
 		//some more pre-computed values
 		double c = 2 * Math.atan(rho * scaleFactor);
 		double sinc = Math.sin(c);
 		double cosc = Math.cos(c);
 		double ysinc = y * sinc;
 		
 		double phi = Math.asin(cosc*sinphi1 + ysinc*cosphi1/rho);
 		double lambda = lambda0 + Math.atan(x*sinc/(rho*cosphi1*cosc - ysinc*sinphi1));
 		
 		//convert to degrees
 		double lat = rad2deg * phi;
 		double lon = rad2deg * lambda;
 		
 		//make coordinates and return
 		return new CoordinateLatLon(lat, lon);
 	}
 	
 	
 	
 	
 	//Some tests
 	public static void test(){
 		System.out.println("Stereographic tests");
 		//TODO: Insert tests
 		Projection p1 = new ProjectionStereographic(46, 47);
 		Projection p2 = new ProjectionStereographic(75, -120);
 		
 		//forward transform tests
 		Vector2f res1 = p1.transformForward(45.0, 45.0);
 		Vector2f res2 = p1.transformForward(85.0, 65.0);
 		Vector2f res3 = p2.transformForward(58.0, -130.0);
 		Vector2f res4 = p2.transformForward(11.0, -133.0);
 		
 		System.out.println(res1);
 		System.out.println(res2);
 		System.out.println(res3);
 		System.out.println(res4);
 		
 		CoordinateLatLon c1, c2, c3, c4;
 		c1 = p1.transformInverse(res1);
 		c2 = p1.transformInverse(res2);
 		c3 = p2.transformInverse(res3);
 		c4 = p2.transformInverse(res4);
 		
 		c1.print();
 		c2.print();
 		c3.print();
 		c4.print();
 	}
 	
 }
