package paco.lugares.comer.opendata.chascarentenerife.models;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Entity;

public class ValoracionEstablecimiento extends Entity{
	
	  @Column(primaryKey=true)
	  public int id;
	  public String idserver, media, precio;
	  
	  public ValoracionEstablecimiento(){
	     this(null, null, null);
      }
	  
	  public ValoracionEstablecimiento(String idserver, String media, String precio){
		  this.idserver = idserver;
		  this.media = media;
		  this.precio = precio;
	  }
	  
	  public void actualizarMedia (String media){
		  this.media = media;
	  }
	  
	  public void actualizarPrecio (String precio){
		  this.precio = precio;
	  }


}