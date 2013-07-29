package paco.lugares.comer.opendata.chascarentenerife;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import paco.lugares.comer.opendata.chascarentenerife.controllers.Utilities;
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
import com.roscopeco.ormdroid.ORMDroidApplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NuevoComentario extends SherlockActivity {
	
	String idserver, nombre, direccion, tipo;
	Spinner precioS;
	EditText nombreET, recomendacionET, opinionET;
	ImageView v1, v2, v3, v4, v5;
	int valoracion=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nuevo_comentario);
		
		ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
		
		Bundle bundle = getIntent().getExtras();
		idserver=bundle.getString("idserver");
		nombre=bundle.getString("nombre");
		tipo=bundle.getString("tipo");
		direccion=bundle.getString("direccion");
		
		initCabecera();
		initElementos();
	}
	
	public void initCabecera(){
		TextView nombreTV = (TextView) findViewById(R.id.nombre);
		TextView direccionTV = (TextView) findViewById(R.id.direccion);
		ImageView icono = (ImageView) findViewById(R.id.icon);
		
		nombreTV.setText(Utilities.getCamelCase(nombre));
		direccionTV.setText(Utilities.getCamelCase(direccion));
		icono.setImageResource(Utilities.getIconoTipo(this, tipo));
	}
	
	public void initElementos(){
		precioS = (Spinner) findViewById(R.id.precio);
		List<String> precios = new ArrayList<String>();
		precios.add(getResources().getString(R.string.barato));
		precios.add(getResources().getString(R.string.normal));
		precios.add(getResources().getString(R.string.carillo));
		precios.add(getResources().getString(R.string.caro));
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, precios);
		precioS.setAdapter(spinnerArrayAdapter);
		
		v1 = (ImageView) findViewById(R.id.v1);
		v2 = (ImageView) findViewById(R.id.v2);
		v3 = (ImageView) findViewById(R.id.v3);
		v4 = (ImageView) findViewById(R.id.v4);
		v5 = (ImageView) findViewById(R.id.v5);
		
		initValoracion();
		
		nombreET = (EditText) findViewById(R.id.nombre_usuario);
		recomendacionET = (EditText) findViewById(R.id.recomendacion);
		opinionET = (EditText) findViewById(R.id.opinion);
	}

	public void initValoracion(){
		
		v1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				valoracion=1;
				setValoracion(true, false, false, false, false);
			}
		});
		
		v2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				valoracion=2;
				setValoracion(false, true, false, false, false);
			}
		});

		v3.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				valoracion=3;
				setValoracion(false, false, true, false, false);
			}
		});

		v4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				valoracion=4;
				setValoracion(false, false, false, true, false);
			}
		});

		v5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				valoracion=5;
				setValoracion(false, false, false, false, true);
			}
		});
		
	}
	
	public void setValoracion(boolean uno, boolean dos, boolean tres, boolean cuatro, boolean cinco){
		if (uno){
			v1.setImageResource(R.drawable.ic_launcher);
			v2.setImageResource(R.drawable.staroff);
			v3.setImageResource(R.drawable.staroff);
			v4.setImageResource(R.drawable.staroff);
			v5.setImageResource(R.drawable.staroff);
		} else if (dos){
			v1.setImageResource(R.drawable.ic_launcher);
			v2.setImageResource(R.drawable.ic_launcher);
			v3.setImageResource(R.drawable.staroff);
			v4.setImageResource(R.drawable.staroff);
			v5.setImageResource(R.drawable.staroff);
		} else if (tres){
			v1.setImageResource(R.drawable.ic_launcher);
			v2.setImageResource(R.drawable.ic_launcher);
			v3.setImageResource(R.drawable.ic_launcher);
			v4.setImageResource(R.drawable.staroff);
			v5.setImageResource(R.drawable.staroff);
		} else if (cuatro){
			v1.setImageResource(R.drawable.ic_launcher);
			v2.setImageResource(R.drawable.ic_launcher);
			v3.setImageResource(R.drawable.ic_launcher);
			v4.setImageResource(R.drawable.ic_launcher);
			v5.setImageResource(R.drawable.staroff);
		} else if (cinco){
			v1.setImageResource(R.drawable.ic_launcher);
			v2.setImageResource(R.drawable.ic_launcher);
			v3.setImageResource(R.drawable.ic_launcher);
			v4.setImageResource(R.drawable.ic_launcher);
			v5.setImageResource(R.drawable.ic_launcher);
		}
	}
	
	@Override
	public void onResume() { 
	    super.onResume(); 
	    ORMDroidApplication.initialize(this);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_nuevo_comentario, menu);
        return true;
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
        } else if (item.getItemId() == R.id.guardar) {
        	
        	if (comprobarCampos()){
        		if (idserver != null){
	        		ProgressDialog pd = ProgressDialog.show(this, getResources().getText(R.string.esperar), getResources().getText(R.string.enviando_comentario));
	                pd.setIndeterminate(false);
	                pd.setCancelable(true);
	        		
	        		RequestSimpleResponse taskResquest = new RequestSimpleResponse();
	        		
	        		JSONObject data = new JSONObject();
	        		try {
		        		data.put("opinion", opinionET.getText().toString());
						data.put("recomendacion", recomendacionET.getText().toString());
						data.put("precio", (precioS.getSelectedItemPosition()+1));
						data.put("usuario", nombreET.getText().toString());
						data.put("valoracion", valoracion);
						data.put("fecha", new Date());
						StringEntity body = new StringEntity(data.toString(), "UTF-8");
						HttpPost post = ServerConnection.getPost(getResources().getString(R.string.ip_server), getResources().getString(R.string.port_server), "comentario/"+idserver, body);
		    			taskResquest.setParams(new ResponseServer_comentario_TaskListener(this, pd), ServerConnection.getClient(), post);
		    			taskResquest.execute();	
	        		} catch (Exception e){
						pd.dismiss();
					}

        		} else {
        			Toast.makeText(this, R.string.nomunicipios, Toast.LENGTH_LONG).show();
        		}
        	}
        	
            
        }
        return false;
    }
    
    class ResponseServer_comentario_TaskListener implements IStandardTaskListener {
	    
		Context context;
		ProgressDialog pd;
		
	    public ResponseServer_comentario_TaskListener(Context context, ProgressDialog pd) {
	    	this.context = context;
	    	this.pd = pd;
	    }
	    
	    @Override
	    public void taskComplete(Object result) {
	    	if (result != null){
	    		String responseServer = (String) result;
	    		if ((result != null) && (result.equals("ok"))){
	    			pd.dismiss();
		    		Intent myIntent = new Intent(context, Principal.class);
		    		myIntent.putExtra("guardadoComentario", true);
		            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		            startActivity(myIntent);
		            finish();
	    		} else {
	    			pd.dismiss();
	    			Toast.makeText(context, R.string.fall_comentario, Toast.LENGTH_LONG).show();
	    		}
			} else {
				pd.dismiss();
			}
	    }
	}
    
    public boolean comprobarCampos(){
    	if (nombreET.getText().toString().equals("")){
    		Toast.makeText(this, R.string.faltanombre, Toast.LENGTH_LONG).show();
    		return false;
    	} else if (valoracion == 0){
    		Toast.makeText(this, R.string.faltavaloracion, Toast.LENGTH_LONG).show();
    		return false;
    	}
    	return true;
    }

}
