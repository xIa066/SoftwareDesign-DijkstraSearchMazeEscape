/*
 * Group 20
 */

package mycontroller;

import java.util.HashMap;
import java.util.List;

import tiles.MapTile;
import utilities.Coordinate;

public interface IRouteSelector {

	List<Coordinate> routeSelect(Coordinate start, Coordinate finish, HashMap<Coordinate, MapTile> map);

}
