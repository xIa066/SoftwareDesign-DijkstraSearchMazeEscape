package mycontroller;

import java.util.HashMap;

import tiles.GrassTrap;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import tiles.TrapTile;
import utilities.Coordinate;

public class ExploreMap {
	
	public final static int KEY_WEIGHT = 10000;
	public final static int LAVA_WEIGHT = 50;
	public final static int NORMAL_WEIGHT = 10000;


	
	public ExploreMap(HashMap<Coordinate, MapTile> currentView, 
			HashMap<Coordinate, MapTile> wholeMap, 
			HashMap<Coordinate, Integer> weightMap, 
			HashMap<Coordinate, Boolean> travelMap) {
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
	}
	
	
}
