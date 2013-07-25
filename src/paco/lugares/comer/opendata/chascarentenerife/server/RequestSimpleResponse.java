package paco.lugares.comer.opendata.chascarentenerife.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpUriRequest;

import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class RequestSimpleResponse extends AsyncTask<String, Void, Boolean>{
	
	private IStandardTaskListener listener;
	private HttpClient httpClient;
	private HttpUriRequest method;
	private String responseServer;

	@Override
	// Se ejecuta en segundo plano
	protected Boolean doInBackground(String... args) {
		try {
			responseServer=null;
	        HttpResponse resp = httpClient.execute(method);
	        try {
	        	responseServer = EntityUtils.toString(resp.getEntity());
	        } catch (Exception e) {}
	        return true;
		 } catch (Exception e) {
			Log.e("ServicioRest","Error en los preparativos!", e);
	        return false;
		 }
	}

	// Este metodo se ejecuta cuando el 'doInBackground' ha terminado, y recibe como parametro
	// lo devuelto en el 'doInBackground'
	@Override
	protected void onPostExecute(Boolean result){
        if (listener != null) {
        	listener.taskComplete(responseServer);
        }
	}
	
	public void setParams(IStandardTaskListener listener, HttpClient httpClient, HttpUriRequest method) {
         this.listener = listener;
         this.httpClient = httpClient;
         this.method = method;
	}

}
