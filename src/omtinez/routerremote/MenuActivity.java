package omtinez.routerremote;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.omtinez.ohmenu.Ohmenu;
import com.omtinez.ohmenu.OhmenuItem;

public class MenuActivity extends SherlockActivity {
	CommandsDB db;
	Telnet telnet;
	Activity activity;
	
	public static final int TOAST = 0;
	public static final int ALERT_DIALOG = 1;
	public static final int INPUT_DIALOG = 2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionsmenu);
		activity = this;

		// get telnet instance
		telnet = Telnet.getInstance();
		
		// get the commands from database
		db = new CommandsDB(this.getApplicationContext());
		List<String[]> commands = db.getCommands();
		
		// create the menu with this activity's context
		Ohmenu myMenu = new Ohmenu(this, "Router Remote", "Remote control for your router");
		
		// attach the menu's view to this activity's FrameLayout
		FrameLayout ohmenu_frame = (FrameLayout) findViewById(R.id.ohmenu_frame);
		ohmenu_frame.addView(myMenu.getView());
		
		// initialize the items
		OhmenuItem[] items = new OhmenuItem[commands.size()];
		
		// create the menu items
		for (int i = 0; i < commands.size(); i++) {
			
			// get the command from the list
			String[] cmd = commands.get(i);
			String cmdname = cmd[0];
			final String command = cmd[2];
			final int cmdtype = Integer.parseInt(cmd[3]);
			Bitmap icon = BitmapFactory.decodeResource(getResources(), Integer.parseInt(cmd[1]));
			
			// initialize menu item
			items[i] = new OhmenuItem(icon, cmdname, "Tap button to perform action");
			items[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					switch(cmdtype) {
					case TOAST:
						new Thread() { public void run() {
							final String output = telnet.command(command);
							activity.runOnUiThread(new Runnable() { public void run() {
								Toast.makeText(v.getContext(), output, Toast.LENGTH_LONG).show();
							}});
						}}.start();
						break;
					case ALERT_DIALOG:
						new Thread() { public void run() {
							final String output = telnet.command(command);
							activity.runOnUiThread(new Runnable() { public void run() {
								AlertDialog alert = (new AlertDialog.Builder(v.getContext())).create();
								alert.setMessage(output);
								alert.setTitle("Output");
								alert.show();
							}});
						}}.start();
						break;
					case INPUT_DIALOG:
						AlertDialog alert1 = (new AlertDialog.Builder(v.getContext())).create();
						alert1.setTitle("Input");
						alert1.setMessage("Enter input data:");
						final EditText input = new EditText(v.getContext());
						final String cmd = command;
						final Context mContext = v.getContext();
						alert1.setView(input);
						alert1.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								new Thread() { public void run() {
									final String output = telnet.command(cmd.concat(input.getText().toString()));
									activity.runOnUiThread(new Runnable() { public void run() {
										Toast.makeText(mContext, output, Toast.LENGTH_LONG).show();
									}});
								}}.start();
							}});
						alert1.show();
						break;
					}
				}
			});
			
			// add item to the menu
			myMenu.add(items[i]);
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.layout.actionsmenu_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
			case android.R.id.home:
				return true;
		
			case R.id.showList:
				startActivity(new Intent(this, ListActivity.class));
				return true;
				
			case R.id.addItem:
				startActivity(new Intent(this, CustomActivity.class));
				return true;
		}
		
		return(super.onOptionsItemSelected(item));
	}
    
    @Override
    public void onBackPressed() {
       startActivity(new Intent(this, LoginActivity.class));
    }
	
}