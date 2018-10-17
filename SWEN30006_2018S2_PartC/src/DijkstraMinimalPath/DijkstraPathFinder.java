package DijkstraMinimalPath;

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
			// 一个loop 循环 2，3，4 次， children 数量
			current.getChildren().forEach(child -> {
				// set the cost = parent's cost + tile cost （tile cost is get from hashing global map）
				// 根据大地图，来计算cost
				setNodeCost(child, map);
				if (child.cost < Double.POSITIVE_INFINITY) {
					// 如果还没有被graph 读到过, 加入到graph 里面
				if (!expanded.containsKey(child.coordinate)) {
					expanded.put(child.coordinate, child);
					// 更新priority queue
					frontier.add(child);
				}
				// traversed == true ，如果是已经确定最短距离的点，就不用考虑，因为已经从priority queue 里面碰出来了
				else if (expanded.get(child.coordinate).traversed == false) {
					if (child.cost < expanded.get(child.coordinate).cost) {
						// 更新priority queue 跟graph
						frontier.remove(expanded.get(child.coordinate));
						frontier.add(child);
						expanded.remove(child.coordinate);
						expanded.put(child.coordinate, child);
					}
				}
				}
			});
		}
		
		//get path ， 这时候 current 已经是finish点； 
		//loop 终止，一种情况是在current = finish， 还有就是在 已经探到没有路可以探的情况下
		List<Coordinate> path = new ArrayList<>();
		while (current != null) {
			path.add(0, current.coordinate);
			current = current.parent;
		}
		return path;
	}
	
	public void setNodeCost(Node node, HashMap<Coordinate, MapTile> map) {
		//MapTile tile = map.get(node.coordinate);
		//  we can get the tile type
		//  Base one tile type, we set cost accordingly
		// 这里可以专门再写一个 不同tile type 的package
		// 他们的code 是写了一个tilecost package ，然后里面定义
		/* 参考 code
		 * 
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
		node.cost += 100;
		
	}

}
