package omtinez.routerremote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class Custom extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom);
        
        final CommandsDB db = new CommandsDB(this.getApplicationContext());
        final EditText cmdtext = (EditText)findViewById(R.id.cmd);
        final EditText nametext = (EditText)findViewById(R.id.cmd);
        final RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup1);
        
        Button addb = (Button)findViewById(R.id.addb);
        addb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// retrieve the values
				String cmd = cmdtext.getText().toString();
				String name = nametext.getText().toString();
				int id = radiogroup.getCheckedRadioButtonId();
				int type = id == R.id.radio0 ? 0 : (id == R.id.radio1 ? 1 : 2);
				db.insert("zyxel",name, cmd, type);
				Intent i = new Intent(Custom.this, Actions.class);
				startActivity(i);
			}
        });
    }
}
