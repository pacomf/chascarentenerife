package paco.lugares.comer.opendata.chascarentenerife.models;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Entity;

public class OpinionEstablecimiento extends Entity{

	  @Column(primaryKey=true)
	  public int id;
	  public String idserver;
	  public String valoracion, precio, nombre, recomendacion, opinion, fecha;
	  
	  public OpinionEstablecimiento(){
	     this(null, null, null, null, null, null, null);
      }
	  
	  public OpinionEstablecimiento(String idserver, String valoracion, String precio, String nombre, String recomendacion, String opinion, String fecha){
		  this.idserver = idserver;
		  this.valoracion = valoracion;
		  this.precio = precio;
		  this.nombre = nombre;
		  this.recomendacion = recomendacion;
		  this.opinion = opinion;
		  this.fecha = fecha;
	  }

}