package no.rin.mapper;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SQLiteAdapter {

	public static final String MYDATABASE_NAME = "MY_DATABASE";
	public static final String MYDATABASE_TABLE = "MY_TABLE";
	public static final int MYDATABASE_VERSION = 1;
	public static final String KEY_ID = "Trip";
	public static final String KEY_LAT = "Latitude";
	public static final String KEY_LON = "Longtitude";

	//create table MY_DATABASE (ID integer primary key, Content text not null);
	private static final String SCRIPT_CREATE_DATABASE =
			"create table " + MYDATABASE_TABLE
			+ " (" + KEY_ID + " integer not null, "
			+ KEY_LON + " integer not null, "
			+ KEY_LAT + " integer not null);";

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	private Context context;

	public SQLiteAdapter(Context c){
		context = c;
	}

	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this; 
	}

	public SQLiteAdapter open() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this; 
	}

	public void close(){
		sqLiteHelper.close();
	}

	public long insert(int trip, int lat, int lon){

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_ID, trip);
		contentValues.put(KEY_LAT, lat);
		contentValues.put(KEY_LON, lon);
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}

	public int deleteAll(){
		return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
	}

	public List<GeoPoint> selectAll(int id) {
		List<GeoPoint> list = new ArrayList<GeoPoint>();

		Cursor c = this.sqLiteDatabase.query(MYDATABASE_TABLE, new String[]{KEY_LAT, KEY_LON}, KEY_ID + " =?",
				new String[] {""+id}, null, null, null);
		if(c.moveToFirst()){
			do{
				int lat = c.getInt(0);
				int lon = c.getInt(1);
				list.add(new GeoPoint(lat,lon));
			} while (c.moveToNext());
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		return list;
	}

	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

	public int getId() {
		int out = 0;

		Cursor c = this.sqLiteDatabase.query(MYDATABASE_TABLE, new String[]{KEY_ID}, null, null, null, null, null);
		if (c.moveToLast()) {
			out = c.getInt(0);
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		
		return out + 1;
	}

	public List<GeoPoint> selectLast() {
		int out = 0;
		Cursor c = this.sqLiteDatabase.query(MYDATABASE_TABLE, new String[]{KEY_ID}, null, null, null, null, null);
		if (c.moveToLast()) {
			out = c.getInt(0);
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		return selectAll(out);
	}

}