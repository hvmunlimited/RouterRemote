package omtinez.routerremote;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class ListActivity extends SherlockListActivity {
	CommandsDB db;
	Telnet telnet;
	
	List<String[]> commands;
	
	public static final int TOAST = 0;
	public static final int ALERT_DIALOG = 1;
	public static final int INPUT_DIALOG = 2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// get telnet instance
		telnet = Telnet.getInstance();
		
		// get the commands from database
		db = new CommandsDB(this.getApplicationContext());
		commands = db.getCommands();
		
		// Create an ArrayAdapter, that will actually make the Strings above appear in the ListView
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		
		// populate the array adapter
		for (String[] command : commands) aa.add(command[0]);
		
		// hook the adapter to this view
		this.setListAdapter(aa);
		
		// handle long click
		this.getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo cinfo) {
				menu.setHeaderTitle("Context Menu");
				menu.add("Delete entry");
		}});
			
	}
	@Override
	public boolean onContextItemSelected(android.view.MenuItem aitem) {
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
		final String cmd = commands.get(position)[2];
		switch(Integer.parseInt(commands.get(position)[3])) {
		case TOAST:
			Toast.makeText(this, telnet.command(cmd), Toast.LENGTH_LONG).show();
			break;
		case ALERT_DIALOG:
			AlertDialog alert = (new AlertDialog.Builder(this)).create();
			alert.setMessage(telnet.command(cmd));
			alert.setTitle("Output");
			alert.show();
			break;
		case INPUT_DIALOG:
			AlertDialog alert1 = (new AlertDialog.Builder(this)).create();
			alert1.setTitle("Input");
			alert1.setMessage("Enter input data:");
			final EditText input = new EditText(this);
			final Context mContext = this;
			alert1.setView(input);
			alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String output = telnet.command(cmd.concat(input.getText().toString()));
				  Toast.makeText(mContext, output, Toast.LENGTH_LONG).show();
				}});
			alert1.show();
			break;
		}
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
			case android.R.id.home:
				startActivity(new Intent(this, MenuActivity.class));
				return true;
		}
		
		return(super.onOptionsItemSelected(item));
	}
    
    @Override
    public void onBackPressed() {
       startActivity(new Intent(this, MenuActivity.class));
    }
}