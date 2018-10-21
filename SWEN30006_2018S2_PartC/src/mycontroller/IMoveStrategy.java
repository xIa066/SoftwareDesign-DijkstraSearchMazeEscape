/* 
 * Group 20
 */

package mycontroller;

import java.util.HashMap;
import java.util.List;

import tiles.MapTile;
import utilities.Coordinate;

public interface IMoveStrategy {
	Coordinate destination = null;
	List<Coordinate> move(Coordinate start, HashMap<Coordinate, MapTile> wholeMap);
}
