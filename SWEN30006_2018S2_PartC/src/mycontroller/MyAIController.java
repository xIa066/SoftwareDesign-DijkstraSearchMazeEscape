package mycontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Orientation;

import controller.CarController;
import mycontroller.DijkstraMinimalPath.Node;
import swen30006.driving.Simulation;
import tiles.GrassTrap;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
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
	
	public MyAIController(Car car) {
		super(car);
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
//		System.out.println(weightMap);
	}
	
	private void eastTurn(Direction direction) {
		switch (direction) {
		case EAST:
			applyForwardAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case WEST:
			applyBrake();
			applyReverseAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case NORTH:
			if (getSpeed()  ==CAR_MAX_SPEED) {
				turnLeft();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		case SOUTH:
			if (getSpeed() ==CAR_MAX_SPEED) {
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
			applyBrake();
			applyReverseAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case SOUTH:
			if (getSpeed() ==CAR_MAX_SPEED) {
				turnLeft();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		case NORTH:
			if (getSpeed() ==CAR_MAX_SPEED) {
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
			applyBrake();
			applyReverseAcceleration();
			weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			break;
		case WEST:
			if (getSpeed() ==CAR_MAX_SPEED) {
				turnLeft();
				weightMap.put(nextCoordinate, weightMap.get(nextCoordinate)-20);
			}
			break;
		case EAST:
//			System.out.println(getSpeed());
//			System.out.println(CAR_MAX_SPEED);
			if (getSpeed()  ==CAR_MAX_SPEED) {
				System.out.println("hhhhhhh");
				turnRight();
				System.out.println("didnt turn Right");
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
			applyBrake();
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
			// if all keys are found
			getAllKeys = true;
			for(Coordinate coordinate : wholeMap.keySet()) {
				if(wholeMap.get(coordinate).isType(MapTile.Type.FINISH)) {
					// we need to find the shortest distance to FINISH point
					weightMap.put(coordinate,Integer.MAX_VALUE);
				}
			}
		}
		// if the weight of current Position is greater than 1k, put 30 down
		if (weightMap.get(new Coordinate(getPosition())) > 1000) {
			weightMap.put(new Coordinate(getPosition()), 30);
		}
		
		HashMap<Coordinate, MapTile> currentView = getView();
		
		if (getHealth() <= 50) {
			healthFlag = true;
		}else if (getHealth() == 100 && healthFlag) {
			healthFlag = false;
		}
		// if the currentView has currentPos that is an instance of Health Trap, here has a return
		if (currentView.get(new Coordinate(getPosition())) instanceof HealthTrap) {
			if (healthFlag) {
				applyBrake();
				return;
			}
		}
		// loop in current View, update three hashMap : Travel, Weight and wholeMap
		for(Coordinate coordinate : currentView.keySet()) {
			MapTile tile =  currentView.get(coordinate);
			if(tile.isType(MapTile.Type.EMPTY)) {
				// put the weight of EMPTY tile as Min
				weightMap.put(coordinate, Integer.MIN_VALUE);
			}else if(tile.isType(MapTile.Type.TRAP)) {
				
				if(((TrapTile) tile).getTrap().equals("lava")){
					// if we have a key in lava, and the lava is undiscovered and the weight is not defined
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
//					System.out.println("mud   " + coordinate);
				}else if(((TrapTile) tile).getTrap().equals("health") && !travelMap.get(coordinate)){//current health to be added
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
		//
		ArrayList<Integer> arrayList = new ArrayList<>();
		for(Coordinate coordinate : currentView.keySet()) {
			arrayList.add(weightMap.get(coordinate));
//			System.out.println(weightMap.get(coordinate)+"    "+coordinate+"\n");
		}
		Collections.sort(arrayList, Collections.reverseOrder());
//		System.out.println(arrayList);
		// index for the biggest weigh in currentView
		int i = 0;
		mycontroller.DijkstraMinimalPath.DijkstraPathFinder dijkstraPathFinder 
		= new mycontroller.DijkstraMinimalPath.DijkstraPathFinder();
		for(Coordinate coordinate : currentView.keySet()) {
			if(weightMap.get(coordinate) == arrayList.get(i)) {

//				//find a route
//				//routeSelection(getPosition(), coordinate, wholeMap); -> -1   (x,y)
				List<Coordinate> coordinates;
				if (getAllKeys) {
					// there might be path that we can't get to finish (unlikely)
					coordinates = dijkstraPathFinder.planRoute(new Coordinate(getPosition()), finishPoint, wholeMap, weightMap);
				}else {
					coordinates = dijkstraPathFinder.planRoute(new Coordinate(getPosition()), coordinate, wholeMap, weightMap);
				}
//				System.out.println(getPosition());
				
				Direction orientation = getOrientation();
				if (coordinates == null || coordinates.size() <= 1) {
					i++;
					continue;
				}
				System.out.println("Now at:	 "+getPosition());
				System.out.println("WantToFinishAt:    "+ coordinate);
				System.out.println(coordinates);
//				System.out.println("=======");
				
				nextCoordinate = coordinates.get(1);
//				 the weight of destination - 1
				weightMap.put(coordinates.get(coordinates.size()-1),weightMap.get(coordinates.get(coordinates.size()-1))-1);
				
//				List<Coordinate> movable = getMovableCoor(coordinate, orientation, getSpeed());
//				System.out.println("  *****   " + movable);
//				if (!checkInORNot(movable, coordinate)) {
//					i++;
//					continue;
//				}

				int x = nextCoordinate.x-new Coordinate(getPosition()).x;
				int y = nextCoordinate.y-new Coordinate(getPosition()).y;
				
//				System.out.println("X value :  "+x);
//				System.out.println("Y value :  "+y);
				
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
				
				System.out.println("WANTTOTURN :  " + direction);
				System.out.println("FACETO: " + orientation);
				System.out.println("Current speed is:  " + getSpeed());
				System.out.println("Current health is"+getHealth());
				if(!coordinates.isEmpty()) {
// this should be commented
					if (wholeMap.get(nextCoordinate).isType(MapTile.Type.WALL)) {
						System.out.println("fuck virgin");
						applyBrake();
					}
					else {
						switch (orientation) {
						case EAST:
							eastTurn(direction);
//							System.out.println("a");
							break;
						case WEST:
							westTurn(direction);
//							System.out.println("b");
							break;
						case NORTH:
							northTurn(direction);
//							System.out.println("c");
							break;
						case SOUTH:
							southTurn(direction);
//							System.out.println("d");
							break;
						}
					}
				}
				System.out.println("speed after move "+ getSpeed());
				System.out.println("health after move"+getHealth());
				System.out.println("#########");
				break;
			}
			
		}
		//System.out.println(weightMap);
//		Set<Integer> keys = Simulation.getKeys();
//		Simulation.resetKeys();
//		for (int k : keys){
//		     switch (k){
//		        case Input.Keys.B:
//		        	applyBrake();
//		            break;
//		        case Input.Keys.UP:
//		        	applyForwardAcceleration();
//		            break;
//		        case Input.Keys.DOWN:
//		        	applyReverseAcceleration();
//		        	break;
//		        case Input.Keys.LEFT:
//		        	turnLeft();
//		        	break;
//		        case Input.Keys.RIGHT:
//		        	turnRight();
//		        	break;
//		        default:
//		      }
//		  }
	}
	
//	private Boolean checkInORNot(List<Coordinate> coordinates, Coordinate coordinate) {
//		for (Coordinate coor:coordinates) {
//			if (coor.equals(coordinate)) {
//				return true;
//			}
//		}
//		return false;
//		
//	}
//	
//	private Boolean checkWall(Coordinate coor) {
//		if (wholeMap.get(coor).isType(MapTile.Type.WALL)) {
//			return true;
//		}
//		return false;
//	}
//	
//	private List<Coordinate> getMovableCoor(Coordinate coordinate, Direction orientation, float speed) {
//		List<Coordinate> movable = new ArrayList<>();
//		if (speed > 0) {
//			for (int i=-1; i<2; i=i+2) {
//				if (coordinate.x+i < World.MAP_WIDTH && coordinate.x+i >=0 && checkWall(new Coordinate(coordinate.x+i, coordinate.y))) {
//					
//					movable.add(new Coordinate(coordinate.x+i, coordinate.y));
//				}
//				if (coordinate.y+i < World.MAP_HEIGHT && coordinate.y+i >=0 && checkWall(new Coordinate(coordinate.x, coordinate.y+i))) {
//					movable.add(new Coordinate(coordinate.x, coordinate.y+i));
//				}
//			}
//		} else {
//			switch (orientation) {
//			case NORTH:
//			case SOUTH:
//				Coordinate child1 = new Coordinate(coordinate.x, coordinate.y+1);
//				Coordinate child2 = new Coordinate(coordinate.x, coordinate.y-1);
//				if (!checkWall(child1)) {
//					movable.add(child1);
//				}
//				if (!checkWall(child2)) {
//					movable.add(child2);
//				}
//				
//				break;
//			case WEST:
//			case EAST:
//				Coordinate child3 = new Coordinate(coordinate.x+1, coordinate.y);
//				Coordinate child4 = new Coordinate(coordinate.x-1, coordinate.y);
//				if (!checkWall(child4)) {
//					movable.add(child4);
//				}
//				if (!checkWall(child3)) {
//					movable.add(child3);
//				}
//				break;
//			}
//		}
//				
//		return movable;
//		
//	}
	
}


