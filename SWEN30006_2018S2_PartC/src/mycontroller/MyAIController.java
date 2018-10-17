package mycontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Input;


import controller.CarController;
import swen30006.driving.Simulation;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController{

	private HashMap<Coordinate, MapTile> wholeMap;
	private HashMap<Coordinate, Integer> weightMap;
	public MyAIController(Car car) {
		super(car);
		wholeMap = getMap();
		weightMap = new HashMap<Coordinate,Integer>();
		
		for(Coordinate coordinate : wholeMap.keySet()) {
			if(wholeMap.get(coordinate).isType(MapTile.Type.ROAD)) {
				weightMap.put(coordinate, 100);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.START)) {
				weightMap.put(coordinate, 100);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.WALL)) {
				weightMap.put(coordinate, Integer.MIN_VALUE);
			}else if(wholeMap.get(coordinate).isType(MapTile.Type.FINISH)) {
				weightMap.put(coordinate,Integer.MIN_VALUE);//need to be modified
			}
		}
		System.out.println(weightMap);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		HashMap<Coordinate, MapTile> currentView = getView();
		for(Coordinate coordinate : currentView.keySet()) {
			MapTile tile =  currentView.get(coordinate);
			if(tile.isType(MapTile.Type.EMPTY)) {
				weightMap.put(coordinate, Integer.MIN_VALUE);
			}else if(tile.isType(MapTile.Type.TRAP)) {
				//System.out.println(((TrapTile) tile).getTrap()+"    "+coordinate);
				if(((TrapTile) tile).getTrap().equals("lava")){
					if(((LavaTrap) tile).getKey() != 0 && wholeMap.get(coordinate).isType(MapTile.Type.ROAD)) {
						
						weightMap.put(coordinate, 10000);
					}else if(((LavaTrap) tile).getKey() == 0 && wholeMap.get(coordinate).isType(MapTile.Type.ROAD)) {
						weightMap.put(coordinate, 50);
					}
				}else if(((TrapTile) tile).getTrap().equals("mud") && wholeMap.get(coordinate).isType(MapTile.Type.ROAD)){
					weightMap.put(coordinate, Integer.MIN_VALUE);
				}else if(((TrapTile) tile).getTrap().equals("health")){//current health to be added
					weightMap.put(coordinate, 200);
				}else if(((TrapTile) tile).getTrap().equals("grass") && wholeMap.get(coordinate).isType(MapTile.Type.ROAD)){
					weightMap.put(coordinate, 100);
				}
			}
		}
		ArrayList<Integer> arrayList = new ArrayList<>();
		for(Coordinate coordinate : currentView.keySet()) {
			arrayList.add(weightMap.get(coordinate));
			System.out.println(weightMap.get(coordinate)+"    "+coordinate+"\n\n\n\n");
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
				List<Coordinate> coordinates =  dijkstraPathFinder.planRoute(new Coordinate(getPosition()), coordinate, wholeMap);
				System.out.println(coordinates);
				
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
