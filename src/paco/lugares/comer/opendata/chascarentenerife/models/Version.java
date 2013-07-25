package paco.lugares.comer.opendata.chascarentenerife.models;

import com.roscopeco.ormdroid.Column;
import com.roscopeco.ormdroid.Entity;

public class Version extends Entity{

	  @Column(primaryKey=true)
	  public int id;
	  public String version;
	  public String zona;

	  public Version(){
		 this(null, null);
	  }
	  
	  public Version(String version, String zona){
		  this.version = version;
		  this.zona = zona;
	  }

}