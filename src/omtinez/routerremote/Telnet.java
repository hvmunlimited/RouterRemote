package omtinez.routerremote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Telnet extends Activity {
	CommandsDB db = null;
	Socket socket = null;
	BufferedReader r = null;
	PrintWriter w = null;
	
	int port = 0;
	String user = null, pwd = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final EditText iptext =  (EditText)findViewById(R.id.ip);
        final EditText porttext = (EditText)findViewById(R.id.port);
        final EditText usertext =  (EditText)findViewById(R.id.user);
        final EditText pwdtext =  (EditText)findViewById(R.id.pwd);
        final CheckBox rembox = (CheckBox)findViewById(R.id.rembox);
        
        // get saved credentials
        db = new CommandsDB(Telnet.this);
        porttext.setText(Integer.toString(db.getPort()));
        usertext.setText(db.getUser());
        pwdtext.setText(db.getPwd());
        
        

        
        Button connectb = (Button)findViewById(R.id.connectb);
        connectb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				// retrieve the values
				String ip = iptext.getText().toString();
				try {
					port = Integer.parseInt(porttext.getText().toString().trim());
				} catch (NumberFormatException nfe) {
					Toast.makeText(Telnet.this, "Selecter port is not a number", Toast.LENGTH_LONG).show();
				} finally {
					user = usertext.getText().toString().trim();
					pwd = pwdtext.getText().toString().trim();
					
					// save credentials if remember is checked
					if (rembox.isChecked()) db.save(user,pwd,port);
					
					// create intent and prepare data to be passed 
					Intent i = new Intent(Telnet.this, Actions.class);
					i.putExtra("omtinez.Telnet.user", user);
					i.putExtra("omtinez.Telnet.pwd", pwd);
					i.putExtra("omtinez.Telnet.ip", ip);
					i.putExtra("omtinez.Telnet.port", port);
					startActivity(i);
				}
			}
        });
    }
    
    public Telnet() {}
    public Telnet(Context context, String ip, int port) {
    	try {
			// socket
			socket = new Socket(ip,port);
			socket.setKeepAlive(true);
			socket.setSoTimeout(3000);
			// I/O
			r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			w = new PrintWriter(socket.getOutputStream(),true);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Toast.makeText(context, "Error connecting to router, unknown host exception", Toast.LENGTH_SHORT).show();
		} catch (SocketException e) {
			e.printStackTrace();
			Toast.makeText(context, "Error connecting to router, socket exception", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(context, "Error connecting to router, IO exception", Toast.LENGTH_SHORT).show();
		}
	}
    
    public boolean login(String user, String pwd) {
    	try {
			String login1 = read(r);
			w.print(user+"\r\n");
			w.flush();
			String login2 = read(r);
	    	w.print(user+"\r\n");
	    	w.flush();
	    	String enter = read(r);
	    	// to tell whether we logged in succesfully, compare the last 5 characters of output
	    	if(enter.length() < 5) return true;
	    	else if(enter.substring(enter.length()-5).matches(login1.substring(login1.length()-5)) || enter.substring(enter.length()-5).matches(login2.substring(login2.length()-5))) return false;
	    	else return true;
	    	
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public String command(String cmd) {
    	StringBuffer sb = new StringBuffer();
    	try {
    		for(String x : cmd.split(";")) {
	    		w.print(x+"\r\n");
	    		w.flush();
	    		Thread.sleep(1000);
    		}
    		for (int c=0; c != -1; c = r.read())
    			sb.append((char)c);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return sb.toString();
    }
	
	public static String read(BufferedReader r) throws IOException {
		StringBuffer sb = new StringBuffer();
		for (int c=0; c != -1; c = r.read()) {
	    	sb.append((char)c);
	    	if((char)c == ':' || (char)c == '>') break;
		}
		return sb.toString();
    }
}