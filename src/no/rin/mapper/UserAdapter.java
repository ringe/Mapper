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

public class UserAdapter {

	public static final String MYDATABASE_NAME = "USERDB";
	public static final String MYDATABASE_TABLE = "USER_TABLE";
	public static final int MYDATABASE_VERSION = 2;
	public static final String KEY_ID = "Contest";
	public static final String KEY_LAT = "Latitude";
	public static final String KEY_LON = "Longtitude";
	public static final String KEY_REBUS = "Rebus";
	public static final String KEY_USER = "Username";

	//create table MY_DATABASE (ID integer primary key, Content text not null);
	private static final String SCRIPT_CREATE_DATABASE =
			"create table " + MYDATABASE_TABLE
			+ " (" + KEY_ID + " integer, "
			+ KEY_LON + " integer, "
			+ KEY_LAT + " integer, "
			+ KEY_USER + " text not null, "
			+ KEY_REBUS + " text);";

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	private Context context;

	public UserAdapter(Context c){
		context = c;
	}

	public UserAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this; 
	}

	public UserAdapter open() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this; 
	}

	public void close(){
		sqLiteHelper.close();
	}

	public long insert(int trip, int lat, int lon, String user, String rebus){

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_ID, trip);
		contentValues.put(KEY_LAT, lat);
		contentValues.put(KEY_LON, lon);
		contentValues.put(KEY_USER, user);
		contentValues.put(KEY_REBUS, rebus);
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}
	
	public long insertUser(String user){
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_USER, user);
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}

	public int deleteAll(){
		return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
	}

	public List<UserData> selectAll(int id) {
		List<UserData> list = new ArrayList<UserData>();

		Cursor c = this.sqLiteDatabase.query(MYDATABASE_TABLE, 
				new String[]{KEY_LAT, KEY_LON, KEY_USER, KEY_REBUS}, KEY_ID + " =?",
				new String[] {""+id}, null, null, null, null);
		if(c.moveToFirst()){
			do{
				UserData u = new UserData();
				int lat = c.getInt(0);
				int lon = c.getInt(1);
				u.setP(new GeoPoint(lat,lon));
				u.setUsername(c.getString(2));
				u.setRebus(c.getString(3));
				list.add(u);
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
			db.execSQL("drop table " + MYDATABASE_TABLE + ";");
			db.execSQL(SCRIPT_CREATE_DATABASE);
		}

	}

	public int getId() {
		int out = 0;

		Cursor c = this.sqLiteDatabase.query(MYDATABASE_TABLE, 
				new String[]{KEY_ID}, null, null, null, null, null);
		if (c.moveToLast()) {
			out = c.getInt(0);
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		
		return out + 1;
	}

	public List<UserData> selectLast() {
		int out = 0;
		Cursor c = this.sqLiteDatabase.query(MYDATABASE_TABLE, 
				new String[]{KEY_ID}, null, null, null, null, null);
		if (c.moveToLast()) {
			out = c.getInt(0);
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		return selectAll(out);
	}

}