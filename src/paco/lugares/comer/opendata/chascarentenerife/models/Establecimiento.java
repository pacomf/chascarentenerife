package paco.lugares.comer.opendata.chascarentenerife.models;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Entity;
import static com.roscopeco.ormdroid.Query.eql;

public class Establecimiento extends Entity{

	  @Column(primaryKey=true)
	  public int id;
	  public String idserver, nombre, tipo, direccion, numero, cp, municipio, plazas;
	  public long latitud, longitud;
	  
	  public Establecimiento(){
	     this(null, null, null, null, null, null, null, null, null, null);
      }
	  
	  public Establecimiento(String idserver, String nombre, String tipo, String direccion, String numero, String cp, String latitud, String longitud, String municipio, String plazas){
		  this.idserver = idserver;
		  this.nombre = nombre;
		  this.tipo = tipo;
		  this.direccion = direccion;
		  this.numero = numero;
		  this.cp  = cp;
		  this.latitud = (latitud != null) ? Long.valueOf(String.format("%.7f", Double.valueOf(latitud)).replace(",", "")) : 0;
		  this.longitud = (longitud != null) ? Long.valueOf(String.format("%.7f", Double.valueOf(longitud)).replace(",", "")) : 0;
		  this.municipio = municipio;
		  this.plazas = plazas;
	  }
	  
	  public void actualizar (String nombre, String tipo, String direccion, String numero, String cp, long latitud, long longitud, String municipio, String plazas){
		  this.nombre = nombre;
		  this.tipo = tipo;
		  this.direccion = direccion;
		  this.numero = numero;
		  this.cp  = cp;
		  this.latitud = latitud;
		  this.longitud = longitud;
		  this.municipio = municipio;
		  this.plazas = plazas;
	  }

}