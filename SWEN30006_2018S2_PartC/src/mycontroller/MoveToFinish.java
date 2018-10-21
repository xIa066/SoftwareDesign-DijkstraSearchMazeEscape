package mycontroller;

import java.util.HashMap;
import java.util.List;
import tiles.MapTile;

import utilities.Coordinate;

public class MoveToFinish implements IMoveStrategy{
	DijkstraRouteSelector dijkstraRouteSelector;
	Coordinate destination;
	public MoveToFinish(Coordinate destination) {
		dijkstraRouteSelector = new DijkstraRouteSelector();
		this.destination = destination;
	}
	
	@Override
	public List<Coordinate> move(Coordinate start, HashMap<Coordinate, MapTile> wholeMap) {
		return dijkstraRouteSelector.routeSelect(start, destination , wholeMap);
	}
}
