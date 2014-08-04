package com.lowcoupling.trackerbot;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {

	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumns = {"name","latitude","longitude","range"};
	private String[] allEventsColumns = {"direction","name","time"};


	public DataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}


	private Event cursorToEvent(Cursor cursor){
		Event event = new Event();
		event.setId(0);
		event.setDirection(cursor.getInt(0));
		event.setName(cursor.getString(1));
		event.setTime(cursor.getLong(2));
		return event;
	}

	private Location cursorToLocation(Cursor cursor) {
		Location location = new Location();
		location.setName(cursor.getString(0));
		location.setLatitude(cursor.getFloat(1));
		location.setLongitude(cursor.getFloat(2));
		location.setRange(cursor.getInt(3));
		return location;
	}

	public Location createLocation(String name,double d, double e,int range) {
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("latitude", d);
		values.put("longitude", e);
		values.put("range", range);
		Location newLocation = null;
		if(getLocation(name)==null){
			database.insert("Locations", null, values);
			String[] params = {name};
			Cursor cursor = database.query("Locations", allColumns, "name  = ?", params,null, null, null);
			cursor.moveToFirst();
			newLocation = cursorToLocation(cursor);
			cursor.close();
		}
		return newLocation;
	}
	
	public Event createEvent(String name, int direction, long seconds){
		ContentValues values = new ContentValues();
		values.put("name",name);
		values.put("direction",direction);
		values.put("time", seconds);
		Event evt = null;
		int id = (int) database.insert("Events",null, values);

//		String [] params = {Integer.toString(id)};
//		Cursor cursor = database.query("Events",allEventsColumns,"id=?",params,null,null,null);
//		cursor.moveToFirst();
//		evt = cursorToEvent(cursor);
//		cursor.close();
		return evt;
		
	}

	public List<Location> getAllLocations() {
		List<Location> locations = new ArrayList<Location>();

		Cursor cursor = database.query("Locations",
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location loc = cursorToLocation(cursor);
			locations.add(loc);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return locations;
	}
	
	public void deleteAllEvents(){
		
			database.delete("Events", null, null);

	}
	
	public List<Event> getAllEvents(){
		List<Event> events = new ArrayList<Event>();
		Cursor cursor = database.query("Events",allEventsColumns,null,null,null,null,null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Event evt = cursorToEvent(cursor);
			events.add(evt);
			cursor.moveToNext();
		}
		cursor.close();
		return events;
	}
	
	public void deleteLocation(String name){
		String[] params = {name};
		database.delete("Locations", "name  = ?", params);

	}
	
	public void deleteEvent(long time){
		String [] params = {Long.toString(time)};
		database.delete("Events","time=?",params);
	}
	
	public Location getLocation(String name){
		String[] params = {name};
		Location loc = null;
		Cursor cursor = database.query("Locations", allColumns, "name  = ?", params,null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount()!=0){
			loc = cursorToLocation(cursor);
		}
		return loc;
	}

	public Event getEvent(int id){
		String[] params = {Integer.toString(id)};
		Event evt = null;
		Cursor cursor = database.query("Events",allEventsColumns, "id=?",params,null,null,null);
		cursor.moveToFirst();
		if(cursor.getCount()!=0){
			evt = cursorToEvent(cursor);
		}
		return evt;
	}

	public Location updateLocation(Location loc){
		String[] params={loc.getName()};
		Location tgt = null;
		Cursor cursor = database.query("Locations", allColumns, "name  = ?", params,null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount()!=0){
			tgt = cursorToLocation(cursor);
		}
		cursor.close();
		ContentValues args = new ContentValues();
		args.put("latitude", loc.getLatitude());
		args.put("longitude", loc.getLongitude());
		args.put("range", loc.getRange());
		String[] whereArgs = {loc.getName()};
		if(tgt!=null){
			database.update("Locations", args, "name=?" , whereArgs);
		}else{
			loc = createLocation(loc.getName(),loc.getLatitude(),loc.getLongitude(),loc.getRange());
		}
		return loc;
	}
}





