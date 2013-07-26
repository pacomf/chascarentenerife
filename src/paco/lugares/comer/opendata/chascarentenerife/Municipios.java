package paco.lugares.comer.opendata.chascarentenerife;

import java.util.Locale;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.roscopeco.ormdroid.ORMDroidApplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Municipios extends SherlockActivity {

	ListView listView1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_municipios);
		
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
		
		initLista();
	}
	
	@Override
	public void onResume() { 
	    super.onResume(); 
	    ORMDroidApplication.initialize(this);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_solo_info, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(this, Principal.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.infoDetails) {
        	Intent myIntent = new Intent(this, Info.class);
            startActivity(myIntent);
        	return true;
        }
        return false;
    }
	
	private void initLista(){
		listView1 = (ListView) findViewById(R.id.lista);
		
		String[] items = getResources().getStringArray(R.array.lista_municipios);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        
        listView1.setAdapter(adapter);
        
        listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				 String item = ((TextView)view).getText().toString().toUpperCase();
				 Intent myIntent = new Intent(view.getContext(), EstablecimientosMunicipio.class);
     			 myIntent.putExtra("municipio", item);
     			 startActivity(myIntent);
				
			}
		});
	}

}
