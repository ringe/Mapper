package no.rin.mapper;

public class Point {
	private int order, lng, lat, range, contest_id, id;
	private String rebus, image;
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getLng() {
		return lng;
	}
	public void setLng(int lng) {
		this.lng = lng;
	}
	public int getLat() {
		return lat;
	}
	public void setLat(int lat) {
		this.lat = lat;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getContest_id() {
		return contest_id;
	}
	public void setContest_id(int contest_id) {
		this.contest_id = contest_id;
	}
	public String getRebus() {
		return rebus;
	}
	public void setRebus(String rebus) {
		this.rebus = rebus;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		return order + ": " +  rebus;
	}
}
