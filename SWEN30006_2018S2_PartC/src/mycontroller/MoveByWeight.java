package mycontroller;

import java.util.HashMap;
import java.util.List;

import tiles.MapTile;
import utilities.Coordinate;

public class MoveByWeight implements IMoveStrategy{
	DijkstraRouteSelector dijkstraRouteSelector;
	Coordinate destination;
	public MoveByWeight(Coordinate destination) {
		dijkstraRouteSelector = new DijkstraRouteSelector();
		this.destination = destination;
	}
	
	@Override
	public List<Coordinate> move(Coordinate start, HashMap<Coordinate, MapTile> wholeMap) {
		return dijkstraRouteSelector.routeSelect(start, destination , wholeMap);
	}
}
