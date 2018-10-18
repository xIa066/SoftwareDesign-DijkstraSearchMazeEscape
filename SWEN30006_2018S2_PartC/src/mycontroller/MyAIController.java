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
	private Direction direction;
	private Boolean healthFlag = false;
	private Boolean getAllKeys = false;
	private Coordinate finishPoint;
	private ArrayList<Coordinate> healthTraps;

	
	private void initializeMap() {
		wholeMap = getMap();
		healthTraps = new ArrayList<>();
		weightMap = new HashMap<Coordinate,Integer>();
		travelMap = new HashMap<Coordinate,Boolean>();
		for(Coordinate coordinate : wholeMap.keySet()) {
			travelMap.put(coordinate, false);
		}
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
	

	private Coordinate getClosestHealthTraps(ArrayList<Coordinate> healthTraps) {
		if (healthTraps.size() == 1) {
			return healthTraps.get(0);
		}else if(healthTraps.size() > 0) {
			Coordinate cloestPoint = healthTraps.get(0);
			int min = Integer.MAX_VALUE;
			Coordinate current = new Coordinate(getPosition());
			for (Coordinate coordinate:healthTraps) {
				int deltaX = current.x - coordinate.x;
				int deltaY = current.y - coordinate.y;
				int distance = deltaX * deltaX + deltaY * deltaY;
				if (min > distance) {
					cloestPoint = coordinate;
				}
			}
			return cloestPoint;
		}
		return null;
	}
	
	public MyAIController(Car car) {
		super(car);
		initializeMap();
	}
	
	private void eastTurn() {
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
	private void westTurn() {
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
	private void northTurn() {
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
	private void southTurn() {
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
		
		for(Coordinate coordinate : currentView.keySet()) {
			MapTile tile =  currentView.get(coordinate);
			if(tile.isType(MapTile.Type.EMPTY)) {
				weightMap.put(coordinate, Integer.MIN_VALUE);
			}else if(tile.isType(MapTile.Type.TRAP) && !travelMap.get(coordinate)) {
				if(((TrapTile) tile).getTrap().equals("lava")){
					if(((LavaTrap) tile).getKey() != 0) {
						weightMap.put(coordinate, 10000);
						travelMap.put(coordinate, true);
						wholeMap.put(coordinate, (LavaTrap)tile);
					}else if(((LavaTrap) tile).getKey() == 0) {
						weightMap.put(coordinate, 50);
						travelMap.put(coordinate, true);
						wholeMap.put(coordinate, (LavaTrap)tile);
					}
					if (getKeys().contains(((LavaTrap) tile).getKey())) {
						weightMap.put(coordinate, 50);
					}
				}else if(((TrapTile) tile).getTrap().equals("mud")){
					weightMap.put(coordinate, Integer.MIN_VALUE);
					travelMap.put(coordinate, true);
					wholeMap.put(coordinate, (MudTrap)tile);
				}else if(((TrapTile) tile).getTrap().equals("health")){//current health to be added
					weightMap.put(coordinate, 100);
					travelMap.put(coordinate, true);
					wholeMap.put(coordinate, (HealthTrap)tile);
					healthTraps.add(coordinate);
				}else if(((TrapTile) tile).getTrap().equals("grass")){
					weightMap.put(coordinate, 100);
					travelMap.put(coordinate, true);
					wholeMap.put(coordinate, (GrassTrap)tile);
				}
			}
		}
		ArrayList<Integer> arrayList = new ArrayList<>();
		for(Coordinate coordinate : currentView.keySet()) {
			arrayList.add(weightMap.get(coordinate));
		}
		Collections.sort(arrayList, Collections.reverseOrder());
//		System.out.println(arrayList);
		int i = 0;
		for(Coordinate coordinate : currentView.keySet()) {
			if(weightMap.get(coordinate) == arrayList.get(i)) {
				//find a route
				//routeSelection(getPosition(), coordinate, wholeMap); -> -1   (x,y)
				DijkstraRouteSelection dijkstraRouteSelection = new DijkstraRouteSelection();
				List<Coordinate> coordinates;
				if (getAllKeys) {
					coordinates = dijkstraRouteSelection.routeSelect(new Coordinate(getPosition()), finishPoint, wholeMap);
				}else if (healthFlag & healthTraps.size() > 0) { 
					coordinates = dijkstraRouteSelection.routeSelect(new Coordinate(getPosition()), getClosestHealthTraps(healthTraps), wholeMap);
				}else{
					coordinates = dijkstraRouteSelection.routeSelect(new Coordinate(getPosition()), coordinate, wholeMap);
				}
				Direction orientation = getOrientation();
				if (coordinates.size() <= 1) {
					i++;
					continue;
				}
				nextCoordinate = coordinates.get(1);
				// the weight of destination - 1
				weightMap.put(coordinates.get(coordinates.size()-1),weightMap.get(coordinates.get(coordinates.size()-1))-1);
				
				int x = nextCoordinate.x-new Coordinate(getPosition()).x;
				int y = nextCoordinate.y-new Coordinate(getPosition()).y;
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
						eastTurn();
						break;
					case WEST:
						westTurn();
						break;
					case NORTH:
						northTurn();
						break;
					case SOUTH:
						southTurn();
						break;
					}
				}
				
				break;
			}
			
		}
	}

}
