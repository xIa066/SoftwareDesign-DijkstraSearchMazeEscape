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
			// 涓�涓猯oop 寰幆 2锛�3锛�4 娆★紝 children 鏁伴噺
			current.getChildren().forEach(child -> {
				// set the cost = parent's cost + tile cost 锛坱ile cost is get from hashing global map锛�
				// 鏍规嵁澶у湴鍥撅紝鏉ヨ绠梒ost
				setNodeCost(child, map);
				if (child.cost < Double.POSITIVE_INFINITY) {
					// 濡傛灉杩樻病鏈夎graph 璇诲埌杩�, 鍔犲叆鍒癵raph 閲岄潰
				if (!expanded.containsKey(child.coordinate)) {
					expanded.put(child.coordinate, child);
					// 鏇存柊priority queue
					frontier.add(child);
				}
				// traversed == true 锛屽鏋滄槸宸茬粡纭畾鏈�鐭窛绂荤殑鐐癸紝灏变笉鐢ㄨ�冭檻锛屽洜涓哄凡缁忎粠priority queue 閲岄潰纰板嚭鏉ヤ簡
				else if (expanded.get(child.coordinate).traversed == false) {
					if (child.cost < expanded.get(child.coordinate).cost) {
						// 鏇存柊priority queue 璺焔raph
						frontier.remove(expanded.get(child.coordinate));
						frontier.add(child);
						expanded.remove(child.coordinate);
						expanded.put(child.coordinate, child);
					}
				}
				}
			});
		}
		
		//get path 锛� 杩欐椂鍊� current 宸茬粡鏄痜inish鐐癸紱 
		//loop 缁堟锛屼竴绉嶆儏鍐垫槸鍦╟urrent = finish锛� 杩樻湁灏辨槸鍦� 宸茬粡鎺㈠埌娌℃湁璺彲浠ユ帰鐨勬儏鍐典笅
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
		// 杩欓噷鍙互涓撻棬鍐嶅啓涓�涓� 涓嶅悓tile type 鐨刾ackage
		// 浠栦滑鐨刢ode 鏄啓浜嗕竴涓猼ilecost package 锛岀劧鍚庨噷闈㈠畾涔�
		/* 鍙傝�� code
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
		node.cost += 1;
		
	}

}
