package omtinez.routerremote;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Actions extends ListActivity {
	CommandsDB db = null;
	Telnet telnet = null;
	
	public static final int TOAST = 0;
	public static final int ALERT_DIALOG = 1;
	public static final int INPUT_DIALOG = 2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get the variables
		String ip = getIntent().getStringExtra("omtinez.Telnet.ip");
		int port = getIntent().getIntExtra("omtinez.Telnet.port", 23);
		String user = getIntent().getStringExtra("omtinez.Telnet.user");
		String pwd = getIntent().getStringExtra("omtinez.Telnet.pwd");
		
		// connect to router if not connected yet
		if(telnet == null) {
			telnet = new Telnet(getApplicationContext(),ip,port);
			if(!telnet.login(user, pwd)) {
				Toast.makeText(this, "Unable to log in", Toast.LENGTH_LONG).show();
				this.finish();
			}
		}
		
		// get the commands from database
		db = new CommandsDB(this.getApplicationContext());
		List<String> actions = db.getActions();
		
		// Create an ArrayAdapter, that will actually make the Strings above appear in the ListView
		this.setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, actions));
		
		// handle long click
		this.getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo cinfo) {
				menu.setHeaderTitle("Context Menu");
				menu.add("Delete entry");
		}});
			
	}
	@Override
	public boolean onContextItemSelected(MenuItem aitem) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) aitem.getMenuInfo();
		String name = (String)super.getListView().getAdapter().getItem(menuInfo.position);
		db.delete(name); // delete record
		
		// reload the list view
		Toast.makeText(this, "Deleted: " + name, Toast.LENGTH_LONG).show();
		this.onCreate(null);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		String keyword = (String)this.getListAdapter().getItem(position);
		switch(db.getType(keyword)) {
		case TOAST:
			Toast.makeText(this, telnet.command(db.getCmd(keyword)), Toast.LENGTH_LONG).show();
			break;
		case ALERT_DIALOG:
			AlertDialog alert = (new AlertDialog.Builder(this)).create();
			alert.setMessage(telnet.command(db.getCmd(keyword)));
			alert.setTitle("Output");
			alert.show();
			break;
		case INPUT_DIALOG:
			AlertDialog alert1 = (new AlertDialog.Builder(this)).create();
			alert1.setTitle("Input");
			alert1.setMessage("Enter input data:");
			final EditText input = new EditText(this);
			final String keyword1 = keyword;
			final Context mContext = this;
			alert1.setView(input);
			alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String output = telnet.command(db.getCmd(keyword1).concat(input.getText().toString()));
				  Toast.makeText(mContext, output, Toast.LENGTH_LONG).show();
				}});
			alert1.show();
			break;
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add("Create Custom Command");
		Intent i = new Intent(Actions.this, Custom.class);
		item.setIntent(i);
		return super.onCreateOptionsMenu(menu);
	}
}