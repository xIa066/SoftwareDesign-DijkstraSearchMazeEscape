package controller;

import java.util.HashMap;
import java.util.Set;
import com.badlogic.gdx.Input;
import world.Car;
import swen30006.driving.Simulation;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

// Manual Controls for the car
public class ManualController extends CarController {
	
	public ManualController(Car car){
		super(car);
	}
	
	public void update(){
		Set<Integer> keys = Simulation.getKeys();
		Simulation.resetKeys();
		// System.out.print("Get Keys: ");
        // System.out.println(keys);
		HashMap<Coordinate, MapTile> currentView = getView();
		//System.out.println(currentView);
		MapTile tile =  currentView.get(new Coordinate(getPosition()));
		if(tile.isType(MapTile.Type.TRAP)) {
			System.out.println(((TrapTile) tile).getTrap());
		}
		//System.out.println(tile.getTrap());
		System.out.println(numKeys());
		System.out.println(getHealth());
		System.out.println(getPosition());
		System.out.println(getKeys());
		System.out.println(getMap());
		System.out.println(mapHeight());
		System.out.println(mapWidth());
        for (int k : keys){
		     switch (k){
		        case Input.Keys.B:
		        	applyBrake();
		            break;
		        case Input.Keys.UP:
		        	applyForwardAcceleration();
		            break;
		        case Input.Keys.DOWN:
		        	applyReverseAcceleration();
		        	break;
		        case Input.Keys.LEFT:
		        	turnLeft();
		        	break;
		        case Input.Keys.RIGHT:
		        	turnRight();
		        	break;
		        default:
		      }
		  }
	}
}
