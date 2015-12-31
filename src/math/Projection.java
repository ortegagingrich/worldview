/**
 * Abstract class of spherical projections; contain methods for converting back
 * and forth between lat/lon coordinates and the 2D mapped space
 */
package math;

import com.jme3.math.Vector2f;

import math.CoordinateLatLon;

public abstract class Projection {
	
	public Vector2f transformForward(CoordinateLatLon coordinate){
		return forward(coordinate.lat, coordinate.lon);
	}
	
	public Vector2f transformForward(double lat, double lon){
		return forward(lat, lon);
	}
	
	public CoordinateLatLon transformInverse(Vector2f vector){
		return inverse(vector.x, vector.y);
	}
	
	public CoordinateLatLon transformInverse(float x, float y){
		return inverse(x, y);
	}
	
	//internal abstract methods
	protected abstract Vector2f forward(double lat, double lon);
	protected abstract CoordinateLatLon inverse(float x, float y);
	
}
