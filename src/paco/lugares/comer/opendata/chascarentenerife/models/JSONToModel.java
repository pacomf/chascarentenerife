package paco.lugares.comer.opendata.chascarentenerife.models;

import org.json.JSONObject;

import paco.lugares.comer.opendata.chascarentenerife.R;

import android.content.Context;
import android.widget.Toast;

public class JSONToModel {

	public static Establecimiento toEstablecimientoModel (Context ctx, JSONObject json){
		try {
			String nombre = json.getString("nombre");
			String tipo = json.getString("tipo");
			String direccion = json.getString("direccion");
			String numero = json.getString("numero");
			String idserver = json.getString("_id");
			String cp = json.getString("cp");
			String latitud = json.getString("latitud");
			String longitud = json.getString("longitud");
			String municipio = json.getString("municipio");
			String plazas = json.getString("plazas");
			Establecimiento establecimiento = new Establecimiento(idserver, nombre, tipo, direccion, numero, cp, latitud, longitud, municipio, plazas);
			return establecimiento;
		} catch (Exception e) {
			Toast.makeText(ctx, R.string.excepcion_convertir_json, Toast.LENGTH_LONG).show();
			return null;
		}	
	}
	
	public static void toValoracionEstablecimientoModel (Context ctx, JSONObject json){
		try {
			String idserver = json.getString("idserver");
			String media = json.getString("media");
			String precio = json.getString("precio");
			ValoracionEstablecimiento.valoracion.put(idserver, new Valoracion(precio, media));
		} catch (Exception e) {
			Toast.makeText(ctx, R.string.excepcion_convertir_json, Toast.LENGTH_LONG).show();
			return;
		}	
	}
	
	public static Comentario toComentarioModel (Context ctx, JSONObject json){
		try {
			String usuario = json.getString("usuario");
			String fecha = json.getString("fecha");
			String valoracion = json.getString("valoracion");
			String precio = json.getString("precio");
			String recomendacion = json.getString("recomendacion");
			String opinion = json.getString("opinion");
			Comentario comentario = new Comentario(usuario, fecha, valoracion, precio, recomendacion, opinion);
			return comentario;
		} catch (Exception e) {
			Toast.makeText(ctx, R.string.excepcion_convertir_json, Toast.LENGTH_LONG).show();
			return null;
		}	
	}

}
