package paco.lugares.comer.opendata.chascarentenerife.server;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class ServerConnection {
	
	public static HttpClient getClient(){
		
		int timeoutConnection=7000;
		int timeoutSocket=5000;
		
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		return new DefaultHttpClient(httpParameters);
	}
	
	public static HttpPost getPost(String url, String port, String webService, StringEntity body){
		HttpPost post = new HttpPost(url+":"+port+"/"+webService);
		post.setHeader("content-type", "application/json");
		if(body != null)
			post.setEntity(body);
		return post;
	}
	
	public static HttpGet getGet(String url, String port, String webService){
		HttpGet get = new HttpGet(url+":"+port+"/"+webService);
		get.setHeader("content-type", "application/json");
		return get;
	}
	
	public static HttpPut getPut(String url, String port, String webService, StringEntity body){
		HttpPut put = new HttpPut(url+":"+port+"/"+webService);
		put.setHeader("content-type", "application/json");
		if(body != null)
			put.setEntity(body);
		return put;
	}
	
	public static HttpDelete getDelete(String url, String port, String webService){
		HttpDelete delete = new HttpDelete(url+":"+port+"/"+webService);
		delete.setHeader("content-type", "application/json");
		return delete;
	}

}
