package student;

public class ShortestPathNode {

	private int cityId;
	private int nextCityId;
	private int cost;
	private int distance;
	
	public ShortestPathNode(int cityId, int prevCityId) {
		this.cityId = cityId;
		this.nextCityId = prevCityId;
		this.cost = Integer.MAX_VALUE;
	}
	
	public ShortestPathNode(int cityId, int prevCityId, int cost) {
		this.cityId = cityId;
		this.nextCityId = prevCityId;
		this.cost = cost;
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	
	public int getNextCityId() {
		return nextCityId;
	}
	
	public void setNextCityId(int nextCityId) {
		this.nextCityId = nextCityId;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cityId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShortestPathNode other = (ShortestPathNode) obj;
		if (cityId != other.cityId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "cityId=" + cityId + ", nextCityId=" + nextCityId + ", cost=" + cost + ", distance=" + distance;
	}
	
}
