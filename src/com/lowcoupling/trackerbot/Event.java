package com.lowcoupling.trackerbot;

public class Event {

	private long id;
	private String name;
	private int direction;
	private long time;
	
	public long getId() {
		return id;
	}
	public void setId(long l) {
		this.id = l;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long l) {
		this.time = l;
	}
	
}
