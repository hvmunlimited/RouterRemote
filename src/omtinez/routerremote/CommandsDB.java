package omtinez.routerremote;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommandsDB extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	
	public CommandsDB(Context context) {
		super(context, "router", null, 3);
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		// create tables
		db.execSQL("CREATE TABLE zyxel (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, cmd TEXT, type INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS router (" +
		"id INTEGER PRIMARY KEY UNIQUE, user TEXT, pwd TEXT, port INTEGER)");
		// save default credentials
		save("1234","1234",23);
		// insert default actions
		insert("zyxel", "Restart Router", "reboot",0);
		insert("zyxel", "Refresh IP", "adsl stop;adsl start --up",0);
		insert("zyxel", "Stop Adsl", "adsl stop",0);
		insert("zyxel", "Stop Wifi", "wlan config status down",0);
		insert("zyxel", "Deny MAC", "wlan config macmode 1;wlan config mac add ",2);
		insert("zyxel", "Start Adsl", "adsl start --up",0);
		insert("zyxel", "Start Wifi", "wlan config status up",0);
		insert("zyxel", "Show Hosts", "lanhosts show all",1);
		insert("zyxel", "Help", "?",1);
		insert("zyxel", "Exit", "exit", 0);
	}

	public void insert(String table, String name, String cmd, int type) {
		db.execSQL("INSERT OR REPLACE INTO " + table + " (name,cmd,type) VALUES ('" + 
				name + "','" + cmd + "'," + type + ")");
	}
	
	public void save(String user, String pwd, int port) {
		db.execSQL("INSERT OR REPLACE INTO router (id,user,pwd,port) VALUES (1,'"+user+"','"+pwd+"',"+port+")");
	}
	
	public void delete(String name) {
		db.execSQL("DELETE FROM zyxel WHERE name='" + name + "'");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.db = db;
		// drop old tables
		db.execSQL("DROP TABLE IF EXISTS commands");
		db.execSQL("DROP TABLE IF EXISTS zyxel");
		db.execSQL("DROP TABLE IF EXISTS router");
		// create new
		onCreate(db);
	}
	
	public String getUser() {
		Cursor c = db.rawQuery("SELECT user FROM router", null);
		while(c.moveToNext()) return c.getString(0);
		return null;
	}
	
	public String getPwd() {
		Cursor c = db.rawQuery("SELECT pwd FROM router", null);
		while(c.moveToNext()) return c.getString(0);
		return null;
	}
	
	public int getPort() {
		Cursor c = db.rawQuery("SELECT port FROM router", null);
		while(c.moveToNext()) return c.getInt(0);
		return 23;
	}
	
	public List<String> getActions() {
		Cursor c = db.rawQuery("SELECT name FROM zyxel", null);
		List<String> actions = new ArrayList<String>();
		while (c.moveToNext()) actions.add(c.getString(0));
		return actions;
	}
	
	public String getCmd(String action) {
		Cursor c = db.rawQuery("SELECT cmd FROM zyxel WHERE name='" + action + "'", null);
		while(c.moveToNext()) return c.getString(0);
		return null;
	}
	
	public int getType(String action) {
		Cursor c = db.rawQuery("SELECT type FROM zyxel WHERE name='" + action + "'", null);
		while(c.moveToNext()) return Integer.parseInt(c.getString(0));
		return -1;
	}

}
