package paco.lugares.comer.opendata.chascarentenerife;

import static com.roscopeco.ormdroid.Query.eql;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;

import paco.lugares.comer.opendata.chascarentenerife.adapters.ComentarioAdapter;
import paco.lugares.comer.opendata.chascarentenerife.adapters.EstablecimientoAdapter;
import paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities;
import paco.lugares.comer.opendata.chascarentenerife.models.Comentario;
import paco.lugares.comer.opendata.chascarentenerife.models.Establecimiento;
import paco.lugares.comer.opendata.chascarentenerife.models.JSONToModel;
import paco.lugares.comer.opendata.chascarentenerife.models.ValoracionEstablecimiento;
import paco.lugares.comer.opendata.chascarentenerife.server.IStandardTaskListener;
import paco.lugares.comer.opendata.chascarentenerife.server.RequestArrayJSONResponse;
import paco.lugares.comer.opendata.chascarentenerife.server.ServerConnection;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.SupportMapFragment;
import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.ORMDroidApplication;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ComentariosEstablecimiento extends SherlockActivity {
	
	ListView listView1;
	TextView nombre, direccion, precio;
	ImageView icono, v1, v2, v3, v4, v5;
	List<Comentario> comentarios = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comentarios_establecimiento);
		
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);

		
		Bundle bundle = getIntent().getExtras();
		String idserver=bundle.getString("idserver");
		String nombreStr=bundle.getString("nombre");
		String mediaStr=bundle.getString("media");
		String precioStr=bundle.getString("precio");
		String tipoStr=bundle.getString("tipo");
		String direccionStr=bundle.getString("direccion");

		listView1 = (ListView) findViewById(R.id.lista_comentarios);
		
		initCabecera(nombreStr, direccionStr, tipoStr, mediaStr, precioStr, idserver);
	}
	
	private void initCabecera (String nombreStr, String direccionStr, String tipoStr, String mediaStr, String precioStr, String idserver){
		
		nombre = (TextView) findViewById(R.id.nombre);
		direccion = (TextView) findViewById(R.id.direccion);
		precio = (TextView) findViewById(R.id.precio);
		icono = (ImageView) findViewById(R.id.icon);
		v1 = (ImageView) findViewById(R.id.v1);
		v2 = (ImageView) findViewById(R.id.v2);
		v3 = (ImageView) findViewById(R.id.v3);
		v4 = (ImageView) findViewById(R.id.v4);
		v5 = (ImageView) findViewById(R.id.v5);
		

		nombre.setText(Utilities.getCamelCase(nombreStr));
		direccion.setText(Utilities.getCamelCase(direccionStr));
		precioStr = (precioStr != null) ? precioStr : "0";
		precio.setText(Utilities.getPrecioStr(this, precioStr));
		
	    if (precioStr.equals("1")){
	    	precio.setTextColor(Color.GREEN);
	    } else if (precioStr.equals("2")){
	    	precio.setTextColor(Color.BLUE);
	    } else if (precioStr.equals("3")){
	    	precio.setTextColor(Color.YELLOW);
	    } else if (precioStr.equals("4")){
	    	precio.setTextColor(Color.RED);
	    }
		
		icono.setImageResource(Utilities.getIconoTipo(this, tipoStr));
		
		ProgressDialog pd = ProgressDialog.show(this, getResources().getText(R.string.esperar), getResources().getText(R.string.cargando_comentarios));
        pd.setIndeterminate(false);
        pd.setCancelable(true);
		
        if (!paco.lugares.comer.opendata.chascarentenerife.server.Utilities.haveInternet(this)){
        	pd.dismiss();
			Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
		} else {
			RequestArrayJSONResponse taskResquest = new RequestArrayJSONResponse();
			HttpGet get = ServerConnection.getGet(getResources().getString(R.string.ip_server), getResources().getString(R.string.port_server), "comentarios/"+idserver);
			taskResquest.setParams(new ResponseServer_comentarios_TaskListener(this, pd, this), ServerConnection.getClient(), get);
			taskResquest.execute();	
		}
		
		
	}
	
	private class ResponseServer_comentarios_TaskListener implements IStandardTaskListener {
	    
		Context context;
		ProgressDialog pd;
		Activity activity;
		
	    public ResponseServer_comentarios_TaskListener(Context context, ProgressDialog pd, Activity activity) {
	    	this.context = context;
	    	this.pd = pd;
	    	this.activity = activity;
	    }
	    
	    @Override
	    public void taskComplete(Object result) {
	    	comentarios = new ArrayList<Comentario>();
	    	if (result != null){
	    		JSONArray responseServer = (JSONArray) result;
	    		for(int i=0; i<responseServer.length(); i++){
	        		try {
	        			comentarios.add(JSONToModel.toComentarioModel(context, responseServer.getJSONObject(i)));
	        		} catch (Exception e){}
	    		}
	    		ComentarioAdapter adapter = new ComentarioAdapter(activity, comentarios);
		        listView1.setAdapter(adapter);
			} else { // TODO: Quitar esto!!!
				comentarios.add(new Comentario("Paco", "14/08/2008", "4", "1", "El Pulpo a la gallega y el salpicón de atún", "Espectacular lugar donde los haya, bueno, bonito y baratao. Cumple con las expectatvivas y es un clásico."));
				comentarios.add(new Comentario("Paco Martin", "14/09/2008", "2", "4", "El Pulpo a la gallega y el salpicón de atún", "Espectacular lugar donde los haya, bueno, bonito y baratao. Cumple con las expectatvivas y es un clásico."));
				ComentarioAdapter adapter = new ComentarioAdapter(activity, comentarios);
		        listView1.setAdapter(adapter);
			}
	    	pd.dismiss();
	    }
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        // TODO: Cambiar MENU
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
    	// TODO: Rellenar Opciones de MENU
    	if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(this, Principal.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            finish();
            return true;
        } 
        return false;
    }

}
