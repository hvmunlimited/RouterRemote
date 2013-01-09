package omtinez.routerremote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class LoginActivity extends SherlockActivity {
	CommandsDB db;
	Telnet telnet;
	Activity activity;
	
	int port;
	String ip;
	String user;
	String pwd;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // get telnet class instance
        telnet = Telnet.getInstance();
        activity = this;
        
        final EditText iptext =  (EditText)findViewById(R.id.ip);
        final EditText porttext = (EditText)findViewById(R.id.port);
        final EditText usertext =  (EditText)findViewById(R.id.user);
        final EditText pwdtext =  (EditText)findViewById(R.id.pwd);
        final CheckBox rembox = (CheckBox)findViewById(R.id.rembox);
        
        // get saved credentials
        db = new CommandsDB(LoginActivity.this);
        porttext.setText(Integer.toString(db.getPort()));
        usertext.setText(db.getUser());
        pwdtext.setText(db.getPwd());
        if (!usertext.getText().equals("1234") || !pwdtext.getText().equals("1234")) {
        	rembox.setChecked(true);
        } else {
        	rembox.setChecked(false);
        }
        
        
        Button connectb = (Button)findViewById(R.id.connectb);
        connectb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				// retrieve the values
				ip = iptext.getText().toString();
				try {
					port = Integer.parseInt(porttext.getText().toString().trim());
				} catch (NumberFormatException nfe) {
					Toast.makeText(LoginActivity.this, "Selected port is not a number", Toast.LENGTH_LONG).show();
				} finally {
					user = usertext.getText().toString().trim();
					pwd = pwdtext.getText().toString().trim();
					
					// save credentials if remember is checked
					if (rembox.isChecked()) db.save(user,pwd,port);
					
					// try to connect
					new Thread() { public void run() {
						telnet.init(activity, ip, port);
						if(telnet.login(user, pwd)) {
							startActivity(new Intent(LoginActivity.this, MenuActivity.class));
						} else {
							activity.runOnUiThread(new Runnable() { public void run() {
								Toast.makeText(activity.getBaseContext(), "Unable to log in", Toast.LENGTH_LONG).show();
							}});
						}
					}}.start();
				}
			}
        });
    }
    
    @Override
    public void onBackPressed() {
       Intent setIntent = new Intent(Intent.ACTION_MAIN);
       setIntent.addCategory(Intent.CATEGORY_HOME);
       setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       startActivity(setIntent);
    }
}