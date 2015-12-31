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

import input.Input;
import terrain.DigitalElevationModelData;
import terrain.TerrainManager;
import math.ProjectionStereographic;
import math.ArrayUtils;

/*
 * Contains a test to load DEM data from Seattle and render in on a TerrainQuad
 */
public class TerrainTest extends SimpleApplication{
	
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
		
		makeBox();
		makeTestTerrain();
	}
	
	
	private void makeBox(){
		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
		//mat.setColor("Color", ColorRGBA.Green);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
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
		
		if(printMoveSpeed){
			String out = "Camera moveSpeed = %f%n";
			System.out.format(out, flyCam.getMoveSpeed());
		}
	}
	
	
	
}
