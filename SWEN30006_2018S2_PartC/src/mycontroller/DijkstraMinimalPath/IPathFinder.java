package mycontroller.DijkstraMinimalPath;

import java.util.HashMap;
import java.util.List;

import tiles.MapTile;
import utilities.Coordinate;

public interface IPathFinder {

	List<Coordinate> planRoute(Coordinate start, Coordinate finish, HashMap<Coordinate, MapTile> map);

	List<Coordinate> planRoute(Coordinate start, Coordinate finish, HashMap<Coordinate, MapTile> map,
			HashMap<Coordinate, Integer> weightMap);

}
