package paco.lugares.comer.opendata.chascarentenerife;

import static com.roscopeco.ormdroid.Query.eql;

import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;

import paco.lugares.comer.opendata.chascarentenerife.adapters.EstablecimientoAdapter;
import paco.lugares.comer.opendata.chascarentenerife.controllers.EstablecimientosController;
import paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities;
import paco.lugares.comer.opendata.chascarentenerife.models.Establecimiento;
import paco.lugares.comer.opendata.chascarentenerife.models.JSONToModel;
import paco.lugares.comer.opendata.chascarentenerife.models.Valoracion;
import paco.lugares.comer.opendata.chascarentenerife.models.ValoracionEstablecimiento;
import paco.lugares.comer.opendata.chascarentenerife.server.IStandardTaskListener;
import paco.lugares.comer.opendata.chascarentenerife.server.RequestArrayJSONResponse;
import paco.lugares.comer.opendata.chascarentenerife.server.RequestSimpleResponse;
import paco.lugares.comer.opendata.chascarentenerife.server.ServerConnection;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.ORMDroidApplication;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EstablecimientosMunicipio extends SherlockActivity {
	
	ListView listView1;
	TextView municipioNombre;
	ImageView municipioIcono;
	List<Establecimiento> establecimientos = null;
	ProgressDialog pd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_establecimientos_municipio);
		
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
		
        Bundle bundle = getIntent().getExtras();
		String municipio=bundle.getString("municipio");

		if (municipio == null)
			return;
        
		actualizarValoracion(this, municipio);
	}
	
	private void initLista(Activity activity, String municipio){

		listView1 = (ListView) findViewById(R.id.lista_establecimientos);
		municipioNombre = (TextView) findViewById(R.id.nombre);
		municipioIcono = (ImageView) findViewById(R.id.municipio);

		municipioNombre.setText(Utilities.getCamelCase(municipio));
		municipioIcono.setImageResource(Utilities.getIconoMunicipio(this, municipio));
		
		establecimientos = Entity.query(Establecimiento.class).where(eql("municipio", municipio)).orderBy("nombre").executeMulti();
		
		EstablecimientoAdapter adapter = new EstablecimientoAdapter(activity, establecimientos);
        listView1.setAdapter(adapter);
        
        listView1.setClickable(true);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Establecimiento establecimiento = (Establecimiento) listView1.getItemAtPosition(position);
				if (establecimiento != null){
					Bundle bundle = getIntent().getExtras();
	        		String municipio=bundle.getString("municipio");
					Intent myIntent = new Intent(view.getContext(), MapaEstablecimientos.class);
					myIntent.putExtra("latitud", establecimiento.latitud);
					myIntent.putExtra("longitud", establecimiento.longitud);
					myIntent.putExtra("tipo", establecimiento.tipo);
					myIntent.putExtra("idserver", establecimiento.idserver);
					myIntent.putExtra("nombre", establecimiento.nombre);
					myIntent.putExtra("municipio", municipio);
					Valoracion vE = ValoracionEstablecimiento.valoracion.get(establecimiento.idserver);
					myIntent.putExtra("media", ((vE != null) && (vE.media != null && (!vE.media.equals("0"))) ? vE.media : view.getContext().getResources().getString(R.string.valor_defecto)));
					myIntent.putExtra("precio", (vE != null) ? vE.precio : "0");
	        		startActivity(myIntent);
				}
			}
	    	
	    });
        
        if (pd != null)
        	pd.dismiss();

	}
	
	private void actualizarValoracion(Activity activity, String municipio){
		pd = ProgressDialog.show(this, getResources().getText(R.string.esperar), getResources().getText(R.string.comprobar_actualizaciones));
        pd.setIndeterminate(false);
        pd.setCancelable(true);
		
        if (!paco.lugares.comer.opendata.chascarentenerife.server.Utilities.haveInternet(this)){
			Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
			initLista(activity, municipio);
		} else {
			RequestArrayJSONResponse taskResquest = new RequestArrayJSONResponse();
			HttpGet get = ServerConnection.getGet(getResources().getString(R.string.ip_server), getResources().getString(R.string.port_server), "establecimientosGeo/"+municipio.replaceAll(" ", "_"));
			taskResquest.setParams(new ResponseServer_valoraciones_TaskListener(this, pd, municipio, activity), ServerConnection.getClient(), get);
			taskResquest.execute();	
		}
	}
	
	private class ResponseServer_valoraciones_TaskListener implements IStandardTaskListener {
	    
		Context context;
		ProgressDialog pd;
		String municipio;
		Activity activity;
		
	    public ResponseServer_valoraciones_TaskListener(Context context, ProgressDialog pd, String municipio, Activity activity) {
	    	this.context = context;
	    	this.pd = pd;
	    	this.municipio = municipio;
	    	this.activity = activity;
	    }
	    
	    @Override
	    public void taskComplete(Object result) {
	    	if (result != null){
	    		JSONArray responseServer = (JSONArray) result;
	    		ValoracionEstablecimiento.valoracion = new HashMap<String, Valoracion>();
	    		for(int i=0; i<responseServer.length(); i++){
	        		try {
	        			JSONToModel.toValoracionEstablecimientoModel(context, responseServer.getJSONObject(i));
	        		} catch (Exception e){}
	    		}
			}
			initLista(activity, municipio);
	    }
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_establecimientos_municipio, menu);
        return true;
    }
	
	@Override
	public void onResume() { 
	    super.onResume(); 
	    ORMDroidApplication.initialize(this);
	}
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(this, Principal.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.vermapa) {
        	if (establecimientos == null){
        		Toast.makeText(this, R.string.nomunicipios, Toast.LENGTH_LONG).show();
        	} else {
        		Intent myIntent = new Intent(this, MapaEstablecimientos.class);
        		Bundle bundle = getIntent().getExtras();
        		String municipio=bundle.getString("municipio");
        		myIntent.putExtra("municipio", municipio);
        		startActivity(myIntent);
        	}
        	return true;
        } else if (item.getItemId() == R.id.infoDetails) {
        	Intent myIntent = new Intent(this, Info.class);
            startActivity(myIntent);
        	return true;
        }
        return false;
    }
}
