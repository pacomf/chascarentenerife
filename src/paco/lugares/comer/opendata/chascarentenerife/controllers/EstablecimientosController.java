package paco.lugares.comer.opendata.chascarentenerife.controllers;

import static com.roscopeco.ormdroid.Query.and;
import static com.roscopeco.ormdroid.Query.eql;
import static com.roscopeco.ormdroid.Query.geq;
import static com.roscopeco.ormdroid.Query.leq;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;

import com.roscopeco.ormdroid.Entity;

import paco.lugares.comer.opendata.chascarentenerife.R;
import paco.lugares.comer.opendata.chascarentenerife.models.Establecimiento;
import paco.lugares.comer.opendata.chascarentenerife.models.JSONToModel;
import paco.lugares.comer.opendata.chascarentenerife.models.Version;
import paco.lugares.comer.opendata.chascarentenerife.server.IStandardTaskListener;
import paco.lugares.comer.opendata.chascarentenerife.server.RequestArrayJSONResponse;
import paco.lugares.comer.opendata.chascarentenerife.server.RequestSimpleResponse;
import paco.lugares.comer.opendata.chascarentenerife.server.ServerConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


public class EstablecimientosController {
	
	public static void getNuevosDatos (Context context, String zona, String version, ProgressDialog pd){
		HttpGet get = ServerConnection.getGet(context.getResources().getString(R.string.ip_server), context.getResources().getString(R.string.port_server), "actualizarDatosGeo/"+zona+"/"+version);
		RequestArrayJSONResponse taskResquest = new RequestArrayJSONResponse();
		taskResquest.setParams(new ResponseServer_actualizarDatos_TaskListener(context, zona, pd), ServerConnection.getClient(), get);
		taskResquest.execute();
	}
	
	private static class ResponseServer_actualizarDatos_TaskListener implements paco.lugares.comer.opendata.chascarentenerife.server.IStandardTaskListener {
        
    	Context context;
    	String zona;
    	ProgressDialog pd;
    	
        public ResponseServer_actualizarDatos_TaskListener(Context ctx, String zona, ProgressDialog pd) {
        	this.context = ctx;
        	this.zona = zona;
        	this.pd = pd;
        }
        
        @Override
        public void taskComplete(Object result) {
        	if (result != null){
        		JSONArray responseServer = (JSONArray) result;
        		pd.dismiss();
        		if (responseServer.length()>0){

	        		pd = new ProgressDialog(context);
	        	    pd.setTitle(R.string.descargando);
	        	    pd.setMessage(context.getResources().getString(R.string.en_proceso));
	        	    pd.setIndeterminate(false);
	        	    pd.setMax(100);
	        	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        	    pd.setProgress(0);
	        	    pd.setCancelable(false);
	        	    pd.show();
	        	    
	        	    DownloadInfo down = new DownloadInfo();
	        	    down.setBuffer(responseServer, context, pd, zona, false);
	        	    down.execute();
        		}

			} else {
				pd.dismiss();
				Toast.makeText(context, R.string.excepcion_parsear_json, Toast.LENGTH_LONG).show();
			}
        }
	}
	
	private static class ResponseServer_version_TaskListener implements IStandardTaskListener {
	    
		Context context;
		ProgressDialog pd;
		
	    public ResponseServer_version_TaskListener(Context context, ProgressDialog pd) {
	    	this.context = context;
	    	this.pd = pd;
	    }
	    
	    @Override
	    public void taskComplete(Object result) {
	    	if (result != null){
	    		Version encontrado = Entity.query(Version.class).where(eql("zona", "Tenerife")).execute();
	    		if (encontrado == null){
	    			Version nuevaVersion = new Version((String)result, "Tenerife");
	    			nuevaVersion.save();
	    		} else {
	    			encontrado.version = (String)result;
	    			encontrado.save();
	    		}
			} else {
				Toast.makeText(context, R.string.excepcion_version, Toast.LENGTH_LONG).show();
			}
	    	pd.dismiss();
	    }
	}
	
	public static class DownloadInfo extends AsyncTask<String , String , Void>{

	    JSONArray buffer;
	    Context context;
	    ProgressDialog pd;
	    String zona;
	    int porcentaje=0;
	    int fallos=0;
	    boolean fich=false;
	    
	    public void setBuffer(JSONArray buffer, Context context, ProgressDialog pd, String zona, boolean fich){
	    	this.buffer = buffer;
	    	this.context = context;
	    	this.pd = pd;
	    	this.zona = zona;
	    	this.fich = fich;
	    }

	    @Override
	    protected Void doInBackground(String... params) {

	        try {
	        	for(int i=0; i<buffer.length(); i++){
	        		try {
	        			Establecimiento establecimiento = JSONToModel.toEstablecimientoModel(context, buffer.getJSONObject(i));
	        			Establecimiento encontrado = Entity.query(Establecimiento.class).where(eql("idserver", establecimiento.idserver)).execute();
	        			if (encontrado == null){
	        				establecimiento.save();
	        			} else {
	        				encontrado.actualizar(establecimiento.nombre, establecimiento.tipo, establecimiento.direccion, establecimiento.numero, establecimiento.cp, establecimiento.latitud, establecimiento.longitud, establecimiento.municipio, establecimiento.plazas);
	        				encontrado.save();
	        			}
	        			porcentaje=(int)((i*100)/buffer.length());
	        			publishProgress("" + porcentaje);
	        		} catch (Exception e){
	        			fallos++;
	        		}
	        	}
	                    

	        } catch (Exception e) {}
	        return null;
	    }

	    protected void onProgressUpdate(String... progress) {
	        super.onProgressUpdate(progress);         
	        pd.setProgress(porcentaje);
	        pd.setMessage(context.getResources().getString(R.string.en_proceso) + " " + porcentaje + "%");      
	    }

	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);
	        pd.dismiss();
	        if ((buffer.length()>0) && (fallos < buffer.length()/4)){
	        	pd = ProgressDialog.show(context, context.getResources().getText(R.string.esperar), context.getResources().getText(R.string.comprobar_actualizaciones));
		        pd.setIndeterminate(false);
		        pd.setCancelable(false);
		        
		        if (!fich){
		        	RequestSimpleResponse taskResquest = new RequestSimpleResponse();
	        		HttpGet get = ServerConnection.getGet(context.getResources().getString(R.string.ip_server), context.getResources().getString(R.string.port_server), "infoDatos/"+zona);
					taskResquest.setParams(new ResponseServer_version_TaskListener(context, pd), ServerConnection.getClient(), get);
					taskResquest.execute();
		        } else{
		        	Version encontrado = Entity.query(Version.class).where(eql("zona", "Tenerife")).execute();
		    		if (encontrado == null){
		    			Version nuevaVersion = new Version("1", "Tenerife");
		    			nuevaVersion.save();
		    		} else {
		    			encontrado.version = "1";
		    			encontrado.save();
		    		}
		    		pd.dismiss();
		        }
        	} else {
        		Toast.makeText(context, R.string.fallos, Toast.LENGTH_LONG).show();
        	}
	    }

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	}
	
	//Radio en Metros
	public static List<Establecimiento> getCercanos(long latitud, long longitud, Double radio){
		ArrayList<Long> dimensiones = Utilities.getXMeterAreaToPoint(latitud, longitud, radio);
		if (dimensiones.size()>0){
			List<Establecimiento> ret = Entity.query(Establecimiento.class).where(and(geq("longitud", dimensiones.get(3)), leq("longitud", dimensiones.get(1)), geq("latitud", dimensiones.get(2)), leq("latitud", dimensiones.get(0)))).executeMulti();
			return ret;
		} else
			return new ArrayList<Establecimiento>();
	}

}
