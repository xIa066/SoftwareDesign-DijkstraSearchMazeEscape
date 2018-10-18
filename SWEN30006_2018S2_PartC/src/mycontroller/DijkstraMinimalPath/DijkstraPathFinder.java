package mycontroller.DijkstraMinimalPath;

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

public class DijkstraPathFinder implements IPathFinder {

	HashMap<Coordinate, Node> expanded;
	PriorityQueue<Node> frontier;
	HashMap<Coordinate, Node> unreachable;
	int i;
	
	public DijkstraPathFinder() {
		expanded = new HashMap<>();
		frontier = new PriorityQueue<>();
		i = 0;
	}

	@Override
	public List<Coordinate> planRoute(Coordinate start, Coordinate finish, HashMap<Coordinate, MapTile> map) {
		expanded.clear();
		frontier.clear();
		i=0;
//		System.out.println("start: " +start );
//		System.out.println("finish: " +finish);
		// new Node( Node Parent, coordinate)
		Node current = new Node(null, start);
		current.setCost(0);
		expanded.put(current.coordinate, current);
		frontier.add(current);
//		System.out.println("this current Node:   "+current.coordinate.toString());
//		System.out.println("this current Node cost:  "+current.cost);
//		for (Node child: current.getChildren()) {
//			System.out.println("Child           " +child.coordinate.toString());
//		}
		
		while (!frontier.isEmpty()) {
			i++;
			// current is a node
			current = frontier.remove();
			current.traversed = true;
//			System.out.println("current coordinate:" +current.coordinate);
//			System.out.println("current cost:" + current.cost);
//			System.out.println('*');
			
			if (finish.equals(current.coordinate) ) {
				break;
			}
			// expand current, the children, we only has its coordinate
			//current.getChildren().forEach(child -> {
			
//			List<Node> children = current.getChildren();
//			Iterator<Node> it = children.iterator();
//			
//			while (it.hasNext()) {
//				Node child = it.next();
			current.getChildren().forEach(child -> {

				setNodeCost(child, map);
//				System.out.println(child.coordinate);
//				System.out.println(child.cost);
//				if (child.cost < Integer.MAX_VALUE) {
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
			);
//			System.out.println("===========");
		}
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("*Actual finish: " + current.coordinate);
		System.out.println("*Actual cost" + current.cost);
		
		List<Coordinate> path = new ArrayList<>();
		while (current != null) {
			path.add(0, current.coordinate);
			current = current.parent;
		}
//		if(path.get(path.size()-1).x > start.x+4 || path.get(path.size()-1).y > start.y+4 || path.get(path.size()-1).x < start.x-4 || path.get(path.size()-1).y < start.y-4)
//			path.clear();
		if (path.size() == 1) {
			path.clear();
		}
		System.out.println("$ i value is " + i);
		return path;
	}
	
	private boolean finishUnreachable() {
		
		return false;
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
			node.setCost(Integer.MAX_VALUE);
		}else {
			if(tile.isType(MapTile.Type.TRAP)) {
				if(((TrapTile) tile).getTrap().equals("lava")){
					node.setCost(100+node.parent.cost);
				}else if(((TrapTile) tile).getTrap().equals("mud")){
					node.setCost(Integer.MAX_VALUE);
				}else if(((TrapTile) tile).getTrap().equals("health")){//current health to be added
					node.setCost(1+node.parent.cost);
				}else if(((TrapTile) tile).getTrap().equals("grass")){
					node.setCost(1+node.parent.cost);
				}
			}
			else node.setCost(1+node.parent.cost);
		}
	}

}
