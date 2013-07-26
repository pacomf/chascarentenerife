package paco.lugares.comer.opendata.chascarentenerife;

import org.json.JSONArray;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.roscopeco.ormdroid.ORMDroidApplication;

import paco.lugares.comer.opendata.chascarentenerife.models.Establecimiento;
import paco.lugares.comer.opendata.chascarentenerife.server.Utilities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Principal extends SherlockFragmentActivity {
	
	Button alrededor, municipios;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Configuracion BBDD
		ORMDroidApplication.initialize(this);
		
		setContentView(R.layout.activity_principal);
		
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);		

        if (Utilities.firstTime){
        	Utilities.comprobarActualizaciones(this);
        	Utilities.firstTime=false;
        }
		
		alrededor = (Button) findViewById(R.id.botonalrededor);
		municipios = (Button) findViewById(R.id.botonmunicipio);
		
		initBotonAlrededor();
		initBotonMunicipios();

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
	public void finish(){
		Utilities.firstTime=true;
	    super.finish();
	}
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.infoDetails) {
        	Intent myIntent = new Intent(this, Info.class);
            startActivity(myIntent);
        	return true;
        }
        return false;
    }
	
	private void initBotonAlrededor(){
		alrededor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Alrededor.class);
    			startActivity(myIntent);
			}
		});
	}
	
	private void initBotonMunicipios(){
		municipios.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Municipios.class);
    			startActivity(myIntent);
			}
		});
	}

}
