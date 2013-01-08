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
		super(context, "router", null, 4);
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		// create tables
		db.execSQL("CREATE TABLE commands (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, icon INTEGER, cmd TEXT, type INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS router (" +
		"id INTEGER PRIMARY KEY UNIQUE, user TEXT, pwd TEXT, port INTEGER)");
		// save default credentials
		save("1234","1234",23);
		// insert default actions
		insert("commands", "Restart Router", "reboot", R.drawable.restart_router, 0);
		insert("commands", "Refresh IP", "adsl stop;adsl start --up", R.drawable.refresh_ip, 0);
		insert("commands", "Stop Adsl", "adsl stop", R.drawable.stop_adsl, 0);
		insert("commands", "Stop Wifi", "wlan config status down", R.drawable.stop_wifi,0);
		insert("commands", "Deny MAC", "wlan config macmode 1;wlan config mac add ", R.drawable.deny_mac, 2);
		insert("commands", "Start Adsl", "adsl start --up", R.drawable.start_adsl, 0);
		insert("commands", "Start Wifi", "wlan config status up", R.drawable.start_wifi, 0);
		insert("commands", "Show Hosts", "lanhosts show all", R.drawable.view_hosts, 1);
		insert("commands", "Help", "?", R.drawable.help_telnet, 1);
		insert("commands", "Exit", "exit", R.drawable.exit_telnet, 0);
	}

	public void insert(String table, String name, String cmd, int icon, int type) {
		db.execSQL("INSERT OR REPLACE INTO " + table + " (name,cmd,icon,type) VALUES ('" + 
				name + "','" + cmd + "'," + icon + "," + type + ")");
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
	
	public List<String[]> getCommands() {
		Cursor c = db.rawQuery("SELECT name, icon, cmd, type FROM commands", null);
		List<String[]> actions = new ArrayList<String[]>();
		while (c.moveToNext()) {
			String[] res = new String[4];
			res[0] = c.getString(0);
			res[1] = c.getString(1);
			res[2] = c.getString(2);
			res[3] = c.getString(3);
			actions.add(res);
		}
		return actions;
	}
	
	// deprecated
	public List<String> getCmdNames() {
		Cursor c = db.rawQuery("SELECT name FROM zyxel", null);
		List<String> actions = new ArrayList<String>();
		while (c.moveToNext()) {
			actions.add(c.getString(0));
		}
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
