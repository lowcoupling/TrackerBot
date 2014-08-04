package com.lowcoupling.trackerbot;

public class Location {
	private String name;
	private double latitude;
	private double longitude;
	private int range;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String toString(){
		return (name+" "+latitude+" "+longitude);
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
}
