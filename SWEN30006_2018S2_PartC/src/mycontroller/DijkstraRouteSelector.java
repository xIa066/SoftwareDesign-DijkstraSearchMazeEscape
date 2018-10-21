/*
 * Group 20
 * This class is implement Dijkstra Algorithm
 */

package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import tiles.TrapTile;
import utilities.Coordinate;

public class DijkstraRouteSelector implements IRouteSelector {

	private final int LAVA_COST = 100;
	private final int NORMAL_COST = 1;
	
	HashMap<Coordinate, Node> expanded;
	PriorityQueue<Node> frontier;
	int i;
	
	public DijkstraRouteSelector() {
		expanded = new HashMap<>();
		frontier = new PriorityQueue<>();
		i = 0;
	}

	@Override
	public List<Coordinate> routeSelect(Coordinate start, Coordinate finish, HashMap<Coordinate, MapTile> map) {
		expanded.clear();
		frontier.clear();
		i=0;
		Node current = new Node(null, start);
		current.setCost(0);
		expanded.put(current.coordinate, current);
		frontier.add(current);
		
		while (!frontier.isEmpty()) {
			i++;
			// current is a node
			current = frontier.remove();
			current.traversed = true;
			
			if (finish.equals(current.coordinate) ) {
				break;
			}
			
			current.getChildren().forEach(child -> {
				setNodeCost(child, map);
				if (child.cost < Integer.MAX_VALUE) {
					if (!expanded.containsKey(child.coordinate)) {
						expanded.put(child.coordinate, child);
						frontier.add(child);
					}
					else if (expanded.get(child.coordinate).traversed == false) {
						if (child.cost < expanded.get(child.coordinate).cost) {
							frontier.remove(expanded.get(child.coordinate));
							frontier.add(child);
							expanded.remove(child.coordinate);
							expanded.put(child.coordinate, child);
						}
					}
				}}
			);
			
		}
		
		List<Coordinate> path = new ArrayList<>();
		while (current != null) {
			path.add(0, current.coordinate);
			current = current.parent;
		}
		if (path.size() == 1) {
			path.clear();
		}
		return path;
	}

	public void setNodeCost(Node node, HashMap<Coordinate, MapTile> map) {
		MapTile tile = map.get(node.coordinate);
				
		if(tile.getType() == MapTile.Type.WALL) {
			node.setCost(Integer.MAX_VALUE);
		}else {
			if(tile.isType(MapTile.Type.TRAP)) {
				if(((TrapTile) tile).getTrap().equals("lava")){
					node.setCost(LAVA_COST + node.parent.cost);
				}else if(((TrapTile) tile).getTrap().equals("mud")){
					node.setCost(Integer.MAX_VALUE);
				}else if(((TrapTile) tile).getTrap().equals("health")){//current health to be added
					node.setCost(NORMAL_COST + node.parent.cost);
				}else if(((TrapTile) tile).getTrap().equals("grass")){
					node.setCost(NORMAL_COST + node.parent.cost);
				}
			}
			else node.setCost(1+node.parent.cost);
		}
	}

}
