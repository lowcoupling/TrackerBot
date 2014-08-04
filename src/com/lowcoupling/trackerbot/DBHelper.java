package com.lowcoupling.trackerbot;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String Location_Table_Create =
                "CREATE TABLE Locations("
                + "name varchar(200),"
                + "latitude float,"
                + "longitude float,"
                + "range int"
                + ")";
    private static final String Events_Table_Create = 
    		"CREATE TABLE Events("
    			+"direction int,"
    			+"name String,"
    			+"time long)";
    
    DBHelper(Context context) {
        super(context, "locations.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Location_Table_Create);
        db.execSQL(Events_Table_Create);
 
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}