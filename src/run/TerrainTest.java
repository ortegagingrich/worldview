package run;

import java.util.logging.*;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;

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
		TerrainManager.test();
		
		//test.start();
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
		
		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
		//mat.setColor("Color", ColorRGBA.Green);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
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
