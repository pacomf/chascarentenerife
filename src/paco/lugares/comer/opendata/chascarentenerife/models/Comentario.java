package paco.lugares.comer.opendata.chascarentenerife.models;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Entity;
import static com.roscopeco.ormdroid.Query.eql;

public class Comentario extends Entity{

	  @Column(primaryKey=true)
	  public int id;
	  public String usuario, fecha, valoracion, precio, recomendacion, opinion;
	  
	  public Comentario(){
	     this(null, null, null, null, null, null);
      }
	  
	  public Comentario(String usuario, String fecha, String valoracion, String precio, String recomendacion, String opinion){
		  this.usuario = usuario;
		  this.fecha = fecha;
		  this.valoracion = valoracion;
		  this.precio = precio;
		  this.recomendacion = recomendacion;
		  this.opinion = opinion;
	  }


}