/*
 * Contains a basic input interface for use with JME3.
 * To use:
 * 1) bind the input manager by calling Input.INSTANCE.bind(inputManager)
 * 2) call Input.INSTANCE.update() every frame
 * 3) use Input.INSTANCE.trigger("w"), etc. as needed.
 */
package input;

import java.util.ArrayList;

import com.jme3.math.*;
import com.jme3.input.*;
import com.jme3.input.controls.*;

//input class
public class Input {
	
	public static Input INSTANCE = new Input();
	
	
	private InputManager inputManager;
	private String[] mappings;
	
	private ArrayList<String> pressed_keys;
	private ArrayList<String> triggered_keys;
	private ArrayList<String> release_keys;
	private ArrayList<String> double_triggered_keys;
	private ArrayList<String> new_pressed_keys;
	private ArrayList<String> new_release_keys;
	private ArrayList<String> recent_triggered_keys;
	private ArrayList<Integer> recent_trigger_times;
	private int mousex;
	private int mousey;
	
	public Input(){
		pressed_keys=new ArrayList<String>();
		triggered_keys=new ArrayList<String>();
		release_keys=new ArrayList<String>();
		double_triggered_keys=new ArrayList<String>();
		new_pressed_keys=new ArrayList<String>();
		new_release_keys=new ArrayList<String>();
		recent_triggered_keys=new ArrayList<String>();
		recent_trigger_times=new ArrayList<Integer>();
	}
	
	
	public void bind(InputManager im){
		inputManager = im;
		setup();
	}
	
	
	private void setup(){
    	//inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
    	inputManager.addMapping("Mouse Right",new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    	inputManager.addMapping("Mouse Left",new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    	inputManager.addMapping("Wheel Up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
    	inputManager.addMapping("Wheel Down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
    	
    	inputManager.addMapping("Space",new KeyTrigger(KeyInput.KEY_SPACE));
    	inputManager.addMapping("Enter",new KeyTrigger(KeyInput.KEY_RETURN));
    	inputManager.addMapping("Shift",new KeyTrigger(KeyInput.KEY_LSHIFT));
    	inputManager.addMapping("Control",new KeyTrigger(KeyInput.KEY_LCONTROL));
    	inputManager.addMapping("Delete",new KeyTrigger(KeyInput.KEY_DELETE));
    	inputManager.addMapping("Add",new KeyTrigger(KeyInput.KEY_ADD));
    	inputManager.addMapping("Subtract",new KeyTrigger(KeyInput.KEY_SUBTRACT));
    	inputManager.addMapping("Up",new KeyTrigger(KeyInput.KEY_UP));
    	inputManager.addMapping("Down",new KeyTrigger(KeyInput.KEY_DOWN));
    	inputManager.addMapping("Left",new KeyTrigger(KeyInput.KEY_LEFT));
    	inputManager.addMapping("Right",new KeyTrigger(KeyInput.KEY_RIGHT));
    	inputManager.addMapping("Comma",new KeyTrigger(KeyInput.KEY_COMMA));
    	inputManager.addMapping("Period",new KeyTrigger(KeyInput.KEY_PERIOD));
    	inputManager.addMapping("W",new KeyTrigger(KeyInput.KEY_W));
    	inputManager.addMapping("A",new KeyTrigger(KeyInput.KEY_A));
    	inputManager.addMapping("S",new KeyTrigger(KeyInput.KEY_S));
    	inputManager.addMapping("D",new KeyTrigger(KeyInput.KEY_D));
    	inputManager.addMapping("M",new KeyTrigger(KeyInput.KEY_M));
    	inputManager.addMapping("N",new KeyTrigger(KeyInput.KEY_N));
    	inputManager.addMapping("P",new KeyTrigger(KeyInput.KEY_P));
    	inputManager.addMapping("L",new KeyTrigger(KeyInput.KEY_L));
    	inputManager.addMapping("F",new KeyTrigger(KeyInput.KEY_F));
    	inputManager.addMapping("E",new KeyTrigger(KeyInput.KEY_E));
    	inputManager.addMapping("G",new KeyTrigger(KeyInput.KEY_G));
    	inputManager.addMapping("Q",new KeyTrigger(KeyInput.KEY_Q));
    	inputManager.addMapping("J",new KeyTrigger(KeyInput.KEY_J));
    	inputManager.addMapping("R",new KeyTrigger(KeyInput.KEY_R));
    	inputManager.addMapping("C",new KeyTrigger(KeyInput.KEY_C));
    	inputManager.addMapping("V",new KeyTrigger(KeyInput.KEY_V));
    	mappings=new String[]{"Mouse Left","Mouse Right","Wheel Up","Wheel Down","Space","Enter","Shift","Control","Delete","Add","Subtract","Up","Down","Left","Right","Comma","Period","W","A","S","D","M","N","P","L","F","E","G","Q","J","R","C","V"};
    	inputManager.addListener(actionListener, mappings);
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
        	
        	for(String map:mappings){
        		if((name==map)&&keyPressed){
        			Input.INSTANCE.add_press(map);
        			return;
        		}
        		if((name==map)&&!keyPressed){
        			Input.INSTANCE.add_release(map);
        			return;
        		}
        	}
        }
     };
     
     
     public void update(){
     	updateMouse();
     	updateKeys();
     }
     
     private void updateMouse(){
    	 Vector2f click2d=inputManager.getCursorPosition();
    	 set_mouse(Math.round(click2d.x),Math.round(click2d.y));
    	 //old version:
    	 //set_mouse(Math.round(click2d.x),screen_resolution_y-Math.round(click2d.y));
     }
	
	 private void updateKeys(){
		
		//clear old key codes
		release_keys.clear();
		triggered_keys.clear();
		double_triggered_keys.clear();
		//replace old key codes with new ones
		release_keys.addAll(new_release_keys);
		//handle triggered keys
		for(String newkey:new_pressed_keys){
			triggered_keys.add(newkey);
			//check for double click
			if(recent_triggered_keys.contains(newkey)){
				double_triggered_keys.add(newkey);
			}else{
				recent_triggered_keys.add(newkey);
				recent_trigger_times.add(0);
			}
		}
		//update recent triggered list
		for(int i=recent_triggered_keys.size()-1;i>=0;i--){
			recent_trigger_times.set(i,recent_trigger_times.get(i)+1);
			if(recent_trigger_times.get(i)>32){
				recent_triggered_keys.remove(i);
				recent_trigger_times.remove(i);
			}
		}
		//changes list of pressed keys
		pressed_keys.addAll(new_pressed_keys);
		for(String rkey:release_keys){
			pressed_keys.remove((Object)rkey);
		}
		//clear new key arrays
		new_pressed_keys.clear();
		new_release_keys.clear();
	}
	
	//interface methods
	
	public boolean trigger(String keycode){
		return triggered_keys.contains(keycode);
	}
	
	public boolean doubleTrigger(String keycode){
		return double_triggered_keys.contains(keycode);
	}
	
	public boolean pressed(String keycode){
		return pressed_keys.contains(keycode);
	}
	
	public boolean released(String keycode){
		return release_keys.contains(keycode);
	}
	
	public int mouseX(){
		return mousex;
	}
	
	public int mouseY(){
		return mousey;
	}
	
	//state change methods; only used for interfacing with JMonkey
	
	private void add_press(String keycode){
		new_pressed_keys.add(keycode);
	}
	
	private void add_release(String keycode){
		new_release_keys.add(keycode);
	}
	
	private void set_mouse(int mx,int my){
		mousex=mx;
		mousey=my;
	}
	
	

}
