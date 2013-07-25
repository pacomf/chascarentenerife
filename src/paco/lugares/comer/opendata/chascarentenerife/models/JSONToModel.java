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

}
