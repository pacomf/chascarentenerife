package paco.lugares.comer.opendata.chascarentenerife.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Entity;

public class Version extends Entity{

	  @Column(primaryKey=true)
	  public int id;
	  public String version;
	  public String zona;
	  // Fecha para que no este constantemente consultando al servidor cada vez que se inicia la app
	  public String fecha;

	  public Version(){
		 this(null, null);
	  }
	  
	  public Version(String version, String zona){
		  this.version = version;
		  this.zona = zona;
		  Calendar c = Calendar.getInstance(); 
		  c.setTime(new Date()); 
		  SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		  this.fecha = sdf.format(c.getTime());
	  }

}