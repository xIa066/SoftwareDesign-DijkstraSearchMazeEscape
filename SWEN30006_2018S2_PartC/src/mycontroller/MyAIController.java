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
	public MyAIController(Car car) {
		super(car);
		wholeMap = getMap();
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
			}
		}
		System.out.println(weightMap);
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
		for(Coordinate coordinate : currentView.keySet()) {
			MapTile tile =  currentView.get(coordinate);
			if(tile.isType(MapTile.Type.EMPTY)) {
				weightMap.put(coordinate, Integer.MIN_VALUE);
			}else if(tile.isType(MapTile.Type.TRAP)) {
				//System.out.println(((TrapTile) tile).getTrap()+"    "+coordinate);
				if(((TrapTile) tile).getTrap().equals("lava")){
					if(((LavaTrap) tile).getKey() != 0 && wholeMap.get(coordinate).isType(MapTile.Type.ROAD) && !travelMap.get(coordinate)) {
						weightMap.put(coordinate, 10000);
						travelMap.put(coordinate, true);
						wholeMap.put(coordinate, (LavaTrap)tile);
					}else if(((LavaTrap) tile).getKey() == 0 && wholeMap.get(coordinate).isType(MapTile.Type.ROAD) && !travelMap.get(coordinate)) {
						weightMap.put(coordinate, 50);
						travelMap.put(coordinate, true);
						wholeMap.put(coordinate, (LavaTrap)tile);
					}
				}else if(((TrapTile) tile).getTrap().equals("mud") && wholeMap.get(coordinate).isType(MapTile.Type.ROAD) && !travelMap.get(coordinate)){
					weightMap.put(coordinate, Integer.MIN_VALUE);
					travelMap.put(coordinate, true);
					wholeMap.put(coordinate, (MudTrap)tile);
					System.out.println("mud   " + coordinate);
				}else if(((TrapTile) tile).getTrap().equals("health")){//current health to be added
					weightMap.put(coordinate, 100);
					travelMap.put(coordinate, true);
					wholeMap.put(coordinate, (HealthTrap)tile);
				}else if(((TrapTile) tile).getTrap().equals("grass") && wholeMap.get(coordinate).isType(MapTile.Type.ROAD) && !travelMap.get(coordinate)){
					weightMap.put(coordinate, 100);
					travelMap.put(coordinate, true);
					wholeMap.put(coordinate, (GrassTrap)tile);
				}
			}
		}
		ArrayList<Integer> arrayList = new ArrayList<>();
		for(Coordinate coordinate : currentView.keySet()) {
			arrayList.add(weightMap.get(coordinate));
			System.out.println(weightMap.get(coordinate)+"    "+coordinate+"\n");
		}
		Collections.sort(arrayList, Collections.reverseOrder());
//		System.out.println(arrayList);
		int i = 0;
		for(Coordinate coordinate : currentView.keySet()) {
			if(weightMap.get(coordinate) == arrayList.get(i)) {
//				System.out.println(coordinate);
				//find a route
				//routeSelection(getPosition(), coordinate, wholeMap); -> -1   (x,y)
				mycontroller.DijkstraMinimalPath.DijkstraPathFinder dijkstraPathFinder = new mycontroller.DijkstraMinimalPath.DijkstraPathFinder();
				List<Coordinate> coordinates = dijkstraPathFinder.planRoute(new Coordinate(getPosition()), coordinate, wholeMap);
				System.out.println(getPosition());
				
				Direction orientation = getOrientation();
				if (coordinates.size() <= 1) {
					continue;
				}
				System.out.println(coordinates);
				nextCoordinate = coordinates.get(1);
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
		//System.out.println(weightMap);
		Set<Integer> keys = Simulation.getKeys();
		Simulation.resetKeys();
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
