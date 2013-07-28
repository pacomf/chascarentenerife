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
	     this(null, null, null, null, null, null, null, null, null, null, null, null);
      }
	  
	  public Establecimiento(String idserver, String nombre, String tipo, String direccion, String numero, String cp, String latitud, String longitud, String municipio, String plazas, String media, String precio){
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
		  if (idserver != null){
			  ValoracionEstablecimiento ve = Entity.query(ValoracionEstablecimiento.class).where(eql("idserver", idserver)).execute();
			  if (ve == null) {
				  media = (media != null) ? media : "0";
				  precio = (precio != null) ? precio : "0";
				  ve = new ValoracionEstablecimiento(idserver, media, precio);
				  ve.save();
		  	  } else {
		  		  if (media != null)
		  			  ve.actualizarMedia(media);
		  		  if (precio != null)
		  			  ve.actualizarPrecio(precio);
		  		  ve.save();
		  	  }
		  }
	  }
	  
	  public void actualizar (String nombre, String tipo, String direccion, String numero, String cp, long latitud, long longitud, String municipio, String plazas, String media, String precio){
		  this.nombre = nombre;
		  this.tipo = tipo;
		  this.direccion = direccion;
		  this.numero = numero;
		  this.cp  = cp;
		  this.latitud = latitud;
		  this.longitud = longitud;
		  this.municipio = municipio;
		  this.plazas = plazas;
		  ValoracionEstablecimiento ve = Entity.query(ValoracionEstablecimiento.class).where(eql("idserver", idserver)).execute();
		  if (ve == null) {
			  ve = new ValoracionEstablecimiento(idserver, media, precio);
			  ve.save();
	  	  } else {
	  		  ve.actualizarMedia(media);
	  		  ve.actualizarPrecio(precio);
	  		  ve.save();
	  	  }
	  }

}