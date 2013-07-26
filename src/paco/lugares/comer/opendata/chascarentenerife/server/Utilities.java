package paco.lugares.comer.opendata.chascarentenerife.server;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;

import com.roscopeco.ormdroid.Entity;

import paco.lugares.comer.opendata.chascarentenerife.R;
import paco.lugares.comer.opendata.chascarentenerife.controllers.EstablecimientosController;
import paco.lugares.comer.opendata.chascarentenerife.models.Version;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import static com.roscopeco.ormdroid.Query.eql;

public class Utilities {
	
	public static boolean firstTime=true;

	public static boolean haveInternet(Context ctx) { 

		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo(); 
		
		if (info == null || !info.isConnected()) { 
		    return false; 
		} 
		if (info.isRoaming()) { 
		    // here is the roaming option you can change it if you want to 
		    // disable internet while roaming, just return false 
		} 
		if (isConnectionFast(info.getType(), info.getSubtype()))
			return true;
		return false;
	}
	
	public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion 
             * to appropriate level to use these
             */
            case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11 
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                return false; // ~25 kbps 
            case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                return true; // ~ 10+ Mbps
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return false;
            }
        }else{
            return false;
        }
    }
	
	public static void comprobarActualizaciones(Context context){

        Version encontrado = Entity.query(Version.class).where(eql("zona", "Tenerife")).execute();
		String version="";
		if (encontrado == null){
			encontrado = new Version("0", "Tenerife");
			encontrado.save();
			version="0";
		} else {
			version=encontrado.version;
		}
        
		// Para no consultar siempre al servidor, solo una vez al mes
		Calendar limite = Calendar.getInstance(); 
		limite.setTime(new Date()); 
		limite.add(Calendar.MONTH, -1);
		
		Calendar actual = Calendar.getInstance();
		try {
		    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		    actual.setTime(sdf.parse(encontrado.fecha));// all done
		} catch (Exception e){
			actual.setTime(new Date());
		}
		
		if (version.equals("0")){
			ProgressDialog pd = ProgressDialog.show(context, context.getResources().getText(R.string.esperar), context.getResources().getText(R.string.comprobar_actualizaciones));
	        pd.setIndeterminate(false);
	        pd.setCancelable(false);
			
			
			RequestSimpleResponse taskResquest = new RequestSimpleResponse();
    		HttpGet get = ServerConnection.getGet(context.getResources().getString(R.string.ip_server), context.getResources().getString(R.string.port_server), "infoDatos/"+"Tenerife");
			taskResquest.setParams(new ResponseServer_versionDatos_TaskListener(context, pd), ServerConnection.getClient(), get);
			taskResquest.execute();	
		} else if (actual.getTime().before(limite.getTime())){
			if (!Utilities.haveInternet(context)){
				Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show();
				return;
			}
			
			ProgressDialog pd = ProgressDialog.show(context, context.getResources().getText(R.string.esperar), context.getResources().getText(R.string.comprobar_actualizaciones));
	        pd.setIndeterminate(false);
	        pd.setCancelable(false);
			
			EstablecimientosController.getNuevosDatos(context, "Tenerife", version, pd);
		}
	}
	
	private static class ResponseServer_versionDatos_TaskListener implements IStandardTaskListener {
	    
		Context context;
		ProgressDialog pd;
		
	    public ResponseServer_versionDatos_TaskListener(Context context, ProgressDialog pd) {
	    	this.context = context;
	    	this.pd = pd;
	    }
	    
	    @Override
	    public void taskComplete(Object result) {
	    	if (result != null){
	    		if (((String)result).equals("1")){
	    			pd.dismiss();
	    			JSONArray buffer = Utilities.parsearFicheroJSON(context, context.getResources().getString(R.string.ficheroinicial));
	    			
	    			pd = new ProgressDialog(context);
	        	    pd.setTitle(R.string.descargando);
	        	    pd.setMessage(context.getResources().getString(R.string.en_proceso));
	        	    pd.setIndeterminate(false);
	        	    pd.setMax(100);
	        	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        	    pd.setProgress(0);
	        	    pd.setCancelable(false);
	        	    pd.show();
	    			
	    			EstablecimientosController.DownloadInfo down = new EstablecimientosController.DownloadInfo();
	        	    down.setBuffer(buffer, context, pd, "Tenerife", true);
	        	    down.execute();
	    		} else {
	    			if (!Utilities.haveInternet(context)){
	    				pd.dismiss();
	    				Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show();
	    				return;
	    			}
	    			
	    			EstablecimientosController.getNuevosDatos(context, "Tenerife", "0", pd);
	    		}
	    		
			} else {
				pd.dismiss();
				JSONArray buffer = Utilities.parsearFicheroJSON(context, context.getResources().getString(R.string.ficheroinicial));
    			
    			pd = new ProgressDialog(context);
        	    pd.setTitle(R.string.descargando);
        	    pd.setMessage(context.getResources().getString(R.string.en_proceso));
        	    pd.setIndeterminate(false);
        	    pd.setMax(100);
        	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	    pd.setProgress(0);
        	    pd.setCancelable(false);
        	    pd.show();
    			
    			EstablecimientosController.DownloadInfo down = new EstablecimientosController.DownloadInfo();
        	    down.setBuffer(buffer, context, pd, "Tenerife", true);
        	    down.execute();
			}
	    }
	}
	
	private static String loadJSONFromAsset(Context ctx, String file) {
        String json = null;
        try {

            InputStream is = ctx.getAssets().open(file);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            return null;
        }
        return json;

    }
	
	public static JSONArray parsearFicheroJSON(Context ctx, String file){
		try {
			return new JSONArray(loadJSONFromAsset(ctx, file));
		} catch (Exception e) {
			return null;
		}	
	}

}
