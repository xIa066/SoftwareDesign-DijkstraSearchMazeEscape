/*
 * Group 20
 */

package mycontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Orientation;

import controller.CarController;
import swen30006.driving.Simulation;
import tiles.GrassTrap;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial.Direction;

public class MyAIController extends CarController{

	private static final float CAR_MAX_SPEED = 1;
	private static final int FULLHEALTH = 100;
	private static final int WEIGHT_ROAD = 100;
	private static final int ROAD_HAVE_BEEN = 20;
	
	Coordinate nextCoordinate;
	private HashMap<Coordinate, MapTile> wholeMap;
	private HashMap<Coordinate, Integer> weightMap;
	private HashMap<Coordinate, Boolean> travelMap;
	private Boolean healthFlag = false;
	private Boolean getAllKeys = false;
	private Coordinate finishPoint;

	
	public MyAIController(Car car) {
		super(car);
		initializeMap();
	}
	
	private void initializeMap() {
		wholeMap = getMap();
		weightMap = new HashMap<Coordinate,Integer>();
		travelMap = new HashMap<Coordinate,Boolean>();
		for(Coordinate coordinate : wholeMap.keySet()) {
			travelMap.put(coordinate, false);
		}
		// Initialize the whole map
		for(Coordinate coordinate : wholeMap.keySet()) {
			if(wholeMap.get(coordinate).isType(MapTile.Type.ROAD)) {
				weightMap.put(coordinate, WEIGHT_ROAD);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.START)) {
				weightMap.put(coordinate, WEIGHT_ROAD);
				travelMap.put(coordinate, true);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.WALL)) {
				weightMap.put(coordinate, Integer.MIN_VALUE);
				travelMap.put(coordinate, true);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.FINISH)) {
				weightMap.put(coordinate,Integer.MIN_VALUE);
				travelMap.put(coordinate, true);
				finishPoint = coordinate;
			}
		}
	}
	
	// the movement of the car when the orientation is east
	private void eastTurn(Direction direction) {
		switch (direction) {
		case EAST:
			applyForwardAcceleration();
			break;
		case WEST:
			applyReverseAcceleration();
			break;
		case NORTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
			}
			break;
		case SOUTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
			}
			break;
		}
	}
	
	// the movement of the car when the orientation is west
	private void westTurn(Direction direction) {
		switch (direction) {
		case WEST:
			applyForwardAcceleration();
			break;
		case EAST:
			applyReverseAcceleration();
			break;
		case SOUTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
			}
			break;
		case NORTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
			}
			break;
		}
	}
	
	// the movement of the car when the orientation is north
	private void northTurn(Direction direction) {
		switch (direction) {
		case NORTH:
			applyForwardAcceleration();
			break;
		case SOUTH:
			applyReverseAcceleration();
			break;
		case WEST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
			}
			break;
		case EAST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
			}
			break;
		}
	}
	
	// the movement of the car when the orientation is south
	private void southTurn(Direction direction) {
		switch (direction) {
		case SOUTH:
			applyForwardAcceleration();
			break;
		case NORTH:
			applyReverseAcceleration();
			break;
		case EAST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
			}
			break;
		case WEST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
			}
			break;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		HashMap<Coordinate, MapTile> currentView = getView();
		
		// when the car gets all keys, update the value of the finish
		if(getKeys().size() == numKeys()) {
			getAllKeys = true;
			for(Coordinate coordinate : wholeMap.keySet()) {
				if(wholeMap.get(coordinate).isType(MapTile.Type.FINISH)) {
					weightMap.put(coordinate,Integer.MAX_VALUE);
				}
			}
		}
		
		// when the car get a key, change this key trap to a lava trap
		if (weightMap.get(new Coordinate(getPosition())) > WEIGHT_ROAD) {
			weightMap.put(new Coordinate(getPosition()), ExploreMap.LAVA_WEIGHT);
		}
		
		// when the car has less than 50% health and it stand at a health trap
		// it will move until it gets full health
		if (getHealth() <= FULLHEALTH/2) {
			healthFlag = true;
		}else if (getHealth() == FULLHEALTH && healthFlag) {
			healthFlag = false;
		}
		if (currentView.get(new Coordinate(getPosition())) instanceof HealthTrap) {
			if (healthFlag) {
				applyBrake();
				return;
			}
		}
		
		// update the map by current view
		ExploreMap explorer = new ExploreMap(currentView, wholeMap, weightMap, travelMap);
		explorer.exploring();
		
		// get the list of weight in descending order
		ArrayList<Integer> weightList = new ArrayList<>();
		for(Coordinate coordinate : currentView.keySet()) {
			weightList.add(weightMap.get(coordinate));
		}
		Collections.sort(weightList, Collections.reverseOrder());
		
		int nthBiggestWeight = 0;
		for(Coordinate coordinate : currentView.keySet()) {
			if(weightMap.get(coordinate) == weightList.get(nthBiggestWeight)) {
				
				DijkstraRouteSelector dijkstraRouteSelector = new DijkstraRouteSelector();
				List<Coordinate> coordinates;
				if (getAllKeys) {
					// if the car gets all keys, it will go to finish straight away
					coordinates = dijkstraRouteSelector.routeSelect(new Coordinate(getPosition()), finishPoint, wholeMap);
				}else {
					coordinates = dijkstraRouteSelector.routeSelect(new Coordinate(getPosition()), coordinate, wholeMap);
				}
				
				// if this destination can't be move to, check the next point of biggest weight
				if (coordinates.size() <= 1) {
					nthBiggestWeight++;
					continue;
				}
				
				nextCoordinate = coordinates.get(1);
				weightMap.put(coordinates.get(coordinates.size()-1),weightMap.get(coordinates.get(coordinates.size()-1))-1);
				
				// get the direction from this point to next point
				int x = nextCoordinate.x-new Coordinate(getPosition()).x;
				int y = nextCoordinate.y-new Coordinate(getPosition()).y;
				Direction direction = Direction.EAST;
				if(x == 1 && y == 0) {
					direction = Direction.EAST;
				}else if(x == -1 && y == 0) {
					direction = Direction.WEST;
				}else if(x == 0 && y == 1) {
					direction = Direction.NORTH;
				}else if(x == 0 && y == -1) {
					direction = Direction.SOUTH;
				}
				
				// Move car
				if(!coordinates.isEmpty()) {
					switch (getOrientation()) {
					case EAST:
						eastTurn(direction);
						break;
					case WEST:
						westTurn(direction);
						break;
					case NORTH:
						northTurn(direction);
						break;
					case SOUTH:
						southTurn(direction);
						break;
					}
				}
				
				// reduce the weight of next coordinate
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-ROAD_HAVE_BEEN);
				
				break;
			}
			
		}
	}
}


