/*
 * Some pointers regarding the JME3 coordinate system:
 * x: north(+)/south(-)
 * y: up(+)/down(-)
 * z: east(+)/west(-)
 */
package run;

import java.util.logging.*;
import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.*;
import com.jme3.terrain.heightmap.*;
import com.jme3.terrain.geomipmap.lodcalc.*;
import com.jme3.texture.*;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.renderer.Camera;
import com.jme3.light.*;

import input.Input;
import terrain.DigitalElevationModelData;
import terrain.TerrainManager;
import math.*;

/**
 * Contains a test to load DEM data from Seattle and render in on a TerrainQuad
 */
public class TerrainTest extends SimpleApplication{
	
	public final TerrainManager terrainManager = new TerrainManager();
	
	public static void run(){
		System.out.println("Starting Terrain Test");
		TerrainTest test = new TerrainTest();
		
		//DigitalElevationModelData.test();
		//ProjectionStereographic.test();
		//TerrainManager.test();
		
		test.start();
	}
	
	
	//for use elsewhere
	public Node getRootNode(){
		return rootNode;
	}
	
	@Override
	public void simpleInitApp(){
		//disable annoying logging
		Logger.getLogger("com.jme3").setLevel(Level.OFF);
		
		//setup input
		Input.INSTANCE.bind(inputManager);
		
		float moveSpeed = 5.0f;
		flyCam.setMoveSpeed(moveSpeed);
		
		Camera cam = getCamera();
		System.out.format("Current frustum far: %f\n", cam.getFrustumFar());
		cam.setFrustumFar(500f*1000f);
		cam.setLocation(new Vector3f(0f, 10f, 500f));
		
		makeBox();
		//makeSeattle();
		makeClaremont();
		//makeTestTerrain();
	}
	
	
	private void makeBox(){
		Box b = new Box(15, 5000, 50);
		Box b2 = new Box(50, 5000, 15);
		Geometry geom = new Geometry("Box", b);
		Geometry geom2 = new Geometry("Box2", b2);
		geom.move(0f, 5000f, 0f);
		geom2.move(-50f, 5000f, 0f);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
		//mat.setColor("Color", ColorRGBA.Green);
		geom.setMaterial(mat);
		geom2.setMaterial(mat);
		Node n = new Node();
		n.attachChild(geom);
		n.attachChild(geom2);
		rootNode.attachChild(n);
		
		
	}
	
	
	private Material getTerrainMaterial(){
		Material mat;
		
		mat = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
		grass.setWrap(WrapMode.Repeat);
		
		mat.setTexture("DiffuseMap", grass);
		//mat.setTexture("Alpha", grass);
		mat.setFloat("DiffuseMap_0_scale", 1f);
		
		
		mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
		return mat;
	}
	
	
	private void makeSeattle(){
		
		//make projection
		double centerLat = 47.604244;
		double centerLon = -122.446589;
		Projection projection = new ProjectionStereographic(centerLat, centerLon);
		
		Node terrainNode = new Node();
		
		//just make a terrain quad
		Material mat = getTerrainMaterial();

		
		int patchDimensions = 2*512;
		int jme3num = 64;
		float patchWidth = 50*1000f; //width in meters
		float scaleFactor = patchWidth/patchDimensions;
		
		System.out.format("Generating heightmap for %d points", (patchDimensions+1) * (patchDimensions+1));
		
		//arrays
		float[] elevArray = new float[(patchDimensions+1) * (patchDimensions+1)];
		
		long startTime = System.currentTimeMillis();
		for(int i = 0; i <= patchDimensions; i++){
			for(int j = 0; j <= patchDimensions; j++){
				int index = i*(patchDimensions+1) + j;
				
				//first, get x and z values from j and i
				float xval = scaleFactor * (j - (0.5f * patchDimensions));
				float zval = scaleFactor * (i - (0.5f * patchDimensions));
				
				//convert to lat/lon
				//Note: x and z must be switched due to JME3's orientation
				CoordinateLatLon coordinate = projection.transformInverse(zval, xval);
				double lat = coordinate.lat;
				double lon = coordinate.lon;
				
				//finally, get elevation data
				elevArray[index] = terrainManager.getElevation(lat, lon);
				//System.out.format("%f %f : %f\n", lat, lon, elevArray[index]);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.format("Done.  Time taken: %f seconds\n", (endTime - startTime)/1000.0);
		
		TerrainQuad terrain = new TerrainQuad("sea", jme3num+1, patchDimensions + 1, elevArray);
		terrain.setMaterial(mat);
		
		terrain.setLocalScale(scaleFactor, 1f, scaleFactor);
		terrainNode.attachChild(terrain);
		
		//add a light
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(0.5f, -0.5f, -0.5f).normalizeLocal());
		terrainNode.addLight(sun);
		
		rootNode.attachChild(terrainNode);
	}
	
	
	private void makeClaremont(){
		
		//make projection
		double centerLat = 34.110524;
		double centerLon = -117.728189;
		Projection projection = new ProjectionStereographic(centerLat, centerLon);
		
		Node terrainNode = new Node();
		
		//just make a terrain quad
		Material mat = getTerrainMaterial();

		
		int patchDimensions = 2*512;
		int jme3num = 64;
		float patchWidth = 100*1000f; //width in meters
		float scaleFactor = patchWidth/patchDimensions;
		
		System.out.format("Generating heightmap for %d points", (patchDimensions+1) * (patchDimensions+1));
		
		//arrays
		float[] elevArray = new float[(patchDimensions+1) * (patchDimensions+1)];
		
		long startTime = System.currentTimeMillis();
		for(int i = 0; i <= patchDimensions; i++){
			for(int j = 0; j <= patchDimensions; j++){
				int index = i*(patchDimensions+1) + j;
				
				//first, get x and z values from j and i
				float xval = scaleFactor * (j - (0.5f * patchDimensions));
				float zval = scaleFactor * (i - (0.5f * patchDimensions));
				
				//convert to lat/lon
				//Note: x and z must be switched due to JME3's orientation
				CoordinateLatLon coordinate = projection.transformInverse(zval, xval);
				double lat = coordinate.lat;
				double lon = coordinate.lon;
				
				//finally, get elevation data
				elevArray[index] = terrainManager.getElevation(lat, lon);
				//System.out.format("%f %f : %f\n", lat, lon, elevArray[index]);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.format("Done.  Time taken: %f seconds\n", (endTime - startTime)/1000.0);
		
		TerrainQuad terrain = new TerrainQuad("sea", jme3num+1, patchDimensions + 1, elevArray);
		terrain.setMaterial(mat);
		
		terrain.setLocalScale(scaleFactor, 1f, scaleFactor);
		terrainNode.attachChild(terrain);
		
		//add a light
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(0.5f, -0.5f, -0.5f).normalizeLocal());
		terrainNode.addLight(sun);
		
		rootNode.attachChild(terrainNode);
	}
	
	
	private void makeTestTerrain(){
		// adapted from jme3 tutorial
		Material mat_terrain;
		TerrainQuad terrain;
		
		mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
		
		mat_terrain.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
		
		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex1", grass);
		mat_terrain.setFloat("Tex1Scale", 64f);
		
		Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex2", dirt);
		mat_terrain.setFloat("Tex2Scale", 32f);
		
		Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex3", rock);
		mat_terrain.setFloat("Tex3Scale", 128f);
		
		
		AbstractHeightMap heightmap = null;
		Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
		heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
		heightmap.load();
		
		
		int patchSize = 65;
		terrain = new TerrainQuad("tutorial terrain", patchSize, 513, heightmap.getHeightMap());
		
		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, -100, 0);
		terrain.setLocalScale(2f, 1f, 2f);
		rootNode.attachChild(terrain);
		
		TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
		//terrain.addControl(control);
		
	}
	
	
	@Override
	public void simpleUpdate(float timePerFrame){
		//Update input
		Input.INSTANCE.update();
		
		//camera
		updateCamera(timePerFrame);
	}
	
	
	private void updateCamera(float timePerFrame){
		
		boolean printMoveSpeed = false;
		if(Input.INSTANCE.pressed("Add")||Input.INSTANCE.trigger("Wheel Up")){
			float moveSpeed = flyCam.getMoveSpeed();
			moveSpeed *= 1.1;
			flyCam.setMoveSpeed(moveSpeed);
			printMoveSpeed = true;
		}
		if(Input.INSTANCE.pressed("Subtract")||Input.INSTANCE.trigger("Wheel Down")){
			float moveSpeed = flyCam.getMoveSpeed();
			moveSpeed /= 1.1;
			if(moveSpeed < 0){
				moveSpeed = 0;
			}
			flyCam.setMoveSpeed(moveSpeed);
			printMoveSpeed = true;
		}
		
		if(printMoveSpeed && false){
			String out = "Camera moveSpeed = %f%n";
			System.out.format(out, flyCam.getMoveSpeed());
		}
	}
	
	
	
}
