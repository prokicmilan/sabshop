package student;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class ShortestPathUtil {
	
	private static final Map<Integer, List<ShortestPathNode>> connectedCities;
	private static final List<Integer> cityIdList;
	
	static {
		pm160695_CityOperations cityOperations = new pm160695_CityOperations();
		Map<Integer, List<ShortestPathNode>> connected = new HashMap<>();
		
		// dohvatamo listu svih gradova
		List<Integer> citiesList = cityOperations.getCities();
		for (Integer cityId : citiesList) {
			
			// za svaki grad dohvatamo listu njemu susednih gradova
			List<Integer> connections = cityOperations.getConnectedCities(cityId);
			
			List<ShortestPathNode> nodes = new LinkedList<>();
			// za svako susedstvo dohvatamo distancu i ubacujemo u listu
			for (Integer connectedCityId : connections) {
				int distance = cityOperations.getDistanceBetweenCities(cityId, connectedCityId);
				// connectedCityId/cityId - sused, cityId/prevCityId - sam grad za koji trenutno gledamo susede
				nodes.add(new ShortestPathNode(connectedCityId, cityId, distance));
			}
			connected.put(cityId, nodes);
		}
		
		cityIdList = Collections.unmodifiableList(citiesList);
		connectedCities = Collections.unmodifiableMap(connected);
	}
	
	public static List<ShortestPathNode> determineShortestPath(int source, int target) {
		PriorityQueue<ShortestPathNode> queue = new PriorityQueue<>(Comparator.comparing(ShortestPathNode::getCost));
		Map<Integer, ShortestPathNode> nodeMap = new HashMap<>();
		ShortestPathNode destination = null; 
		
		for (Integer cityId : cityIdList) {
			ShortestPathNode node = new ShortestPathNode(cityId, -1);
			if (cityId == source) {
				node.setCost(0);
			}
			if (cityId == target) {
				destination = node;
			}
			queue.add(node);
			nodeMap.put(cityId, node);
		}
		Set<ShortestPathNode> visitedNodes = new HashSet<>();
		ShortestPathNode currentNode = null;
		while (!destination.equals(currentNode) && !queue.isEmpty()) {
			currentNode = queue.remove();
			List<ShortestPathNode> neighbours = connectedCities.get(currentNode.getCityId());
			for (ShortestPathNode neighbour : neighbours) {
				ShortestPathNode currentNodeStatus = nodeMap.get(neighbour.getCityId());
				if (visitedNodes.contains(currentNodeStatus)) continue;
				if (currentNode.getCost() + neighbour.getCost() < currentNodeStatus.getCost()) {
					currentNodeStatus.setCost(currentNode.getCost() + neighbour.getCost());
					currentNodeStatus.setNextCityId(currentNode.getCityId());
					currentNodeStatus.setDistance(neighbour.getCost());
				}
			}
			visitedNodes.add(currentNode);
		}
		List<ShortestPathNode> shortestPath = new LinkedList<>();
		while (currentNode.getNextCityId() != -1) {
			shortestPath.add(currentNode);
			currentNode = nodeMap.get(currentNode.getNextCityId());
		}
		shortestPath.add(currentNode);

		return shortestPath;
	}
	
}
