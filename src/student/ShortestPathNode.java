package student;

public class ShortestPathNode {

	private int cityId;
	private int prevCityId;
	private int cost;
	
	public ShortestPathNode(int cityId, int prevCityId) {
		this.cityId = cityId;
		this.prevCityId = prevCityId;
		this.cost = Integer.MAX_VALUE;
	}
	
	public ShortestPathNode(int cityId, int prevCityId, int cost) {
		this.cityId = cityId;
		this.prevCityId = prevCityId;
		this.cost = cost;
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	
	public int getPrevCityId() {
		return prevCityId;
	}
	
	public void setPrevCityId(int prevCityId) {
		this.prevCityId = prevCityId;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
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
		return "cityId=" + cityId + ", prevCityId=" + prevCityId + ", cost=" + cost;
	}
	
}
