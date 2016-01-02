/*
 * Some notes regarding the JME3 coordinate system:
 * x: north(+)/south(-)
 * y: up(+)/down(-)
 * z: east(+)/west(-)
 */
package run;

import java.util.logging.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

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
import com.jme3.asset.plugins.FileLocator;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.*;

import input.Input;
import io.FileUtils;
import terrain.DigitalElevationModelData;
import terrain.TerrainManager;
import math.*;

/**
 * Contains a test to load DEM data and render in on a TerrainQuad
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
		
		//Temporary: find a more permanent solution later:
		assetManager.registerLocator(FileUtils.ASSET_PATH, FileLocator.class);
		
		
		//setup input
		inputManager.deleteMapping("FLYCAM_ZoomIn");
		inputManager.deleteMapping("FLYCAM_ZoomOut");
		Input.INSTANCE.bind(inputManager);
		
		float moveSpeed = 500f;
		flyCam.setMoveSpeed(moveSpeed);
		flyCam.setZoomSpeed(0);
		
		Camera cam = getCamera();
		cam.setFrustumFar(500f*1000f);
		cam.setLocation(new Vector3f(0f, 1000f, 500f));
		cam.lookAt(new Vector3f(1e15f, 0f, 0f), new Vector3f(0f, 1f, 0f));
		
		viewPort.setBackgroundColor(new ColorRGBA(240.0f/255, 1f, 1f, 1f));
		makeBox();
		makeClaremont();
		makeSun();
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
		mat.setBoolean("useTriPlanarMapping", false);
		mat.setFloat("Shininess", 0.0f);
		
		
		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
		grass.setWrap(WrapMode.Repeat);
		
		//Texture grassNormal = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
		Texture grassNormal = assetManager.loadTexture("Textures/black.png");
		grassNormal.setWrap(WrapMode.Repeat);
		
		
		
		
		//make a really big texture
		Image image = grass.getImage();
		
		Texture alpha = assetManager.loadTexture("Textures/red.png");
		mat.setTexture("AlphaMap", alpha);
		
		
		mat.setTexture("DiffuseMap", grass);
		mat.setFloat("DiffuseMap_0_scale", 64f);
		
		mat.setTexture("NormalMap", grassNormal);
		
		
		//mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
		return mat;
	}
	
	
	
	private void makeClaremont(){
		
		//make projection
		double centerLat = 34.110524;
		double centerLon = -117.728189;
		Projection projection = new ProjectionStereographic(centerLat, centerLon);
		
		Node terrainNode = new Node();
		
		//just make a terrain quad
		Material mat = getTerrainMaterial();

		
		int patchDimensions = 4*512;
		int jme3num = 64;
		float patchWidth = 100*1000f; //width in meters
		float scaleFactor = patchWidth/patchDimensions;
		
		System.out.format("Resolution: %f meters\n", scaleFactor);
		System.out.format("Generating heightmap for %d points . . .\n", (patchDimensions+1) * (patchDimensions+1));
		
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
		
		terrain.setLocalScale(scaleFactor, 1*1f, scaleFactor);
		terrainNode.attachChild(terrain);
		
		rootNode.attachChild(terrainNode);
		
		
		//terrainNode.setShadowMode(ShadowMode.CastAndReceive);
		
	}
	
	
	
	private DirectionalLight sun;
	private double theta = 0; //sun angle
	
	private void makeSun(){
		//add a light
		sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White.mult(0.7f));
		sun.setDirection(new Vector3f(-1f, -1.0f, 0).normalizeLocal());
		rootNode.addLight(sun);
		
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.Blue.mult(2.0f));
		//rootNode.addLight(al);
		
		//shadow tests?
		/*rootNode.setShadowMode(ShadowMode.Off);
		int ms = 1048;
		DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, ms, 3);
		dlsr.setLight(sun);
		//viewPort.addProcessor(dlsr);*/
	}
	
	
	private void updateSun(){
		if(Input.INSTANCE.pressed("Space")){
			theta += 0.01;//0.1 * Math.PI;
		}
		
		//sun.setDirection(new Vector3f(-1f, (float) -Math.cos(theta), (float) Math.sin(theta)).normalizeLocal());
	}
	
	
	
	@Override
	public void simpleUpdate(float timePerFrame){
		//Update input
		Input.INSTANCE.update();
		
		//update sun
		updateSun();
		
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
