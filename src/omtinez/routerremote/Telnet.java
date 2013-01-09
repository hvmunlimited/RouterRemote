package omtinez.routerremote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.widget.Toast;

public class Telnet {
	
	static Telnet mThis;
	
	Socket socket;
	BufferedReader r;
	PrintWriter w;
	
	public static Telnet getInstance() {
		if (mThis == null) {
			mThis = new Telnet();
		}
		return mThis;
	}
	
	// allow instantiation
	public Telnet() {}
	
    public void init(final Activity activity, String ip, int port) {
    	try {
			// socket
			socket = new Socket(ip,port);
			socket.setKeepAlive(true);
			socket.setSoTimeout(3000);
			
			// I/O
			r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			w = new PrintWriter(socket.getOutputStream(),true);
			
		} catch (Exception e) {
			final String error_message = e.getMessage();
			activity.runOnUiThread(new Runnable() { public void run() {
				Toast.makeText(activity.getBaseContext(), error_message, Toast.LENGTH_SHORT).show();
			}});
		}
	}
    
    public boolean login(String user, String pwd) {
    	try {
			String login1 = read(r);
			w.print(user + "\r\n");
			w.flush();
			String login2 = read(r);
	    	w.print(pwd + "\r\n");
	    	w.flush();
	    	String enter = read(r);
	    	
	    	// to tell whether we logged in successfully, compare the last 5 characters of output
	    	if (enter.length() < 5) {
	    		return true;
	    	} else if (enter.substring(enter.length() - 5).matches(login1.substring(login1.length()-5)) 
	    	|| enter.substring(enter.length() - 5).matches(login2.substring(login2.length() - 5))) {
	    		return false;
	    	} else {
	    		return true;
	    	}
	    	
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public String command(String cmd) {
    	StringBuffer sb = new StringBuffer();
    	try {
    		for(String x : cmd.split(";")) {
	    		w.print(x + "\r\n");
	    		w.flush();
	    		Thread.sleep(1000);
    		}
    		while (r.ready()) {
    			sb.append((char) r.read());
    		}
		} catch (Exception e) {
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