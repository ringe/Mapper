package no.rin.mapper;

public class Point {
	private int order, contest_id, id;
	private Double lng, lat;
	private float range;
	private String rebus, image;
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public float getRange() {
		return range;
	}
	public void setRange(float range) {
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
