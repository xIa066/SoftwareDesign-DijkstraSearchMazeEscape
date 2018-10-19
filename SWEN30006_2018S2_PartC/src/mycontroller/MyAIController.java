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
	Coordinate nextCoordinate;
	private HashMap<Coordinate, MapTile> wholeMap;
	private HashMap<Coordinate, Integer> weightMap;
	private HashMap<Coordinate, Boolean> travelMap;
	private Boolean healthFlag = false;
	private Boolean getAllKeys = false;
	private Coordinate finishPoint;
	
	private Car car;
	private MoveCar movement;
	
	public MyAIController(Car car) {
		super(car);
		this.car = car;
		initializeMap();
	}
	
	private void initializeMap() {
		movement = new MoveCar(this.car);
		wholeMap = getMap();
		weightMap = new HashMap<Coordinate,Integer>();
		travelMap = new HashMap<Coordinate,Boolean>();
		for(Coordinate coordinate : wholeMap.keySet()) {
			travelMap.put(coordinate, false);
		}
		// Initialize the whole map
		for(Coordinate coordinate : wholeMap.keySet()) {
			if(wholeMap.get(coordinate).isType(MapTile.Type.ROAD)) {
				weightMap.put(coordinate, 100);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.START)) {
				weightMap.put(coordinate, 100);
				travelMap.put(coordinate, true);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.WALL)) {
				weightMap.put(coordinate, Integer.MIN_VALUE);
				travelMap.put(coordinate, true);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.FINISH)) {
				weightMap.put(coordinate,Integer.MIN_VALUE);//need to be modified
				travelMap.put(coordinate, true);
				finishPoint = coordinate;
			}
		}
	}
	
	private void eastTurn(Direction direction) {
		switch (direction) {
		case EAST:
			applyForwardAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case WEST:
			applyReverseAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case NORTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		case SOUTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		}
	}
	private void westTurn(Direction direction) {
		switch (direction) {
		case WEST:
			applyForwardAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case EAST:
			applyReverseAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case SOUTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		case NORTH:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		}
	}
	private void northTurn(Direction direction) {
		switch (direction) {
		case NORTH:
			applyForwardAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case SOUTH:
			applyReverseAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case WEST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		case EAST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		}
	}
	private void southTurn(Direction direction) {
		switch (direction) {
		case SOUTH:
			applyForwardAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case NORTH:
			applyReverseAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case EAST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnLeft();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		case WEST:
			if (getSpeed() == CAR_MAX_SPEED) {
				turnRight();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(getKeys().size() == numKeys()) {
			getAllKeys = true;
			for(Coordinate coordinate : wholeMap.keySet()) {
				if(wholeMap.get(coordinate).isType(MapTile.Type.FINISH)) {
					// why ?? put the max weight
					weightMap.put(coordinate,Integer.MAX_VALUE);
				}
			}
		}
		
		if (weightMap.get(new Coordinate(getPosition())) > 1000) {
			weightMap.put(new Coordinate(getPosition()), 30);
		}
		
		HashMap<Coordinate, MapTile> currentView = getView();
		
		if (getHealth() <= 50) {
			healthFlag = true;
		}else if (getHealth() == 100 && healthFlag) {
			healthFlag = false;
		}
		
		if (currentView.get(new Coordinate(getPosition())) instanceof HealthTrap) {
			if (healthFlag) {
				applyBrake();
				return;
			}
		}
		new ExploreMap(currentView, wholeMap, weightMap, travelMap);
		ArrayList<Integer> arrayList = new ArrayList<>();
		for(Coordinate coordinate : currentView.keySet()) {
			arrayList.add(weightMap.get(coordinate));
//			System.out.println(weightMap.get(coordinate)+"    "+coordinate+"\n");
		}
		Collections.sort(arrayList, Collections.reverseOrder());
//		System.out.println(arrayList);
		int i = 0;
		for(Coordinate coordinate : currentView.keySet()) {
			if(weightMap.get(coordinate) == arrayList.get(i)) {
//				//find a route
//				//routeSelection(getPosition(), coordinate, wholeMap); -> -1   (x,y)
				DijkstraRouteSelector dijkstraRouteSelector 
				= new DijkstraRouteSelector();
				List<Coordinate> coordinates;
				if (getAllKeys) {
					coordinates = dijkstraRouteSelector.routeSelect(new Coordinate(getPosition()), finishPoint, wholeMap);
				}else {
					coordinates = dijkstraRouteSelector.routeSelect(new Coordinate(getPosition()), coordinate, wholeMap);
				}
//				System.out.println(getPosition());
				
				Direction orientation = getOrientation();
				if (coordinates.size() <= 1) {
					i++;
					continue;
				}
				nextCoordinate = coordinates.get(1);
//				 the weight of destination - 1
				weightMap.put(coordinates.get(coordinates.size()-1),weightMap.get(coordinates.get(coordinates.size()-1))-1);
				
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
				if(!coordinates.isEmpty()) {
					switch (orientation) {
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
				
				//movement.carMove(direction);
				//weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
				
				break;
			}
			
		}
	}
}


