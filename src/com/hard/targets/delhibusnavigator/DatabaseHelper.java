package com.hard.targets.delhibusnavigator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("SdCardPath")
public class DatabaseHelper extends SQLiteOpenHelper {

	private SQLiteDatabase myDatabase;
	private final Context myContext;
	private static final String DATABASE_NAME = "bus.mp3";
	public final static String DATABASE_PATH = "/data/data/com.hard.targets.delhibusnavigator/databases/";
	public static final int DATABASE_VERSION = 1;
	
	Cursor c;
	int countcheck;
	int countd;
	int countf;
	int counts;
	String[] destList = null;
	String destbus = null;
	String[] list;
	String[] list1;
	String[] listf;
	String[] sourceList = null;
	int tempk;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
	}
	
	public void createDatabase() throws IOException {
		boolean dbExist = checkDatabase();
		
		if(dbExist) {
			Log.v("DB Exists", "db exists");
		}
		
		boolean dbExist1 = checkDatabase();
		
		if(!dbExist1) {
			this.getReadableDatabase();
			try {
				this.close();
				copyDatabase();
			} catch (IOException e) {
				throw new Error("Error copying Database");
			}
		}
	}
	
	private boolean checkDatabase() {
		boolean checkDB = false;
		
		try {
			String myPath = DATABASE_PATH + DATABASE_NAME;
			File dbFile = new File(myPath);
			checkDB = dbFile.exists();
		} catch (SQLiteException e) {
		}
		
		return checkDB;
	}
	
	private void copyDatabase() throws IOException {
		String outFileName = DATABASE_PATH + DATABASE_NAME;
		
		OutputStream myOutput = new FileOutputStream(outFileName);
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
		
		byte[] buffer = new byte[1024];
		int length;
		
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		
		myInput.close();
		myOutput.flush();
		myOutput.close();
	}
	
	public void db_delete() {
		File file = new File(DATABASE_PATH + DATABASE_NAME);
		if (file.exists()) {
			file.delete();
			System.out.println("Delete Database File");
		}
	}
	
	public void openDatabase() throws SQLException {
		String myPath = DATABASE_PATH + DATABASE_NAME;
		myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}
	
	public synchronized void closeDatabase() throws SQLException {
		if (myDatabase != null)
			myDatabase.close();
		super.close();
	}
	
	public Cursor getAllBusNumber() {
		String query = "SELECT * FROM BusRoute ORDER BY BusNumber";
		Cursor c = myDatabase.rawQuery(query, null);
		return c;
	}
	
	public Cursor searchBusNumber(String s) {
		String query = "SELECT * FROM BusRoute WHERE BusNumber LIKE '%" + s + "%' ORDER BY BusNumber";
		Cursor c = myDatabase.rawQuery(query, null);
		return c;
	}

	public LinkedList<String> getAutoFill() {
		String query = "SELECT DISTINCT Stop FROM BusStop";
		LinkedList<String> ll = new LinkedList<String>();
		Cursor c = myDatabase.rawQuery(query, null);
		c.moveToFirst();
		for(int i = 0; i <= c.getCount() - 1; i++) {
			ll.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		return ll;
	}
	
	public Cursor getSD(String bno) {
		String query = "SELECT * FROM BusRoute WHERE BusNumber='" + bno + "'";
		Cursor c = myDatabase.rawQuery(query, null);
		return c;
	}
	
	public LinkedList<String> getStops(String bno) {
		String query = "SELECT * FROM BusStop WHERE BusNumber='" + bno + "'";
		LinkedList<String> item = new LinkedList<String>();
		Cursor c = myDatabase.rawQuery(query, null);
		c.moveToFirst();
		for(int i = 0; i <= c.getCount() - 1; i++) {
			item.add(c.getString(1));
			c.moveToNext();
		}
		c.close();
		return item;
	}
	
	public LinkedList<String> getAutoStop() {
		String query = "SELECT DISTINCT Stop FROM BusStop";
		LinkedList<String> ll = new LinkedList<String>();
		Cursor c = myDatabase.rawQuery(query, null);
		c.moveToFirst();
		for(int i = 0; i <= c.getCount() - 1; i++) {
			ll.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		return ll;
	}
	
	public LinkedList<String> searchStop(String stop) {
		String query = "SELECT BusNumber FROM BusStop WHERE Stop='" + stop + "'";
		LinkedList<String> ll = new LinkedList<String>();
		Cursor c = myDatabase.rawQuery(query, null);
		c.moveToFirst();
		for(int i = 0; i <= c.getCount() - 1; i++) {
			ll.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		return ll;
	}

	public boolean getDirectRoute(String start, String end) {
		String query = "SELECT DISTINCT BusNumber FROM BusStop WHERE BusNumber IN (SELECT BusNumber FROM BusStop WHERE Stop='" + start + "' INTERSECT SELECT BusNumber FROM BusStop WHERE Stop ='" + end + "') ";
		Cursor c = myDatabase.rawQuery(query, null);
		int i = c.getCount();
		while(true) {
			if (!c.moveToNext()) {
				if (i != 0)
					break;
				return false;
			}
			String str = c.getString(c.getColumnIndex("BusNumber")) + " :  " + start + " to " + end;
			ViewRouteActivity.ll.add(str);
		}
		return true;
	}
	
	/*public boolean getOneStopRoute(String start, String end) {
		boolean bool = true;
		if (this.sourceList == null)
			setSourceList(start);
		setDestinationList(end);
		this.countcheck = 0;
		int j, i;
		for (i = 0; ; i++) {
			if (i >= this.counts) {
				if (this.countf == 0)
					bool = false;
				return bool;
			}
			j = 0;
			if (j < this.countd)
				break;
		}
		String str = "SELECT * FROM (SELECT Stop FROM BusStop WHERE BusNumber = '" + this.list[i] + "' INTERSECT SELECT Stop FROM BusStop WHERE BusNumber = '" + this.list1[j] + "')";
		Cursor localCursor = myDatabase.rawQuery(str, null);
		this.countf = localCursor.getCount();
		if (this.countf > 0)
			this.countcheck = (1 + this.countcheck);
		if (this.countcheck >= 10) {
			localCursor.close();
			return bool;
		}
		label445:
		while(true) {
			this.tempk = (1 + this.tempk);
			ViewRouteActivity.tvLabel.append(Html.fromHtml("<b>" + this.tempk + "</b>" + ". " + start + "--> <font color=#1B1BF5><b>" + this.list[i] + "</b> </font> -->" + localCursor.getString(localCursor.getColumnIndex("stops"))));
		    ViewRouteActivity.tvLabel.append("\n");
		    ViewRouteActivity.tvLabel.append(Html.fromHtml(localCursor.getString(localCursor.getColumnIndex("stops")) + "--> <font color=#EC4217><b>" + this.list1[j] + "</b> </font> -->" + end));
		    if (this.destbus != null) {
		    	ViewRouteActivity.tvLabel.append("\n");
		        ViewRouteActivity.tvLabel.append(Html.fromHtml(end + "--> <font color=#2B9706><b>" + this.destbus + "</b> </font> -->" + end));
		        ViewRouteActivity.tvLabel.append("\n\n");
		    }
		    while(true) {
		    	if(localCursor.moveToNext())
		    		break label445;
		    	localCursor.close();
		    	j++;
		    	break;
		    }
		}
	}
	
	public boolean getTwoStopRoute(String start, String end) {
		boolean bool = false;
		this.sourceList = this.list;
		this.destList = this.list1;
		int i = 0;
		if (i >= this.destList.length) {
			this.destbus = null;
			return bool;
		}
		String str = "SELECT Stop from BusStop WHERE BusNumber='" + this.destList[i] + "'";
		this.destbus = this.destList[i];
		Cursor localCursor = myDatabase.rawQuery(str, null);
		this.countf = localCursor.getCount();
		this.listf = new String[this.countf];
		int k;
		for (int j = 0; ; j++) {
			if (!localCursor.moveToNext()) {
				k = 0;
				if (k < this.listf.length)
					break label173;
				i++;
				break;
			}
			this.listf[j] = localCursor.getString(localCursor.getColumnIndex("Stop"));
		}
		label173: if (this.listf[k].equals(end));
		while(true) {
			k++;
			break;
			if (this.countcheck >= 5)
				return true;
			bool = getOneStopRoute(start, this.listf[k]);
		}
	}
	
	public void setSourceList(String start) {
		String query = "SELECT BusNumber FROM BusStop WHERE Stop='" + start + "' ";
		Cursor c = myDatabase.rawQuery(query, null);
		this.counts = c.getCount();
		this.list = new String[this.counts];
		for(int i = 0; ; i++) {
			if (!c.moveToNext())
				return;
			this.list[i] = c.getString(c.getColumnIndex("BusNumber"));
		}
	}
	
	public void setDestinationList(String end) {
		String query = "SELECT BusNumber FROM BusStop WHERE Stop='" + end + "' ";
		Cursor c = myDatabase.rawQuery(query, null);
		this.countd = c.getCount();
		this.list1 = new String[this.countd];
		for(int i = 0; ; i++) {
			if (!c.moveToNext())
				return;
			this.list1[i] = c.getString(c.getColumnIndex("BusNumber"));
		}
	}*/
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			Log.v("Database Uppgrade", "Database version higher than old.");
			db_delete();
		}
	}
}
