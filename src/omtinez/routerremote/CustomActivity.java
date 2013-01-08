package omtinez.routerremote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class CustomActivity extends SherlockActivity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom);
        
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
        
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
				db.insert("commands", name, cmd, R.drawable.exec_custom, type);
				Intent i = new Intent(CustomActivity.this, ListActivity.class);
				startActivity(i);
			}
        });
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
