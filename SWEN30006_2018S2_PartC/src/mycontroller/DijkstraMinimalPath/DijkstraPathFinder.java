package mycontroller.DijkstraMinimalPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;

public class DijkstraPathFinder implements IPathFinder {

	HashMap<Coordinate, Node> expanded;
	PriorityQueue<Node> frontier;
	
	public DijkstraPathFinder() {
		expanded = new HashMap<>();
		frontier = new PriorityQueue<>();
	}

	@Override
	public List<Coordinate> planRoute(Coordinate start, Coordinate finish, HashMap<Coordinate, MapTile> map) {
		expanded.clear();
		frontier.clear();
		// new Node( Node Parent, coordinate)
		Node current = new Node(null, start);
		current.setCost(0);
		expanded.put(current.coordinate, current);
		frontier.add(current);
		
		while (!frontier.isEmpty()) {
			// current is a node
			current = frontier.remove();
			current.traversed = true;
			
			if (finish.equals(current.coordinate)) {
				break;
			}
			// expand current, the children, we only has its coordinate
			current.getChildren().forEach(child -> {
				MapTile tile = map.get(child.coordinate);
//				if(tile.getType() == MapTile.Type.WALL) {
//					return;
//				}
				setNodeCost(child, map);
				if (child.cost < Double.POSITIVE_INFINITY) {
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
				}
			});
		}
		
		List<Coordinate> path = new ArrayList<>();
		while (current != null) {
			path.add(0, current.coordinate);
			current = current.parent;
		}
		if(path.get(path.size()-1).x > start.x+4 || path.get(path.size()-1).y > start.y+4)
			path.clear();
		if (path.size() == 1) {
			path.clear();
		}
		return path;
	}
	
	public void setNodeCost(Node node, HashMap<Coordinate, MapTile> map) {
		MapTile tile = map.get(node.coordinate);
		//  we can get the tile type
		//  Base one tile type, we set cost accordingly
		 /* 
		 * public class TileCostPool {

		private static TileCostPool instance = new TileCostPool();
		private HashMap<Class<?>, ITileCost> pool;
	
		public TileCostPool() {
			initiliazePool();
		}
	
		public void initiliazePool() {
			pool = new HashMap<>();
			pool.put((new MapTile(MapTile.Type.ROAD)).getClass(), new MapTileCost());
			pool.put((new MudTrap()).getClass(), new MudTrapCost());
			pool.put((new LavaTrap()).getClass(), new LavaTrapCost());
			pool.put((new GrassTrap()).getClass(), new GrassTrapCost());
		}
	
		public static TileCostPool getInstance() {
			return instance;
		}
	
		public ITileCost getTileCost(MapTile tile) {
			return pool.get(tile.getClass());
		}		

		}
		 */
//		ITileCost tileCost = TileCostPool.getInstance().getTileCost(tile);
//		node.setCost(tileCost.getCost(node, map) + node.parent.cost);
		
		if(tile.getType() == MapTile.Type.WALL) {
			node.setCost(Double.POSITIVE_INFINITY);
		}
		else node.setCost(1+node.parent.cost);
	}

}
